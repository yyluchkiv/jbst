package jbst.ops.server.slack;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.files.FilesUploadRequest;
import jbst.ops.server.domain.slack.teams.SlackTeamEventContext;
import jbst.ops.server.domain.slack.teams.SlackTeamEventV1;
import jbst.ops.server.properties.configs.SlackConfigs;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class SlackClient {

    private final SlackConfigs slackConfigs;
    @Getter
    private final MethodsClient slackClient;

    public SlackClient(SlackConfigs slackConfigs) {
        this.slackConfigs = slackConfigs;
        this.slackClient = Slack.getInstance().methods(slackConfigs.getBotToken());
    }

    public final void sendDirectOrChannel(SlackTeamEventContext event) {
        this.sendMessage(event.message(), event.messageEvent().getChannel());
    }

    @Deprecated(forRemoval = true)
    public final void sendDirectOrChannelV2(SlackTeamEventV1 event) {
        if (this.slackConfigs.isDisabled()) {
            return;
        }
        var message = event.getMessage();
        var requestContext = event.getRequestContext();
        this.sendMessage(message, requestContext.getUserChannel());
    }

    public void sendCommunicationMain(SlackTeamEventV1 event) {
        if (this.slackConfigs.isDisabled()) {
            return;
        }
        var mainChannelCommunication = this.slackConfigs.getCommunication();
        if (mainChannelCommunication.isEnabled()) {
            var message = event.getMessage();
            var channel = mainChannelCommunication.getChannel();
            this.sendMessage(message, channel);
            if (event.isFilePresent()) {
                this.sendFile(event.getFileContent(), channel);
            }
        }
    }

    public void sendCommunicationTeam(SlackTeamEventV1 event) {
        if (this.slackConfigs.isDisabled()) {
            return;
        }
        var communication = event.getSlackTeamChannelCommunication().getCommunication();
        if (communication.isEnabled()) {
            var message = event.getMessage();
            var channel = communication.getChannel();
            this.sendMessage(message, channel);
            if (event.isFilePresent()) {
                this.sendFile(event.getFileContent(), channel);
            }
        }
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private void sendMessage(String message, String channel) {
        try {
            this.slackClient.chatPostMessage(
                    ChatPostMessageRequest.builder()
                            .text(message)
                            .channel(channel)
                            .build()
            );
        } catch (SlackApiException | IOException ex) {
            // ignore
        }
    }

    private void sendFile(String fileContent, String channel) {
        try {
            this.slackClient.filesUpload(
                    FilesUploadRequest.builder()
                            .filename("incident-trace")
                            .content(fileContent)
                            .channels(List.of(channel))
                            .build()
            );
        } catch (SlackApiException | IOException ex) {
            // ignore
        }
    }
}
