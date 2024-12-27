package jbst.foundation.feigns.telegram;

import jbst.foundation.configurations.ConfigurationFeignClientTelegram;
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
class TelegramClientTest {

    @Configuration
    @Import({
            ConfigurationFeignClientTelegram.class
    })
    static class TestConfiguration {

    }

    private final TelegramClient telegramClient;

    @Disabled
    @Test
    void sendMessage() {
        // Arrange
        var message = new TelegramClient.TelegramMessageRequest(
                "<?>",
                "<?>",
                "<@username> <b>V1</b>"
        );

        // Act
        this.telegramClient.sendMessage(message);

        // Assert
        // no asserts
    }
}
