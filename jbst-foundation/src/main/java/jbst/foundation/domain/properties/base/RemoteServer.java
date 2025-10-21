package jbst.foundation.domain.properties.base;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.properties.annotations.AbstractProperty;
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
public class RemoteServer extends AbstractProperty {
    @MandatoryProperty
    private final String baseURL;
    @MandatoryProperty
    private final UsernamePasswordCredentials credentials;

    public static RemoteServer hardcoded() {
        return new RemoteServer("localhost", UsernamePasswordCredentials.hardcoded());
    }

    public static RemoteServer random() {
        return new RemoteServer(randomIPv4(), UsernamePasswordCredentials.hardcoded());
    }

    @Override
    public boolean isParent() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isToggle() {
        return false;
    }

    @Override
    public String getNameNonMandatory() {
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
