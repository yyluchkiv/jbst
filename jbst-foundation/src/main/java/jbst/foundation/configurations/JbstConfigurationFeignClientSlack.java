package jbst.foundation.configurations;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jbst.foundation.events.publishers.JbstIncidentsPublisher;
import jbst.foundation.feigns.slack.SlackClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        JbstConfigurationIncidents.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationFeignClientSlack {
    private final JbstIncidentsPublisher incidentsPublisher;

    @Bean
    SlackClient slackClient() {
        var slackDefinition = Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(SlackClient.SlackDefinition.class, "https://slack.com/api");
        return new SlackClient(
                slackDefinition,
                this.incidentsPublisher
        );
    }
}
