package jbst.ops.server.slack.services.options.impl;

import jakarta.annotation.PostConstruct;
import jbst.ops.server.domain.keywords.ServiceKeywordCommandKey;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.exceptions.SlackRuntimeException;
import jbst.ops.server.slack.services.keywords.KeywordsService;
import jbst.ops.server.slack.services.options.OptionFileSystemService;
import jbst.ops.server.slack.services.options.OptionGatewayService;
import jbst.ops.server.slack.services.options.OptionMonitoringService;
import jbst.ops.server.slack.services.options.OptionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static jbst.ops.server.domain.keywords.KeywordCommand.*;
import static jbst.ops.server.properties.atomics.Service.FS;
import static jbst.ops.server.properties.atomics.Service.*;
import static jbst.ops.server.utilities.MessagesUtility.getUnexpectedWarning;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OptionsServiceImpl implements OptionsService {

    // Keywords
    private final KeywordsService keywordsService;
    // Services
    private final OptionGatewayService optionGatewayService;
    private final OptionMonitoringService optionMonitoringService;
    private final OptionFileSystemService optionFileSystemService;

    private final Map<ServiceKeywordCommandKey, Consumer<SlackRequestContext>> optionsConfigs = new HashMap<>();

    @PostConstruct
    public void postConstruct() {
        this.gatewayConfigs();
        this.monitoringConfigs();
        this.fsConfigs();
    }

    @Override
    public void sendMessagesBy(SlackRequestContext slackRequestContext) {
        this.keywordsService.sendMessagesBy(slackRequestContext, this.optionsConfigs);
    }

    @Override
    public void sendFallbackMessage(SlackRequestContext slackRequestContext) {
        if (slackRequestContext.hasAnyPermissions()) {
            this.optionGatewayService.sendHelp(slackRequestContext);
        }
    }

    // ================================================================================================================
    // Private Methods
    // ================================================================================================================
    private void gatewayConfigs() {
         this.optionsConfigs.put(new ServiceKeywordCommandKey(GATEWAY, HELP), this.optionGatewayService::sendHelp);

        this.optionsConfigs.put(new ServiceKeywordCommandKey(GATEWAY, STATUS), slackRequestContext -> {
            this.optionGatewayService.sendHelp(slackRequestContext);

            this.optionGatewayService.sendSpringBootActuatorsGatewayStatus(slackRequestContext);
            this.optionGatewayService.sendGatewayStatus(slackRequestContext);
            this.optionGatewayService.sendFsGatewayStatus(slackRequestContext);
        });
    }

//    @SuppressWarnings("unused")
//    private void logsConfigs() {
//        this.optionsConfigs.put(new ServiceKeywordCommandKey(LOGS, BY_ID), slackContext -> {
//            var permissions = slackContext.getPermissions();
//            if (permissions.containsFounders()) {
//                this.optionLogService.logs(slackContext);
//            } else if (permissions.containsTeam()) {
//                this.optionLogService.logs(slackContext, permissions.getTeam());
//            } else {
//                throw new SlackRuntimeException(this.messagesUtils.getUnexpectedWarning());
//            }
//        });
//    }

    private void monitoringConfigs() {
        this.optionsConfigs.put(new ServiceKeywordCommandKey(MONITORING, SHOW), slackRequestContext -> {
            var permissions = slackRequestContext.getPermissions();
            if (permissions.containsFounders()) {
                this.optionMonitoringService.sendShow(slackRequestContext);
            } else if (permissions.containsTeam()) {
                this.optionMonitoringService.sendShow(slackRequestContext, permissions.getTeam());
            } else {
                throw new SlackRuntimeException(getUnexpectedWarning());
            }
        });

        this.optionsConfigs.put(
                new ServiceKeywordCommandKey(MONITORING, ACTUATORS),
                this.optionMonitoringService::sendSpringBootActuators
        );

        this.optionsConfigs.put(
                new ServiceKeywordCommandKey(MONITORING, RELOAD),
                this.optionMonitoringService::sendReload
        );
    }

    private void fsConfigs() {
        this.optionsConfigs.put(
                new ServiceKeywordCommandKey(FS, SHOW),
                this.optionFileSystemService::sendFsTables
        );
    }
}
