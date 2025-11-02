package jbst.foundation.configurations;

import jbst.foundation.assistants.userdetails.JbstJwtUserDetailsService;
import jbst.foundation.domain.base.AbstractAuthority;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyAuthority;
import jbst.foundation.domain.properties.configs.JbstPropertyIncidentsManager;
import jbst.foundation.domain.properties.configs.JbstPropertyMVC;
import jbst.foundation.domain.properties.configs.JbstPropertySecurity;
import jbst.foundation.domain.properties.configs.mvc.JbstPropertyCORS;
import jbst.foundation.domain.properties.configs.security.*;
import jbst.foundation.filters.jwt.JbstTokensFilter;
import jbst.foundation.handlers.JbstAccessDeniedHandler;
import jbst.foundation.handlers.JbstAuthenticationEntryPoint;
import jbst.foundation.handshakes.JbstCsrfInterceptorHandshake;
import jbst.foundation.handshakes.JbstSecurityHandshakeHandler;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstConfigurationSecurityJwtTest {

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {

        @Bean
        public JbstProperties jbstProperties() {
            var jbstProperties = new JbstProperties();
            jbstProperties.setMvcConfigs(
                    new JbstPropertyMVC(
                            true,
                            "/jbst/security",
                            new JbstPropertyCORS(
                                    "/api/**",
                                    new String[] { "http://localhost:1234" },
                                    new String[] { "GET", "POST" },
                                    new String[] { "Access-Control-Allow-Origin" },
                                    true,
                                    null
                            )
                    )
            );
            jbstProperties.setIncidentsManager(JbstPropertyIncidentsManager.hardcoded());
            jbstProperties.setSecurityJwtConfigs(
                    new JbstPropertySecurity(
                            new JbstPropertyAuthorities(
                                    "jbst.foundation.tests.enums",
                                    Set.of(
                                            new JbstPropertyAuthority(AbstractAuthority.SUPERADMIN),
                                            new JbstPropertyAuthority(AbstractAuthority.INVITATIONS_READ),
                                            new JbstPropertyAuthority(AbstractAuthority.INVITATIONS_WRITE),
                                            new JbstPropertyAuthority(AbstractAuthority.PROMETHEUS_READ),
                                            new JbstPropertyAuthority("admin"),
                                            new JbstPropertyAuthority("user")
                                    )
                            ),
                            CookiesConfigs.hardcoded(),
                            EssenceConfigs.hardcoded(),
                            JwtTokensConfigs.hardcoded(),
                            LoggingConfigs.hardcoded(),
                            SessionConfigs.hardcoded(),
                            UsersEmailsConfigs.hardcoded(),
                            WebsocketsConfigs.hardcoded(),
                            UsersTokensConfigs.hardcoded()
                    )
            );
            return jbstProperties;
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            return mock(PasswordEncoder.class);
        }

        @SuppressWarnings("unchecked")
        @Bean
        DaoAuthenticationConfigurer<AuthenticationManagerBuilder, UserDetailsService> userDetailsService() {
            return mock(DaoAuthenticationConfigurer.class);
        }

        @Bean
        AuthenticationManagerBuilder authenticationManagerBuilder() throws Exception {
            var authenticationManagerBuilder = mock(AuthenticationManagerBuilder.class);
            when(authenticationManagerBuilder.userDetailsService(any())).thenReturn(this.userDetailsService());
            return authenticationManagerBuilder;
        }

        @Bean
        JbstCsrfInterceptorHandshake csrfInterceptorHandshake() {
            return mock(JbstCsrfInterceptorHandshake.class);
        }

        @Bean
        JbstSecurityHandshakeHandler securityHandshakeHandler() {
            return mock(JbstSecurityHandshakeHandler.class);
        }

        @Bean
        JbstConfigurationSecurityJwt configurationBaseSecurityJwt() {
            return new JbstConfigurationSecurityJwt(
                    mock(JbstJwtUserDetailsService.class),
                    mock(BCryptPasswordEncoder.class),
                    mock(JbstTokensFilter.class),
                    mock(JbstAuthenticationEntryPoint.class),
                    mock(JbstAccessDeniedHandler.class),
                    this.csrfInterceptorHandshake(),
                    this.securityHandshakeHandler(),
                    mock(AbstractJbstSecurityJwtConfigurer.class),
                    this.jbstProperties()
            );
        }

        @Bean
        SimpleUrlHandlerMapping stompWebSocketHandlerMapping() {
            return mock(SimpleUrlHandlerMapping.class);
        }

    }

    // Handshakes
    private final JbstCsrfInterceptorHandshake csrfInterceptorHandshake;
    private final JbstSecurityHandshakeHandler securityHandshakeHandler;

    private final JbstConfigurationSecurityJwt componentUnderTest;

    @Test
    void beansTests() {
        // Act
        var methods = Stream.of(this.componentUnderTest.getClass().getMethods())
                .map(Method::getName)
                .collect(Collectors.toList());

        // Assert
        assertThat(methods)
                .hasSize(30)
                .contains("registerStompEndpoints")
                .contains("configureMessageBroker");
    }

    @Test
    void registerStompEndpointsTest() {
        // Arrange
        var registration = mock(StompWebSocketEndpointRegistration.class);
        var registry = mock(StompEndpointRegistry.class);
        when(registration.setAllowedOrigins("http://localhost:1234")).thenReturn(registration);
        when(registration.setHandshakeHandler(this.securityHandshakeHandler)).thenReturn(registration);
        when(registration.addInterceptors(this.csrfInterceptorHandshake)).thenReturn(registration);
        when(registry.addEndpoint("/endpoint")).thenReturn(registration);

        // Act
        this.componentUnderTest.registerStompEndpoints(registry);

        // Assert
        verify(registry).addEndpoint("/endpoint");
        verify(registration).setAllowedOrigins("http://localhost:1234");
        verify(registration).setHandshakeHandler(this.securityHandshakeHandler);
        verify(registration).addInterceptors(this.csrfInterceptorHandshake);
        verify(registration).withSockJS();
        verifyNoMoreInteractions(
                registry,
                registration
        );
    }

    @Test
    void configureMessageBrokerTest() {
        // Arrange
        var registry = mock(MessageBrokerRegistry.class);

        // Act
        this.componentUnderTest.configureMessageBroker(registry);

        // Assert
        verify(registry).setApplicationDestinationPrefixes("/app");
        verify(registry).enableSimpleBroker("/queue");
        verify(registry).setUserDestinationPrefix("/user");
        verifyNoMoreInteractions(
                registry
        );
    }
}
