package jbst.foundation.feigns.telegram;

import jbst.foundation.configurations.JbstConfigurationFeignClientTelegram;
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
class JbstTelegramTest {

    @Configuration
    @Import({
            JbstConfigurationFeignClientTelegram.class
    })
    static class TestConfiguration {

    }

    private final JbstTelegram telegram;

    @Disabled
    @Test
    void sendMessage() {
        // Arrange
        var message = new JbstTelegram.TelegramMessageRequest(
                "<?>",
                "<?>",
                "<@username> <b>V1</b>"
        );

        // Act
        this.telegram.sendMessage(message);

        // Assert
        // no asserts
    }
}
