package jbst.foundation.feigns.slack;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesFixed;
import jbst.foundation.domain.concurrent.JbstSleep;
import jbst.foundation.feigns.slack.JbstSlack.ClientException;
import jbst.foundation.feigns.slack.JbstSlack.ConfigurationException;
import jbst.foundation.feigns.slack.JbstSlack.RateLimitsException;
import jbst.foundation.feigns.slack.JbstSlack.UnexpectedDisabledMessageReqException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;

@Slf4j
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
class JbstSlackTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesFixed.class
    })
    static class TestConfiguration {

    }

    private static final String SLACK_TOKEN = "-";
    private static final String SLACK_CHANNEL = "-";

    private final JbstSlack slack = new JbstSlack();

    public JbstSlackTest() {
        this.slack.initPragmatic(SLACK_TOKEN);
        this.slack.start();
    }

    @Disabled
    @Test
    void messageSend() throws ConfigurationException, UnexpectedDisabledMessageReqException, RateLimitsException, ClientException {
        // Arrange
        var message = JbstSlack.ChatMessageReq.messageSend(
                JbstSlack.ChatMessageDestination.enabled(SLACK_CHANNEL),
                "<@username> <b>text</b>, timestamp: " + getCurrentTimestamp()
        );

        // Act
        var res = this.slack.messageSend(message);

        // Assert
        LOGGER.info("jbst-slack res@send: {}@", res);
    }

    @Disabled
    @Test
    void messageEdit() throws ConfigurationException, UnexpectedDisabledMessageReqException, RateLimitsException, ClientException {
        // Arrange
        var message = JbstSlack.ChatMessageReq.messageEdit(
                JbstSlack.ChatMessageDestination.enabled(SLACK_CHANNEL),
                "<@username> <b>text</b>, timestamp: (edited)",
                new JbstSlack.MessageTs("1764601641.615379")
        );

        // Act
        var res = this.slack.messageEdit(message);

        // Assert
        LOGGER.info("jbst-slack res@edit: {}", res);
    }

    @Disabled
    @Test
    void messagesBackpressureSend() {
        // Arrange
        var step = 20;
        var messages1 = IntStream.range(0, step)
                .mapToObj(i -> JbstSlack.ChatMessageReq.messageSend(
                        JbstSlack.ChatMessageDestination.enabled(SLACK_CHANNEL),
                        "<@username> <b>" + i + "</b>"
                ))
                .toList();
        var messages2 = IntStream.range(step, step * 2)
                .mapToObj(i -> JbstSlack.ChatMessageReq.messageSend(
                        JbstSlack.ChatMessageDestination.enabled(SLACK_CHANNEL),
                        "<@username> <b>" + i + "</b>"
                ))
                .toList();

        // Act
        for (var req : messages1) {
            this.slack.submitMessage(req);
        }
        this.slack.submitMessages(messages2);

        // Assert
        JbstSleep.sleep(30, TimeUnit.SECONDS);
    }

    @Disabled
    @Test
    void messagesBackpressureEdit() throws ConfigurationException, UnexpectedDisabledMessageReqException, RateLimitsException, ClientException {
        // Act: send
        var messageSend = JbstSlack.ChatMessageReq.messageSend(
                JbstSlack.ChatMessageDestination.enabled(SLACK_CHANNEL),
                "index: 0"
        );
        var res = this.slack.messageSend(messageSend);
        JbstSleep.sleep(1, TimeUnit.SECONDS);

        // Act: edit
        var messagesEdit = IntStream.range(1, 10000)
                .mapToObj(i ->
                        JbstSlack.ChatMessageReq.messageEdit(
                                JbstSlack.ChatMessageDestination.enabled(SLACK_CHANNEL),
                                "index: %s".formatted(i), res.ts()
                        )
                )
                .toList();
        this.slack.submitMessages(messagesEdit);
        JbstSleep.sleep(5, TimeUnit.SECONDS);
    }
}
