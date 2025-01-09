package jbst.ops.server.domain.keywords;

import com.slack.api.model.event.AppMentionEvent;
import jbst.ops.server.domain.authorities.Permission;
import jbst.ops.server.properties.atomics.Service;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static java.util.Collections.disjoint;
import static java.util.Objects.nonNull;
import static jbst.ops.server.domain.keywords.KeywordCommandAttributes.enabledDefault;

@Deprecated
@Slf4j
// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class SlackKeywords {
    private final String userKeyword;
    private final String[] keywords;

    private ServiceKeywordCommand serviceKeywordCommand;
    private KeywordCommandAttributes attributes;

    public SlackKeywords(AppMentionEvent event) {
        var eventText = event.getText();
        LOGGER.debug("User keyword before cleaning: `{}`", eventText);
        // https://stackoverflow.com/questions/19166426/replace-all-text-between-braces-in-java-with-regex/19169486
        // WARNING: good place to refactor
        eventText = eventText.replaceAll("\\<.*?\\>", "").trim();
        LOGGER.debug("User keyword after cleaning: `{}`", eventText);
        this.userKeyword = eventText;
        this.keywords = eventText.split(" ", 2);
    }

    public void setServiceKeywordCommandAsDefaultEnabled(ServiceKeywordCommand serviceKeywordCommand) {
        this.serviceKeywordCommand = serviceKeywordCommand;
        this.attributes = enabledDefault();
    }

    public void setServiceKeywordCommandBy(ServiceKeywordCommand serviceKeywordCommand, KeywordCommandAttributes attributes) {
        this.serviceKeywordCommand = serviceKeywordCommand;
        this.attributes = attributes;
    }

    public boolean hasAccess(Service service, List<Permission> permissions) {
        if (nonNull(service)) {
            return service.equals(this.serviceKeywordCommand.getService()) &&
                    !disjoint(permissions, this.serviceKeywordCommand.getPermissions()) &&
                    this.attributes.isEnabled();
        } else {
            return false;
        }
    }

    public boolean isEnabled(Service service, KeywordCommand keywordCommand) {
        if (nonNull(service) && nonNull(keywordCommand)) {
            return service.equals(this.serviceKeywordCommand.getService()) &&
                    keywordCommand.equals(this.serviceKeywordCommand.getKeywordCommand()) &&
                    this.attributes.isEnabled();
        } else {
            return false;
        }
    }
}
