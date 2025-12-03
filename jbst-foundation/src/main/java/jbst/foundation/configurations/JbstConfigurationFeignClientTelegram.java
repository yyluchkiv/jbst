package jbst.foundation.configurations;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jbst.foundation.feigns.telegram.JbstTelegram;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationFeignClientTelegram {

    @Bean
    JbstTelegram telegramClient() {
        return new JbstTelegram(
                Feign.builder()
                        .client(new OkHttpClient())
                        .encoder(new JacksonEncoder())
                        .decoder(new JacksonDecoder())
                        .target(JbstTelegram.TelegramDefinition.class, "https://api.telegram.org")
        );
    }
}
