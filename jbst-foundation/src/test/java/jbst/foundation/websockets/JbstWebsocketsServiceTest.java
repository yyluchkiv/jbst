package jbst.foundation.websockets;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.events.WebsocketEvent;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.JbstPropertySecurity;
import jbst.foundation.domain.properties.configs.security.JbstPropertySecurityWebsockets;
import jbst.foundation.domain.properties.configs.security.websockets.JbstPropertyWebsocketsCSRF;
import jbst.foundation.domain.properties.configs.security.websockets.JbstPropertyWebsocketsMessageBrokerRegistry;
import jbst.foundation.domain.properties.configs.security.websockets.JbstPropertyWebsocketsStompEndpointRegistry;
import jbst.foundation.domain.properties.configs.security.websockets.JbstPropertyWebsocketsFeatures;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstWebsocketsServiceTest {

    private static Stream<Arguments> convertAndSendToUserTestArgs() {
        return Stream.of(
                Arguments.of(true, true),
                Arguments.of(false, false)
        );
    }

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        @Bean
        JbstProperties jbstProperties() {
            return mock(JbstProperties.class);
        }

        @Bean
        SimpMessagingTemplate simpMessagingTemplate() {
            return mock(SimpMessagingTemplate.class);
        }

        @Bean
        JbstIncidentsPublisher incidentsPublisher() {
            return mock(JbstIncidentsPublisher.class);
        }

        @Bean
        JbstWebsocketsService websocketsService() {
            return new JbstWebsocketsService(
                    this.simpMessagingTemplate(),
                    this.incidentsPublisher(),
                    this.jbstProperties()
            );
        }
    }

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JbstIncidentsPublisher incidentsPublisher;
    private final JbstProperties jbstProperties;

    private final JbstWebsocketsService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.simpMessagingTemplate,
                this.incidentsPublisher,
                this.jbstProperties
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.simpMessagingTemplate,
                this.incidentsPublisher,
                this.jbstProperties
        );
    }

    @Test
    void convertAndSendToUserThrowExceptionTest() {
        // Assert
        when(this.jbstProperties.getSecurity()).thenReturn(JbstPropertySecurity.hardcoded());
        var username = Username.random();
        var websocketEvent = mock(WebsocketEvent.class);
        var ex = new MessagingException(randomString());
        var destination = "/" + randomString();
        doThrow(ex).when(this.simpMessagingTemplate).convertAndSendToUser(username.value(), "/queue" + destination, websocketEvent);

        // Act
        this.componentUnderTest.sendEventToUser(username, destination, websocketEvent);

        // Assert
        verify(this.jbstProperties, times(2)).getSecurity();
        verify(this.simpMessagingTemplate).convertAndSendToUser(username.value(), "/queue" + destination, websocketEvent);
        verify(this.incidentsPublisher).publishThrowable(ex);
        verifyNoMoreInteractions(this.simpMessagingTemplate);
    }

    @ParameterizedTest
    @MethodSource("convertAndSendToUserTestArgs")
    void convertAndSendToUserTest(boolean enabled, boolean expectedSend) {
        // Assert
        var security = mock(JbstPropertySecurity.class);
        when(security.getWebsocketsConfigs()).thenReturn(
                new JbstPropertySecurityWebsockets(
                        enabled,
                        JbstPropertyWebsocketsCSRF.hardcoded(),
                        JbstPropertyWebsocketsStompEndpointRegistry.hardcoded(),
                        JbstPropertyWebsocketsMessageBrokerRegistry.hardcoded(),
                        JbstPropertyWebsocketsFeatures.hardcoded()
                )
        );
        when(this.jbstProperties.getSecurity()).thenReturn(security);
        var username = Username.random();
        var destination = randomString();
        var websocketEvent = mock(WebsocketEvent.class);

        // Act
        this.componentUnderTest.sendEventToUser(username, destination, websocketEvent);

        // Assert
        if (expectedSend) {
            verify(this.jbstProperties, times(2)).getSecurity();
            verify(this.simpMessagingTemplate).convertAndSendToUser(username.value(), "/queue" + destination, websocketEvent);
            verifyNoMoreInteractions(this.simpMessagingTemplate);
        } else {
            verify(this.jbstProperties).getSecurity();
        }
    }
}
