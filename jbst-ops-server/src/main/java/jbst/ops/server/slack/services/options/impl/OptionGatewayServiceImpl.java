package jbst.ops.server.slack.services.options.impl;

import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.slack.SlackMessagingService;
import jbst.ops.server.slack.services.options.OptionFileSystemService;
import jbst.ops.server.slack.services.options.OptionGatewayService;
import jbst.ops.server.slack.services.options.OptionMonitoringService;
import jbst.ops.server.utils.MessagesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static java.util.Collections.disjoint;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.TAB;
import static jbst.foundation.utilities.slack.SlackUtility.getSlackMessage;
import static jbst.ops.server.constants.OpsConstants.Services.*;
import static jbst.ops.server.domain.slack.teams.SlackTeamEvent.channelSlackMessage;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OptionGatewayServiceImpl implements OptionGatewayService {

    // Services
    private final OptionFileSystemService optionFileSystemService;
    private final OptionMonitoringService optionMonitoringService;
    // Messaging
    private final SlackMessagingService slackMessagingService;
    // Utilities
    private final MessagesUtils messagesUtils;
    // Properties
    private final OpsProperties opsProperties;

    @Override
    public void sendHelp(SlackRequestContext slackRequestContext) {
        var accesses = slackRequestContext.getPermissions().getAccesses();
        var sb = new StringBuilder();
        var keywordsConfig = opsProperties.getKeywordsConfigs();
        var cmdFormat = "%-20s %10s";
        keywordsConfig.getServices().forEach((service, serviceConfig) -> {
            var commands = serviceConfig.getCommands();
            var servicePermissions = commands.entrySet().stream()
                    .flatMap(entry -> entry.getValue().getPermissions().stream())
                    .collect(Collectors.toSet());

            // WARNING: permissions have at least one in common
            if (!disjoint(accesses, servicePermissions)) {
                sb.append(serviceConfig.getRootCmd()).append(":").append(NEWLINE);
                commands.forEach((keyword, command) -> {
                    // WARNING: permissions have at least one in common
                    if (!disjoint(accesses, command.getPermissions())) {
                        sb.append(TAB).append(String.format(cmdFormat, command.getKey() + ":", command.getDescription())).append(NEWLINE);
                    }
                });
            }
        });
        this.slackMessagingService.sendAsync(
                channelSlackMessage(
                        slackRequestContext,
                        this.messagesUtils.getHelp() + NEWLINE + getSlackMessage(sb.toString().trim())
                )
        );
    }

    @Override
    public void sendGatewayStatus(SlackRequestContext slackRequestContext) {
        try {
            this.optionMonitoringService.sendShow(slackRequestContext);
        } catch (RuntimeException ex) {
            this.slackMessagingService.sendAsync(
                    channelSlackMessage(
                            slackRequestContext,
                            this.messagesUtils.getServiceMessage(true, MONITORING_SERVICE)
                    )
            );
        }
    }

    @Override
    public void sendSpringBootActuatorsGatewayStatus(SlackRequestContext slackRequestContext) {
        try {
            this.optionMonitoringService.sendSpringBootActuators(slackRequestContext);
        } catch (RuntimeException ex) {
            this.slackMessagingService.sendAsync(
                    channelSlackMessage(
                            slackRequestContext,
                            this.messagesUtils.getServiceMessage(true, SPRING_BOOT_ACTUATOR_SERVICE)
                    )
            );
        }
    }

    @Override
    public void sendFsGatewayStatus(SlackRequestContext slackRequestContext) {
        try {
            this.optionFileSystemService.sendFsStatusOnSshRequiredAnyProblemsOnFsMetadata(slackRequestContext);
        } catch (RuntimeException ex) {
            this.slackMessagingService.sendAsync(
                    channelSlackMessage(
                            slackRequestContext,
                            this.messagesUtils.getServiceMessage(true, FILE_SYSTEM_SERVICE)
                    )
            );
        }
    }
}
