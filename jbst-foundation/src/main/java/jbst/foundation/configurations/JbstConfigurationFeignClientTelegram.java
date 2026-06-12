package jbst.foundation.configurations;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jbst.foundation.feigns.telegram.JbstTelegram;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JbstConfigurationFeignClientTelegram {

    @Bean
    JbstTelegram telegram() {
        var feign = Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(JbstTelegram.TelegramDefinition.class, "https://api.telegram.org");
        return new JbstTelegram(feign);
    }
}
