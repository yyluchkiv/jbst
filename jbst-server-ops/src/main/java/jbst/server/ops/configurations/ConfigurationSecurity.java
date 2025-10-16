package jbst.server.ops.configurations;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.server.ops.filters.AuthenticationIncidentFilter;
import jbst.server.ops.properties.ServerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigurationSecurity {

    // Filters
    private final AuthenticationIncidentFilter authenticationIncidentFilter;
    // Properties
    private final JbstProperties jbstProperties;
    private final ServerProperties serverProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(this.authenticationIncidentFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(authorizeHttpRequests -> {
            authorizeHttpRequests
                    .requestMatchers("/api/incidents/**").permitAll()
                    .requestMatchers("/actuator/**").hasRole(Username.ops().value());
            if (this.jbstProperties.getServerConfigs().isSpringdocEnabled()) {
                authorizeHttpRequests.requestMatchers(JbstConstants.Swagger.ENDPOINTS.toArray(new String[0])).permitAll();
            }
            authorizeHttpRequests.anyRequest().authenticated();
        });

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsManager(BCryptPasswordEncoder passwordEncoder) {
        var remoteServer = this.serverProperties.getServerConfigs();
        var credentials = remoteServer.getCredentials();
        return new InMemoryUserDetailsManager(
                User.withUsername(credentials.username().value())
                        .password(passwordEncoder.encode(credentials.password().value()))
                        .roles(Username.ops().value())
                        .build()
        );
    }
}
