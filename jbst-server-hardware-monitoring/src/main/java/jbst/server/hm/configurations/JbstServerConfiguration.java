package jbst.server.hm.configurations;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jakarta.annotation.PostConstruct;
import jbst.foundation.configurations.JbstConfigurationAsync;
import jbst.foundation.configurations.JbstConfigurationEvents;
import jbst.foundation.configurations.JbstConfigurationSpringBootServer;
import jbst.server.hm.client.HardwareMonitoringClient;
import jbst.server.hm.properties.ServerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableConfigurationProperties({
        ServerProperties.class
})
@Import({
        JbstConfigurationAsync.class,
        JbstConfigurationEvents.class,
        JbstConfigurationSpringBootServer.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstServerConfiguration {

    // Properties
    private final ServerProperties serverProperties;

    @PostConstruct
    public void init() {
        this.serverProperties.getServer().assertProperties();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                .anyRequest().authenticated()
        ).csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public HardwareMonitoringClient.HardwareMonitoringClientDefinition hardwareMonitoringClientDefinition() {
        var serverConfigs = this.serverProperties.getServer();
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(HardwareMonitoringClient.HardwareMonitoringClientDefinition.class, serverConfigs.getTargetURL());
    }
}
