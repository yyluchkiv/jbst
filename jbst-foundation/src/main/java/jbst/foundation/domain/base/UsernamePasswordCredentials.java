package jbst.foundation.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.strings.JbstMasks;
import org.springframework.data.annotation.Transient;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public record UsernamePasswordCredentials(
        Username username,
        Password password
) {
    public static UsernamePasswordCredentials mask5(Username username, Password password) {
        return new UsernamePasswordCredentials(username, Password.of(JbstMasks.mask5(password.value())));
    }

    public static UsernamePasswordCredentials hardcoded() {
        return new UsernamePasswordCredentials(Username.hardcoded(), Password.hardcoded());
    }

    public static UsernamePasswordCredentials hardcodedMasked() {
        return UsernamePasswordCredentials.mask5(Username.hardcoded(), Password.hardcoded());
    }

    public static UsernamePasswordCredentials random() {
        return new UsernamePasswordCredentials(Username.random(), Password.random());
    }

    @JsonIgnore
    @Transient
    public UsernamePasswordAuthenticationToken getAuthenticationToken() {
        return new UsernamePasswordAuthenticationToken(username.value(), password.value());
    }
}
