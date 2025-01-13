package jbst.ops.server.configurations;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.ops.server.filters.AuthenticationIncidentFilter;
import jbst.ops.server.properties.OpsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final OpsProperties opsProperties;

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

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        var remoteServer = this.opsProperties.getServerConfigs();
        try {
            auth.inMemoryAuthentication()
                    .withUser(remoteServer.getCredentials().username().value())
                    .password(new BCryptPasswordEncoder().encode(remoteServer.getCredentials().password().value()))
                    .roles(Username.ops().value());
        } catch (Exception ex) {
            throw new IllegalArgumentException("ops-server security configuration failure: " + ex.getMessage());
        }
    }
}
