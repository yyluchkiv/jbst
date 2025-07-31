package jbst.foundation.feigns.slack;

import jbst.foundation.configurations.JbstConfigurationFeignClientSlack;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.utilities.concurrent.SleepUtility;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
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
    void submitMessages() {
        // Arrange
        var messages = IntStream.range(0, 5)
                .mapToObj(i -> new SlackClient.SlackMessageRequest(
                        SLACK_TOKEN,
                        SLACK_CHAT,
                        "<@username> <b>" + i + "</b>"
                ))
                .toList();

        // Act
        messages.forEach(this.slackClient::submitMessage);

        // Assert
        SleepUtility.sleep(5, TimeUnit.SECONDS);
    }
}
