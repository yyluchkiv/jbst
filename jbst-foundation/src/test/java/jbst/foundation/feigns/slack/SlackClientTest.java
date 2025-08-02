package jbst.foundation.feigns.slack;

import jbst.foundation.configurations.JbstConfigurationFeignClientSlack;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.time.TimeAmount;
import jbst.foundation.utilities.concurrent.SleepUtility;
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

    private static final String SLACK_TOKEN = "<?>";
    private static final String SLACK_CHAT = "<?>";


    private final SlackClient slackClient;

    @Autowired
    public SlackClientTest(SlackClient slackClient) {
        this.slackClient = slackClient;
        this.slackClient.configure(new TimeAmount(250, ChronoUnit.MILLIS));
    }

    @Disabled
    @Test
    void sendMessage() {
        // Arrange
        var message = new SlackClient.SlackMessageRequest(
                SLACK_TOKEN,
                SLACK_CHAT,
                "<@username> <b>text</b>"
        );

        // Act
        this.slackClient.sendMessage(message);

        // Assert
        // no asserts
    }


    @Disabled
    @Test
    void submitMessagesBackpressure() {
        // Arrange
        var messages1 = IntStream.range(0, 20)
                .mapToObj(i -> new SlackClient.SlackMessageRequest(
                        SLACK_TOKEN,
                        SLACK_CHAT,
                        "<@username> <b>" + i + "</b>"
                ))
                .toList();
        var messages2 = IntStream.range(20, 40)
                .mapToObj(i -> new SlackClient.SlackMessageRequest(
                        SLACK_TOKEN,
                        SLACK_CHAT,
                        "<@username> <b>" + i + "</b>"
                ))
                .toList();

        // Act
        messages1.forEach(this.slackClient::submitMessage);
        this.slackClient.submitMessages(messages2);

        // Assert
        SleepUtility.sleep(45, TimeUnit.SECONDS);
    }
}
