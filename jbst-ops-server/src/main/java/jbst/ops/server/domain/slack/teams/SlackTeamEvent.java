package jbst.ops.server.domain.slack.teams;

import jbst.foundation.domain.tuples.Tuple2;
import jbst.ops.server.domain.slack.messages.SlackMessageType;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.properties.atomics.SlackTeamChannelCommunication;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class SlackTeamEvent {
    private final SlackRequestContext requestContext;
    private final SlackMessageType messageType;
    private final String message;

    // OPTIONAL: depends on type == SlackMessageType.COMMUNICATION_TEAM
    private SlackTeamChannelCommunication slackTeamChannelCommunication;

    // OPTIONAL: used only in incidents (trace)
    private boolean filePresent;
    private String fileContent;

    public SlackTeamEvent(
            @NotNull SlackRequestContext requestContext,
            @NotNull SlackMessageType messageType,
            @NotNull String message
    ) {
        this.requestContext = requestContext;
        this.messageType = messageType;
        this.message = message;
        this.filePresent = false;
    }

    public static List<SlackTeamEvent> events(
            SlackRequestContext requestContext,
            SlackMessageType type,
            List<String> messages
    ) {
        return messages.stream()
                .map(message -> new SlackTeamEvent(requestContext, type, message))
                .toList();
    }

    public static SlackTeamEvent directSlackMessage(
            SlackRequestContext requestContext,
            String message
    ) {
        return new SlackTeamEvent(
                requestContext,
                SlackMessageType.DIRECT,
                message
        );
    }

    public static SlackTeamEvent channelSlackMessage(
            SlackRequestContext requestContext,
            String message
    ) {
        return new SlackTeamEvent(
                requestContext,
                SlackMessageType.CHANNEL,
                message
        );
    }

    public static List<SlackTeamEvent> channelSlackMessages(
            SlackRequestContext requestContext,
            List<String> messages
    ) {
        return messages.stream()
                .map(message -> channelSlackMessage(requestContext, message))
                .toList();
    }

    public static SlackTeamEvent communicationMainSlackMessage(
            SlackRequestContext requestContext,
            String text
    ) {
        return new SlackTeamEvent(
                requestContext,
                SlackMessageType.COMMUNICATION_MAIN,
                text
        );
    }

    public static SlackTeamEvent communicationMainSlackIncident(
            SlackRequestContext requestContext,
            Tuple2<String, String> incidentTuple
    ) {
        var event = new SlackTeamEvent(
                requestContext,
                SlackMessageType.COMMUNICATION_MAIN,
                incidentTuple.a()
        );
        event.filePresent = true;
        event.fileContent = incidentTuple.b();
        return event;
    }

    public static SlackTeamEvent communicationTeamSlackMessage(
            SlackRequestContext requestContext,
            String text,
            SlackTeamChannelCommunication slackTeamChannelCommunication
    ) {
        var instance = new SlackTeamEvent(
                requestContext,
                SlackMessageType.COMMUNICATION_TEAM,
                text
        );
        instance.slackTeamChannelCommunication = slackTeamChannelCommunication;
        return instance;
    }

    public static SlackTeamEvent communicationTeamSlackIncident(
            SlackRequestContext requestContext,
            Tuple2<String, String> incidentTuple,
            SlackTeamChannelCommunication slackTeamChannelCommunication
    ) {
        var event = new SlackTeamEvent(
                requestContext,
                SlackMessageType.COMMUNICATION_TEAM,
                incidentTuple.a()
        );
        event.filePresent = true;
        event.fileContent = incidentTuple.b();
        event.slackTeamChannelCommunication = slackTeamChannelCommunication;
        return event;
    }
}
