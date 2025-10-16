package jbst.server.ops.configurations;

import jakarta.annotation.PostConstruct;
import jbst.foundation.configurations.*;
import jbst.foundation.domain.base.PropertyId;
import jbst.server.ops.properties.ServerProperties;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({
        JbstConfigurationJasypt.class,
        JbstConfigurationAsync.class,
        JbstConfigurationEvents.class,
        JbstConfigurationEmail.class,
        JbstConfigurationSpringBootServer.class,
        JbstConfigurationUtils.class,
        JbstConfigurationFeignClientGitHub.class
})
@EnableScheduling
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigurationBeans {

    private final ServerProperties serverProperties;

    @PostConstruct
    public void init() {
        this.serverProperties.getServerConfigs().assertProperties(new PropertyId("serverConfigs"));
        this.serverProperties.getServersConfigs().assertProperties(new PropertyId("serversConfigs"));
        this.serverProperties.getSlacksConfigs().assertProperties(new PropertyId("slacksConfigs"));
        this.serverProperties.getRecipientsConfigs().assertProperties(new PropertyId("recipientsConfigs"));
    }

    @Bean
    public RestTemplate restTemplate() {
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                // TODO [YYL] setSSLSocketFactory deprecated, use setTlsSocketStrategy
                .setSSLSocketFactory(
                        SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(SSLContexts.createSystemDefault())
                                .setTlsVersions(TLS.V_1_3)
                                .build()
                )
                .setDefaultSocketConfig(
                        SocketConfig.custom()
                                .setSoTimeout(Timeout.ofMinutes(1))
                                .build()
                )
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                .setConnPoolPolicy(PoolReusePolicy.LIFO)
                .setDefaultConnectionConfig(
                        ConnectionConfig.custom()
                                .setSocketTimeout(Timeout.ofSeconds(5))
                                .setConnectTimeout(Timeout.ofSeconds(5))
                                .setTimeToLive(TimeValue.ofMinutes(10))
                                .build()
                )
                .build();
        var httpClient = HttpClientBuilder
                .create()
                .setConnectionManager(connectionManager)
                .build();
        var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(requestFactory);
    }
}
