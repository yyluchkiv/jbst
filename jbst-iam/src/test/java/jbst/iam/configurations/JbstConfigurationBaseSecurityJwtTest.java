package jbst.iam.configurations;

import jbst.foundation.domain.base.AbstractAuthority;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.Authority;
import jbst.foundation.domain.properties.configs.MvcConfigs;
import jbst.foundation.domain.properties.configs.SecurityJwtConfigs;
import jbst.foundation.domain.properties.configs.mvc.CorsConfigs;
import jbst.foundation.domain.properties.configs.security.jwt.*;
import jbst.iam.assistants.userdetails.JwtUserDetailsService;
import jbst.iam.filters.jwt.JwtTokensFilter;
import jbst.iam.handlers.exceptions.JwtAccessDeniedExceptionHandler;
import jbst.iam.handlers.exceptions.JwtAuthenticationEntryPointExceptionHandler;
import jbst.iam.handshakes.CsrfInterceptorHandshake;
import jbst.iam.handshakes.SecurityHandshakeHandler;
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
class JbstConfigurationBaseSecurityJwtTest {

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {

        @Bean
        public JbstProperties jbstProperties() {
            var properties = new JbstProperties();
            properties.setMvcConfigs(
                    new MvcConfigs(
                            true,
                            "/jbst/security",
                            new CorsConfigs(
                                    "/api/**",
                                    new String[] { "http://localhost:1234" },
                                    new String[] { "GET", "POST" },
                                    new String[] { "Access-Control-Allow-Origin" },
                                    true,
                                    null
                            )
                    )
            );
            properties.setSecurityJwtConfigs(
                    new SecurityJwtConfigs(
                            new AuthoritiesConfigs(
                                    "jbst.iam.tests.domain.enums",
                                    Set.of(
                                            new Authority(AbstractAuthority.SUPERADMIN),
                                            new Authority(AbstractAuthority.INVITATIONS_READ),
                                            new Authority(AbstractAuthority.INVITATIONS_WRITE),
                                            new Authority(AbstractAuthority.PROMETHEUS_READ),
                                            new Authority("admin"),
                                            new Authority("user")
                                    )
                            ),
                            CookiesConfigs.hardcoded(),
                            EssenceConfigs.hardcoded(),
                            IncidentsConfigs.hardcoded(),
                            JwtTokensConfigs.hardcoded(),
                            LoggingConfigs.hardcoded(),
                            SessionConfigs.hardcoded(),
                            UsersEmailsConfigs.hardcoded(),
                            WebsocketsConfigs.hardcoded(),
                            UsersTokensConfigs.hardcoded()
                    )
            );
            return properties;
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
        CsrfInterceptorHandshake csrfInterceptorHandshake() {
            return mock(CsrfInterceptorHandshake.class);
        }

        @Bean
        SecurityHandshakeHandler securityHandshakeHandler() {
            return mock(SecurityHandshakeHandler.class);
        }

        @Bean
        JbstConfigurationBaseSecurityJwt configurationBaseSecurityJwt() {
            return new JbstConfigurationBaseSecurityJwt(
                    mock(JwtUserDetailsService.class),
                    mock(BCryptPasswordEncoder.class),
                    mock(JwtTokensFilter.class),
                    mock(JwtAuthenticationEntryPointExceptionHandler.class),
                    mock(JwtAccessDeniedExceptionHandler.class),
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
    private final CsrfInterceptorHandshake csrfInterceptorHandshake;
    private final SecurityHandshakeHandler securityHandshakeHandler;

    private final JbstConfigurationBaseSecurityJwt componentUnderTest;

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
