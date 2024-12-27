package jbst.foundation.configurations;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jbst.foundation.feigns.slack.SlackClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigurationFeignClientSlack {

    @Bean
    SlackClient slackClient() {
        return new SlackClient(
                Feign.builder()
                        .client(new OkHttpClient())
                        .encoder(new JacksonEncoder())
                        .decoder(new JacksonDecoder())
                        .target(SlackClient.SlackDefinition.class, "https://slack.com/api")
        );
    }
}
