package jbst.foundation.domain.properties.base;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.JbstProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static jbst.foundation.utilities.random.RandomUtility.randomIPv4;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstPropertyRemoteServer extends JbstProperty {
    @MandatoryProperty
    private final String baseURL;
    @MandatoryProperty
    private final UsernamePasswordCredentials credentials;

    public static JbstPropertyRemoteServer hardcoded() {
        return new JbstPropertyRemoteServer("localhost", UsernamePasswordCredentials.hardcoded());
    }

    public static JbstPropertyRemoteServer random() {
        return new JbstPropertyRemoteServer(randomIPv4(), UsernamePasswordCredentials.hardcoded());
    }

    @Override
    public JbstPropertyNodeType getNodeType() {
        return JbstPropertyNodeType.LEAF;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonLeaf() {
        return JbstConstants.Symbols.DASH;
    }

    @SuppressWarnings("unused")
    public boolean containsCredentials(@NotNull UsernamePasswordCredentials credentials) {
        return this.credentials.equals(credentials);
    }

    @SuppressWarnings("unused")
    public <T> T createFeignOnBasicAuthentication(Class<T> clazz) {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .requestInterceptor(
                        new BasicAuthRequestInterceptor(
                                this.credentials.username().value(),
                                this.credentials.password().value()
                        )
                )
                .target(clazz, this.baseURL);
    }
}
