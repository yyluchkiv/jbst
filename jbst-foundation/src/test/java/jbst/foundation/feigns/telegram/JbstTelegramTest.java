package jbst.foundation.feigns.telegram;

import jbst.foundation.configurations.JbstConfigurationFeignClientTelegram;
import jbst.foundation.domain.concurrent.JbstSleep;
import jbst.foundation.domain.strings.JbstTraces;
import jbst.foundation.feigns.telegram.JbstTelegram.ClientException;
import jbst.foundation.feigns.telegram.JbstTelegram.ConfigurationException;
import jbst.foundation.feigns.telegram.JbstTelegram.RateLimitsException;
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

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


@Slf4j
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
class JbstTelegramTest {

    @Configuration
    @Import({
            JbstConfigurationFeignClientTelegram.class
    })
    static class TestConfiguration {

    }

    private static final String TELEGRAM_TOKEN = "-";
    // How to obtain chatId
    // Open: https://api.telegram.org/bot$token/getUpdates
    private static final String TELEGRAM_CHAT_ID = "-";

    private final JbstTelegram telegram;

    @Autowired
    public JbstTelegramTest(JbstTelegram telegram) {
        this.telegram = telegram;
        this.telegram.initPragmatic(TELEGRAM_TOKEN);
        this.telegram.start();
    }

    @Disabled
    @Test
    void sendMessage() throws ConfigurationException, RateLimitsException, ClientException {
        // Arrange
        var npe = new NullPointerException("jbst-telegram");
        var trace = JbstTraces.getTrace(npe).value().substring(0, 4000);
        var message = JbstTelegram.TelegramMessageRequest.of(
                TELEGRAM_CHAT_ID,
                trace
        );

        // Act
        var res = this.telegram.sendMessage(message);

        // Assert
        LOGGER.info("jbst-telegram res@send: {}", res);
    }

    @Disabled
    @Test
    void messagesBackpressureSend() {
        // Arrange
        var step = 20;
        var messages1 = IntStream.range(0, step)
                .mapToObj(i -> JbstTelegram.TelegramMessageRequest.of(
                        TELEGRAM_CHAT_ID,
                        "<b>" + i + "</b>"
                ))
                .toList();
        var messages2 = IntStream.range(step, step * 2)
                .mapToObj(i -> JbstTelegram.TelegramMessageRequest.of(
                        TELEGRAM_CHAT_ID,
                        "<b>" + i + "</b>"
                ))
                .toList();

        // Act
        for (var req : messages1) {
            this.telegram.submitMessage(req);
        }
        this.telegram.submitMessages(messages2);

        // Assert
        JbstSleep.sleep(30, TimeUnit.SECONDS);
    }
}
