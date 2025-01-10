package jbst.ops.server.domain.slack.teams;

import jbst.foundation.domain.tuples.Tuple2;
import jbst.ops.server.domain.slack.messages.SlackMessageType;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class SlackTeamEventV1 {
    private final SlackRequestContext requestContext;
    private final SlackMessageType messageType;
    private final String message;

    // OPTIONAL: depends on type == SlackMessageType.COMMUNICATION_TEAM
//    private SlackTeamChannelCommunication slackTeamChannelCommunication;

    // OPTIONAL: used only in incidents (trace)
    private boolean filePresent;
    private String fileContent;

    public SlackTeamEventV1(
            @NotNull SlackRequestContext requestContext,
            @NotNull SlackMessageType messageType,
            @NotNull String message
    ) {
        this.requestContext = requestContext;
        this.messageType = messageType;
        this.message = message;
        this.filePresent = false;
    }

    public static List<SlackTeamEventV1> events(
            SlackRequestContext requestContext,
            SlackMessageType type,
            List<String> messages
    ) {
        return messages.stream()
                .map(message -> new SlackTeamEventV1(requestContext, type, message))
                .toList();
    }

    public static SlackTeamEventV1 channelSlackMessage(
            SlackRequestContext requestContext,
            String message
    ) {
        return new SlackTeamEventV1(
                requestContext,
                SlackMessageType.CHANNEL,
                message
        );
    }

    public static List<SlackTeamEventV1> channelSlackMessages(
            SlackRequestContext requestContext,
            List<String> messages
    ) {
        return messages.stream()
                .map(message -> channelSlackMessage(requestContext, message))
                .toList();
    }

    public static SlackTeamEventV1 communicationMainSlackMessage(
            SlackRequestContext requestContext,
            String text
    ) {
        return new SlackTeamEventV1(
                requestContext,
                SlackMessageType.COMMUNICATION_MAIN,
                text
        );
    }

    public static SlackTeamEventV1 communicationMainSlackIncident(
            SlackRequestContext requestContext,
            Tuple2<String, String> incidentTuple
    ) {
        var event = new SlackTeamEventV1(
                requestContext,
                SlackMessageType.COMMUNICATION_MAIN,
                incidentTuple.a()
        );
        event.filePresent = true;
        event.fileContent = incidentTuple.b();
        return event;
    }

    public static SlackTeamEventV1 communicationTeamSlackMessage(
            SlackRequestContext requestContext,
            String text
    ) {
        var instance = new SlackTeamEventV1(
                requestContext,
                SlackMessageType.COMMUNICATION_TEAM,
                text
        );
//        instance.slackTeamChannelCommunication = slackTeamChannelCommunication;
        return instance;
    }

    public static SlackTeamEventV1 communicationTeamSlackIncident(
            SlackRequestContext requestContext,
            Tuple2<String, String> incidentTuple
    ) {
        var event = new SlackTeamEventV1(
                requestContext,
                SlackMessageType.COMMUNICATION_TEAM,
                incidentTuple.a()
        );
        event.filePresent = true;
        event.fileContent = incidentTuple.b();
//        event.slackTeamChannelCommunication = slackTeamChannelCommunication;
        return event;
    }
}
