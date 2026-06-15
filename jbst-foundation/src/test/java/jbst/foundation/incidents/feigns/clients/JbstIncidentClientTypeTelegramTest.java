package jbst.foundation.incidents.feigns.clients;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.feigns.telegram.JbstTelegram;
import jbst.foundation.incidents.domain.JbstAbstractIncident;
import jbst.foundation.incidents.domain.JbstIncident;
import jbst.foundation.incidents.domain.authetication.JbstIncidentAuthenticationLogin;
import jbst.foundation.incidents.domain.authetication.JbstIncidentAuthenticationLoginFailureUsernameMaskedPassword;
import jbst.foundation.incidents.domain.authetication.JbstIncidentAuthenticationLoginFailureUsernamePassword;
import jbst.foundation.incidents.domain.authetication.JbstIncidentAuthenticationLogoutFull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

@Slf4j
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstIncidentClientTypeTelegramTest {
    private static final String TELEGRAM_TOKEN = "-";
    // How to obtain chatId
    // Open: https://api.telegram.org/bot$token/getUpdates
    private static final String TELEGRAM_CHAT_ID = "394346882";

    private static Stream<Arguments> jwtArgs() {
        return Stream.of(
                Arguments.of(JbstIncidentAuthenticationLogin.hardcoded()),
                Arguments.of(JbstIncidentAuthenticationLoginFailureUsernameMaskedPassword.hardcoded()),
                Arguments.of(JbstIncidentAuthenticationLoginFailureUsernamePassword.hardcoded()),
                Arguments.of(JbstIncidentAuthenticationLogoutFull.hardcoded())
        );
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        JbstTelegram telegram() {
            var telegram = new JbstTelegram();
            telegram.initPragmatic(TELEGRAM_TOKEN, TELEGRAM_CHAT_ID);
            telegram.start();
            return telegram;
        }

        @Bean
        JbstIncidentClientTypeTelegram incidentClientTypeTelegram() {
            return new JbstIncidentClientTypeTelegram(
                    this.telegram(),
                    JbstProperties.hardcoded()
            );
        }
    }

    private final JbstIncidentClientTypeTelegram incidentClientTypeTelegram;

    @Disabled
    @ParameterizedTest
    @MethodSource("jwtArgs")
    void jwts(JbstAbstractIncident incident) {
        // Act
        this.incidentClientTypeTelegram.registerIncident(incident.getPlainIncident());

        // Assert
        // no actions
    }

    @Disabled
    @Test
    void incidentThrowable() {
        // Arrange
        var npe = new NullPointerException("jbst-telegram");
        var incident = new JbstIncident(npe);

        // Act
        this.incidentClientTypeTelegram.registerIncident(incident);

        // Assert
        // no actions
    }
}
