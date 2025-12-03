package jbst.foundation.feigns.slack;

import jbst.foundation.configurations.JbstConfigurationFeignClientSlack;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.time.TimeAmount;
import jbst.foundation.domain.concurrent.JbstSleep;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

@Slf4j
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
class SlackClientTest {

    @Configuration
    @Import({
            JbstConfigurationFeignClientSlack.class,
            TestJbstConfigurationPropertiesHardcoded.class
    })
    static class TestConfiguration {

    }

    private static final String SLACK_TOKEN = "-";
    private static final String SLACK_CHANNEL = "-";

    private final SlackClient slackClient;

    @Autowired
    public SlackClientTest(SlackClient slackClient) {
        this.slackClient = slackClient;
        this.slackClient.configure(new SlackClient.JbstSlackConfiguration(
                SLACK_TOKEN,
                new TimeAmount(250, ChronoUnit.MILLIS)
        ));
    }

    @Disabled
    @Test
    void sendMessage() throws SlackClient.JbstSlackException {
        // Arrange
        var message = new SlackClient.JbstSlackChatMessage(
                SLACK_CHANNEL,
                "<@username> <b>text</b>, timestamp: " + getCurrentTimestamp()
        );

        // Act
        var ts = this.slackClient.sendMessage(message);

        // Assert
        LOGGER.info("slack-client ts@send: {}@", ts);
    }

    @Disabled
    @Test
    void editMessage() throws SlackClient.JbstSlackException {
        // Arrange
        var ts = new SlackClient.JbstSlackMessageTs("1764601641.615379");
        var message = new SlackClient.JbstSlackChatMessage(
                SLACK_CHANNEL,
                "<@username> <b>text</b>, timestamp: (edited)"
        );

        // Act
        ts = this.slackClient.editMessage(ts, message);

        // Assert
        LOGGER.info("slack-client ts@edit: {}", ts);
    }

    @Disabled
    @Test
    void submitMessagesBackpressure() {
        // Arrange
        var messages1 = IntStream.range(0, 20)
                .mapToObj(i -> new SlackClient.JbstSlackChatMessage(
                        SLACK_CHANNEL,
                        "<@username> <b>" + i + "</b>"
                ))
                .toList();
        var messages2 = IntStream.range(20, 40)
                .mapToObj(i -> new SlackClient.JbstSlackChatMessage(
                        SLACK_CHANNEL,
                        "<@username> <b>" + i + "</b>"
                ))
                .toList();

        // Act
        for (var req : messages1) {
            this.slackClient.submitMessage(req);
        }
        this.slackClient.submitMessages(messages2);

        // Assert
        JbstSleep.sleep(45, TimeUnit.SECONDS);
    }
}
