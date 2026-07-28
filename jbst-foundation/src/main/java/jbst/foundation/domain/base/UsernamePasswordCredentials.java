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

    public static UsernamePasswordCredentials fixed() {
        return new UsernamePasswordCredentials(Username.fixed(), Password.fixed());
    }

    public static UsernamePasswordCredentials fixedMasked() {
        return UsernamePasswordCredentials.mask5(Username.fixed(), Password.fixed());
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
