package jbst.ops.server.slack.services.keywords.impl;

import jbst.ops.server.domain.keywords.ServiceKeywordCommandKey;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.exceptions.SlackRuntimeException;
import jbst.ops.server.slack.services.keywords.KeywordsService;
import jbst.ops.server.utils.MessagesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static jbst.ops.server.utilities.MessagesUtility.getUnexpectedWarning;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KeywordsServiceImpl implements KeywordsService {

    // Utilities
    private final MessagesUtils messagesUtils;

    @Override
    public void sendMessagesBy(SlackRequestContext slackRequestContext, Map<ServiceKeywordCommandKey, Consumer<SlackRequestContext>> configs) {
        var entryOpt = configs.entrySet().stream()
                .filter(entry -> nonNull(entry.getKey()))
                .filter(entry -> slackRequestContext.cmdKey().equals(entry.getKey()))
                .filter(entry -> slackRequestContext.hasAccess(entry.getKey()))
                .findFirst();
        if (entryOpt.isPresent()) {
            entryOpt.get().getValue().accept(slackRequestContext);
        } else {
            throw new SlackRuntimeException(getUnexpectedWarning());
        }
    }
}
