package jbst.ops.server.slack;

import com.slack.api.model.event.AppMentionEvent;
import jbst.ops.server.domain.keywords.ServiceKeywordCommand;
import jbst.ops.server.domain.keywords.SlackKeywords;
import jbst.ops.server.exceptions.SlackRuntimeException;
import jbst.ops.server.properties.atomics.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static jbst.ops.server.utilities.MessagesUtility.getUnexpectedWarning;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SlackRequestService {
    // @PostConstruct
    private final Map<String, Consumer<SlackKeywords>> rootCommands = new HashMap<>();
    private final Map<ServiceKeywordCommand, Consumer<SlackKeywords>> nestedCommands = new HashMap<>();

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
            throw new SlackRuntimeException(getUnexpectedWarning());
        }
        return slackKeywords;
    }

    // ================================================================================================================
    // Private Methods: Process By Type
    // ================================================================================================================
    private void processTwoParamsCmd(SlackKeywords slackKeywords, Service service) {
        if (slackKeywords.getKeywords().length != 2) {
            throw new SlackRuntimeException(getUnexpectedWarning());
        } else {
            var keyword = slackKeywords.getKeywords()[1];
            var keywordOpt = this.getNestedCommandBy(service, keyword);
            if (keywordOpt.isPresent()) {
                keywordOpt.get().getValue().accept(slackKeywords);
            } else {
                throw new SlackRuntimeException(getUnexpectedWarning());
            }
        }
    }

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
            throw new RuntimeException("TODO FIX ME");
        }
    }
}
