package jbst.foundation.feigns.slack;

import jbst.foundation.configurations.ConfigurationFeignClientSlack;
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

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class SlackClientTest {

    @Configuration
    @Import({
            ConfigurationFeignClientSlack.class
    })
    static class TestConfiguration {

    }

    private final SlackClient slackClient;

    @Disabled
    @Test
    void sendMessage() {
        // Arrange
        var message = new SlackClient.SlackMessageRequest(
                "<?>",
                "#<?>",
                "<@username> <b>V2</b>"
        );

        // Act
        this.slackClient.sendMessage(message);

        // Assert
        // no asserts
    }
}
