package jbst.foundation.configurations;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jbst.foundation.feigns.openai.OpenaiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigurationFeignClientOpenai {

    @Bean
    OpenaiClient openaiClient() {
        return new OpenaiClient(
                Feign.builder()
                        .client(new OkHttpClient())
                        .encoder(new JacksonEncoder())
                        .decoder(new JacksonDecoder())
                        .target(OpenaiClient.OpenaiDefinition.class, "https://api.openai.com")
        );
    }
}
