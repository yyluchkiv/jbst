package jbst.ops.server.slack.request;

import com.slack.api.model.event.AppMentionEvent;
import jakarta.annotation.PostConstruct;
import jbst.ops.server.domain.keywords.KeywordCommand;
import jbst.ops.server.domain.keywords.ServiceKeywordCommand;
import jbst.ops.server.domain.keywords.SlackKeywords;
import jbst.ops.server.exceptions.SlackRuntimeException;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.properties.atomics.Service;
import jbst.ops.server.utils.MessagesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static jbst.ops.server.domain.keywords.KeywordCommand.BY_ID;
import static jbst.ops.server.properties.atomics.Service.*;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SlackRequestService {
    private static final List<KeywordCommand> EXCLUDED_KEYWORDS_COMMANDS = List.of(BY_ID);

    // Utilities
    private final MessagesUtils messagesUtils;
    // Properties
    private final OpsProperties opsProperties;

    // @PostConstruct
    private final Map<String, Consumer<SlackKeywords>> rootCommands = new HashMap<>();
    private final Map<ServiceKeywordCommand, Consumer<SlackKeywords>> nestedCommands = new HashMap<>();

    @PostConstruct
    public void postConstruct() {
        // keywords
        var keywordsConfig = this.opsProperties.getKeywordsConfigs();
        var services = keywordsConfig.getServices();

        // root commands
        rootCommands.put(services.get(GATEWAY).getRootCmd(), slackKeywords -> this.processTwoParamsCmd(slackKeywords, GATEWAY));
//        rootCommands.put(services.get(LOGS).getRootCmd(), this::processLogs);
        rootCommands.put(services.get(MONITORING).getRootCmd(), slackKeywords -> this.processTwoParamsCmd(slackKeywords, MONITORING));
        rootCommands.put(services.get(FS).getRootCmd(), slackKeywords -> this.processTwoParamsCmd(slackKeywords, FS));

        // nested commands
        services.forEach((service, serviceConfig) -> {
            var commands = serviceConfig.getCommands();
            commands.forEach((keywordCommand, command) -> {
                var serviceKeywordCommand = ServiceKeywordCommand.of(
                        service,
                        keywordCommand,
                        command.getPermissions()
                );
                if (!EXCLUDED_KEYWORDS_COMMANDS.contains(keywordCommand)) {
                    serviceKeywordCommand.setKey(command.getKey());
                }
                this.nestedCommands.put(serviceKeywordCommand, slackKeywords -> {
                    LOGGER.debug("Execute keywords cmd: `{}`", serviceKeywordCommand);
                    if (!EXCLUDED_KEYWORDS_COMMANDS.contains(serviceKeywordCommand.getKeywordCommand())) {
                        slackKeywords.setServiceKeywordCommandAsDefaultEnabled(serviceKeywordCommand);
                    } else {
                        // NOTE: attributes was already present
                        var attributes = slackKeywords.getAttributes();
                        LOGGER.debug("Execute keywords cmd. Attributes: `{}`", attributes);
                    }
                });
            });
        });
    }

    public SlackKeywords getSlackKeywords(AppMentionEvent event) {
        // Find Type
        var slackKeywords = new SlackKeywords(event);

        var rootCmdConsumerOpt = this.rootCommands.entrySet().stream()
                .filter(entry -> slackKeywords.getUserKeyword().startsWith(entry.getKey()))
                .findFirst();

        if (rootCmdConsumerOpt.isPresent()) {
            rootCmdConsumerOpt.get().getValue().accept(slackKeywords);
        } else {
            LOGGER.warn("Authorized request, but invalid user keyword: {}", slackKeywords.getUserKeyword());
            throw new SlackRuntimeException(this.messagesUtils.getUnexpectedWarning());
        }
        return slackKeywords;
    }

    // ================================================================================================================
    // Private Methods: Process By Type
    // ================================================================================================================
    private void processTwoParamsCmd(SlackKeywords slackKeywords, Service service) {
        if (slackKeywords.getKeywords().length != 2) {
            throw new SlackRuntimeException(this.messagesUtils.getUnexpectedWarning());
        } else {
            var keyword = slackKeywords.getKeywords()[1];
            var keywordOpt = this.getNestedCommandBy(service, keyword);
            if (keywordOpt.isPresent()) {
                keywordOpt.get().getValue().accept(slackKeywords);
            } else {
                throw new SlackRuntimeException(this.messagesUtils.getUnexpectedWarning());
            }
        }
    }

//    private void processLogs(SlackKeywords slackKeywords) {
//        var keywordOpt = this.getNestedCommandBy(LOGS, "<Any>");
//        if (keywordOpt.isPresent()) {
//            // logs attributes ($serverId)
//            var action = keywordOpt.get();
//            var actionKey = action.getKey();
//            if (EXCLUDED_KEYWORDS_COMMANDS.contains(actionKey.getKeywordCommand())) {
//                var attributes = KeywordCommandAttributes.server(Integer.valueOf(slackKeywords.getKeywords()[1]));
//                slackKeywords.setServiceKeywordCommandBy(actionKey, attributes);
//            }
//            keywordOpt.get().getValue().accept(slackKeywords);
//        } else {
//            throw new SlackRuntimeException(this.messagesUtils.getUnexpectedWarning());
//        }
//    }

    private Optional<Map.Entry<ServiceKeywordCommand, Consumer<SlackKeywords>>> getNestedCommandBy(Service service, String keyword) {
        var filteredByService = this.nestedCommands.entrySet().stream()
                .filter(entry -> service.equals(entry.getKey().getService()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var cmdOpt = filteredByService.entrySet().stream()
                .filter(entry -> nonNull(keyword) && keyword.equals(entry.getKey().getKey()))
                .findFirst();

        if (cmdOpt.isPresent()) {
            return cmdOpt;
        } else {
            return filteredByService.entrySet().stream()
                    .filter(entry -> EXCLUDED_KEYWORDS_COMMANDS.contains(entry.getKey().getKeywordCommand()))
                    .findFirst();
        }
    }
}
