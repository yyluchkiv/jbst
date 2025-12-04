package jbst.foundation.configurations;

import jakarta.annotation.PostConstruct;
import jbst.foundation.assistants.userdetails.JbstUserDetailsService;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.filters.jwt.JbstTokensFilter;
import jbst.foundation.handlers.JbstAccessDeniedHandler;
import jbst.foundation.handlers.JbstAuthenticationEntryPoint;
import jbst.foundation.handshakes.JbstCsrfInterceptorHandshake;
import jbst.foundation.handshakes.JbstSecurityHandshakeHandler;
import jbst.foundation.domain.enums.JbstEnums;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import static jbst.foundation.domain.base.AbstractAuthority.INVITATIONS_READ;
import static jbst.foundation.domain.base.AbstractAuthority.INVITATIONS_WRITE;
import static org.springframework.http.HttpMethod.*;

/**
 * <a href="https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html">Documentation #1</a>
 * <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket">Documentation #2</a>
 * <p>
 * Spring Boot 3 Migration Issues 24.04.2024:
 * <p>
 * <a href="https://github.com/spring-projects/spring-security/issues/13640">
 * EnableWebSocketSecurity is not 1:1 replacement for AbstractSecurityWebSocketMessageBrokerConfigurer
 * </a>
 * <p>
 * <a href="https://github.com/jhipster/generator-jhipster/issues/20404">
 * Migrate to Spring Security 6's @EnableWebSocketSecurity (it is not possible to disable CSRF currently)
 * </a>
 */
