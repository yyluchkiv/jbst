package jbst.ops.server.domain.slack.requests;

import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import jbst.ops.server.domain.slack.teams.SlackTeam;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Deprecated(forRemoval = true)
// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class SlackRequestContext {
    private final SlackTeam slackTeam;

    private String username;
    private String userChannel;
    private String rawContent;
    private boolean isDirect;

    // WARNING: architecture how to avoid `limited` methods
    public static SlackRequestContext limitedTech1() {
        return new SlackRequestContext(SlackTeam.TECH1);
    }

    // WARNING: architecture how to avoid `limited` methods
    public static SlackRequestContext limitedSmartApps() {
        return new SlackRequestContext(SlackTeam.SMART_APPS);
    }

    private SlackRequestContext(SlackTeam slackTeam) {
        this.slackTeam = slackTeam;
    }

    public SlackRequestContext(SlackTeam slackTeam, AppMentionEvent event) {
        this.slackTeam = slackTeam;
        this.username = this.getSlackUsername(event.getUser());
        this.userChannel = event.getChannel();
        this.rawContent = event.getText();
        this.isDirect = false;
    }

    public SlackRequestContext(SlackTeam slackTeam, MessageEvent event) {
        this.slackTeam = slackTeam;
        this.username = this.getSlackUsername(event.getUser());
        this.userChannel = event.getChannel();
        this.rawContent = event.getText();
        this.isDirect = true;
    }

    // TODO [YYL] fixme

//    public void addSlackKeywords(SlackServiceTech1 slackServiceTech1, Tech1SlackConfigs tech1SlackConfigs, SlackKeywords slackKeywords) throws SlackApiException, IOException {
//        this.slackKeywords = slackKeywords;
//
//        var conversationsInfo = slackServiceTech1.getSlackClient().conversationsInfo(
//                ConversationsInfoRequest.builder()
//                        .channel(this.userChannel)
//                        .build()
//        );
//        var channelName = conversationsInfo.getChannel().getName();
//
//        var communication = tech1SlackConfigs.getCommunication();
//        if (!this.isDirect && communication.isEnabled() && communication.getChannel().equals(channelName)) {
//            this.permissions.addFoundersPermission();
//        }
//        if (!this.isDirect) {
//            var teamOpt = tech1SlackConfigs.getTeamBy(channelName);
//            teamOpt.ifPresent(teamCommunication -> this.permissions.addTeamPermission(teamCommunication.getTeam()));
//        }
//    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private String getSlackUsername(String username) {
        return "<@" + username + ">";
    }
}
