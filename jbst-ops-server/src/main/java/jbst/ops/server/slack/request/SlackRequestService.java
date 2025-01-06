package jbst.ops.server.slack.request;

import com.slack.api.model.event.AppMentionEvent;
import jbst.ops.server.domain.keywords.SlackKeywords;

public interface SlackRequestService {
    SlackKeywords getSlackKeywords(AppMentionEvent event);
}
