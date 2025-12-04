package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record RequestAccessToken(String value) {
    public static RequestAccessToken hardcoded() {
        return new RequestAccessToken("8CF7449A7D1766DE33AD");
    }

    public static RequestAccessToken random() {
        return new RequestAccessToken(randomString());
    }

    @SuppressWarnings("unused")
    public static RequestAccessToken unknown() {
        return new RequestAccessToken(JbstConstants.Strings.UNKNOWN);
    }

    public JbstJwtAccessToken getJwtAccessToken() {
        return new JbstJwtAccessToken(this.value);
    }
}
