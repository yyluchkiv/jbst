package jbst.ops.server.domain.slack.requests;

import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.conversations.ConversationsInfoRequest;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import jbst.ops.server.domain.authorities.Permissions;
import jbst.ops.server.domain.keywords.ServiceKeywordCommandKey;
import jbst.ops.server.domain.keywords.SlackKeywords;
import jbst.ops.server.domain.slack.teams.SlackTeam;
import jbst.ops.server.properties.configs.Tech1SlackConfigs;
import jbst.ops.server.slack.services.SlackServiceTech1;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

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

    private final Permissions permissions;
    private SlackKeywords slackKeywords;

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
        this.permissions = new Permissions();
    }

    public SlackRequestContext(SlackTeam slackTeam, AppMentionEvent event) {
        this.slackTeam = slackTeam;
        this.username = this.getSlackUsername(event.getUser());
        this.userChannel = event.getChannel();
        this.rawContent = event.getText();
        this.isDirect = false;
        this.permissions = new Permissions();
    }

    public SlackRequestContext(SlackTeam slackTeam, MessageEvent event) {
        this.slackTeam = slackTeam;
        this.username = this.getSlackUsername(event.getUser());
        this.userChannel = event.getChannel();
        this.rawContent = event.getText();
        this.isDirect = true;
        this.permissions = new Permissions();
    }

    public void addSlackKeywords(SlackServiceTech1 slackServiceTech1, Tech1SlackConfigs tech1SlackConfigs, SlackKeywords slackKeywords) throws SlackApiException, IOException {
        this.slackKeywords = slackKeywords;

        var conversationsInfo = slackServiceTech1.getSlackClient().conversationsInfo(
                ConversationsInfoRequest.builder()
                        .channel(this.userChannel)
                        .build()
        );
        var channelName = conversationsInfo.getChannel().getName();

        var communication = tech1SlackConfigs.getCommunication();
        if (!this.isDirect && communication.isEnabled() && communication.getChannel().equals(channelName)) {
            this.permissions.addFoundersPermission();
        }
        if (!this.isDirect) {
            var teamOpt = tech1SlackConfigs.getTeamBy(channelName);
            teamOpt.ifPresent(teamCommunication -> this.permissions.addTeamPermission(teamCommunication.getTeam()));
        }
    }

    public boolean hasAnyPermissions() {
        return nonNull(this.permissions) && !isEmpty(this.permissions.getAccesses());
    }

    public boolean hasAccess(ServiceKeywordCommandKey cmdKey) {
        if (isNull(cmdKey)) {
            return false;
        }
        var service = cmdKey.service();
        var keywordCommand = cmdKey.keywordCommand();
        if (isNull(service) || isNull(keywordCommand) || isNull(this.slackKeywords)) {
            return false;
        }
        return this.slackKeywords.hasAccess(service, this.permissions.getAccesses()) &&
                this.slackKeywords.isEnabled(service, keywordCommand);
    }

    public ServiceKeywordCommandKey cmdKey() {
        return new ServiceKeywordCommandKey(
                this.slackKeywords.getServiceKeywordCommand().getService(),
                this.slackKeywords.getServiceKeywordCommand().getKeywordCommand()
        );
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private String getSlackUsername(String username) {
        return "<@" + username + ">";
    }
}