// idea - reconnect flow: https://stackoverflow.com/questions/53244720/spring-websocket-stomp-exception-handling
// TODO [YYL, deprecation] fixme
@SuppressWarnings({"deprecation", "SpringJavaInjectionPointsAutowiringInspection"})
@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@ComponentScan({
        "jbst.foundation.assistants.utils",
        "jbst.foundation.crons",
        "jbst.foundation.events.publishers",
        "jbst.foundation.handlers",
        "jbst.foundation.handshakes",
        "jbst.foundation.resources",
        "jbst.foundation.services.base",
        "jbst.foundation.tokens",
        "jbst.foundation.validators.base",
        "jbst.foundation.websockets"
})
@Import({
        JbstConfigurationJasypt.class,
        JbstConfigurationUtils.class,
        JbstConfigurationSpringBootServer.class,
        JbstConfigurationSecurityJwtWebMVC.class,
        JbstConfigurationSecurityJwtFilters.class,
        JbstConfigurationSecurityJwtPasswords.class,
        JbstConfigurationIncidents.class,
        JbstConfigurationEmail.class
})
@EnableWebSecurity
@EnableWebSocketMessageBroker
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationSecurityJwt extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    // Assistants
    private final JbstUserDetailsService jwtUserDetailsService;
    // Passwords
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    // Filters
    private final JbstTokensFilter tokensFilter;
    // Handlers
    private final JbstAuthenticationEntryPoint authenticationEntryPoint;
    private final JbstAccessDeniedHandler accessDeniedHandler;
    // Handshakes
    private final JbstCsrfInterceptorHandshake csrfInterceptorHandshake;
    private final JbstSecurityHandshakeHandler securityHandshakeHandler;
    // Configurer
    private final JbstAbstractSecurityJwtConfigurer jbstAbstractSecurityJwtConfigurer;
    // Properties
    private final JbstProperties jbstProperties;

    @PostConstruct
    public void init() {
        // incidents
        var incidentsManager = this.jbstProperties.getIncidentsManager();
        if (incidentsManager.isEnabled()) {
            incidentsManager.assertPropertiesExtended(JbstEnums.getEnumNames(JbstSecurityJwtIncident.class));
            var loginFailure1 = incidentsManager.isEnabled("AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD", JbstSecurityJwtIncident.class);
            var loginFailure2 = incidentsManager.isEnabled("AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD", JbstSecurityJwtIncident.class);
            if (loginFailure1 && loginFailure2) {
                throw new IllegalArgumentException("[IncidentsManager]: one login failure feature type expected to be provided");
            }
        }
        // security
        this.jbstProperties.getSecurity().assertProperties();
    }

    @Autowired
    void configureAuthenticationManager(AuthenticationManagerBuilder builder) throws Exception {
        builder
                .userDetailsService(this.jwtUserDetailsService)
                .passwordEncoder(this.bCryptPasswordEncoder);
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            if (this.jbstProperties.getApp().isSpringdocEnabled()) {
                web.ignoring().requestMatchers(JbstConstants.Swagger.ENDPOINTS.toArray(new String[0]));
            }
            // WARNING: You are asking Spring Security to ignore Ant [pattern='/**/**'].
            // This is not recommended: please use permitAll via HttpSecurity#authorizeHttpRequests instead
            if (this.jbstProperties.getSecurity().getWebsockets().isEnabled()) {
                var endpoint = this.jbstProperties.getSecurity().getWebsockets().getStomp().getEndpoint();
                web.ignoring().requestMatchers(endpoint + "/**");
            }
            this.jbstAbstractSecurityJwtConfigurer.configure(web);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var basePathPrefix = this.jbstProperties.getMvc().getBasePathPrefix();

        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(
                        this.tokensFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(this.authenticationEntryPoint)
                                .accessDeniedHandler(this.accessDeniedHandler)
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // WARNING: order is important, configurer must have possibility to override matchers below
        this.jbstAbstractSecurityJwtConfigurer.configure(http);

        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/**"))
                .authorizeHttpRequests(authorizeHttpRequests -> {
                    authorizeHttpRequests
                            .requestMatchers(POST, basePathPrefix + "/authentication/login/standard").permitAll()
                            .requestMatchers(POST, basePathPrefix + "/authentication/login/magiclink").permitAll()
                            .requestMatchers(POST, basePathPrefix + "/authentication/logout").permitAll()
                            .requestMatchers(POST, basePathPrefix + "/authentication/refreshToken").permitAll()
                            .requestMatchers(GET, basePathPrefix + "/session/current").authenticated()
                            .requestMatchers(POST, basePathPrefix + "/registration/register-magiclink").denyAll()
                            .requestMatchers(POST, basePathPrefix + "/registration/register0").denyAll()
                            .requestMatchers(POST, basePathPrefix + "/registration/register1").denyAll()
                            .requestMatchers(POST, basePathPrefix + "/user/update1").authenticated()
                            .requestMatchers(POST, basePathPrefix + "/user/update2").authenticated()
                            .requestMatchers(POST, basePathPrefix + "/user/changePassword1").authenticated()
                            .requestMatchers(GET, basePathPrefix + "/tokens/email/confirm").denyAll()
                            .requestMatchers(POST, basePathPrefix + "/tokens/password/reset").denyAll()
                            .requestMatchers(PATCH, basePathPrefix + "/tokens/password/reset").denyAll();

                    if (this.jbstProperties.getSecurity().getEssence().getInvitationsOnInit().isEnabled()) {
                        authorizeHttpRequests
                                .requestMatchers(GET, basePathPrefix + "/invitations").hasAuthority(INVITATIONS_READ)
                                .requestMatchers(POST, basePathPrefix + "/invitations").hasAuthority(INVITATIONS_WRITE)
                                .requestMatchers(DELETE, basePathPrefix + "/invitations/{invitationId}").hasAuthority(INVITATIONS_WRITE);
                    } else {
                        authorizeHttpRequests.requestMatchers(basePathPrefix + "/invitations/**").denyAll();
                    }

                    authorizeHttpRequests
                            .requestMatchers(basePathPrefix + "/hardcoded/**").authenticated()
                            .requestMatchers(basePathPrefix + "/**").authenticated();

                    authorizeHttpRequests.requestMatchers(GET, "/system/csrf").authenticated();
                    authorizeHttpRequests.requestMatchers("/actuator/**").permitAll();
                    authorizeHttpRequests.requestMatchers("/hardware/**").permitAll();

                    authorizeHttpRequests.anyRequest().authenticated();
        });

        return http.build();
    }

    // =================================================================================================================
    // Websockets
    // =================================================================================================================
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        if (!this.jbstProperties.getSecurity().getWebsockets().isEnabled()) {
            return;
        }
        registry.addEndpoint(this.jbstProperties.getSecurity().getWebsockets().getStomp().getEndpoint())
                .setAllowedOrigins(this.jbstProperties.getMvc().getCors().getAllowedOrigins())
                .setHandshakeHandler(this.securityHandshakeHandler)
                .addInterceptors(this.csrfInterceptorHandshake)
                .withSockJS();
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry registry) {
        if (!this.jbstProperties.getSecurity().getWebsockets().isEnabled()) {
            return;
        }
        registry.anyMessage().authenticated();
    }

    @Override
    public void configureMessageBroker(@NotNull MessageBrokerRegistry registry) {
        if (!this.jbstProperties.getSecurity().getWebsockets().isEnabled()) {
            return;
        }
        var broker = this.jbstProperties.getSecurity().getWebsockets().getBrokerRegistry();
        registry.setApplicationDestinationPrefixes(broker.getApplicationDestinationPrefix());
        registry.enableSimpleBroker(broker.getSimpleDestination());
        registry.setUserDestinationPrefix(broker.getUserDestinationPrefix());
    }

    /**
     * Determines if a CSRF token is required for connecting. This protects against remote
     * sites from connecting to the application and being able to read/write data over the
     * connection. The default is false (the token is required).
     */
    @Override
    protected boolean sameOriginDisabled() {
        return false;
    }
}
