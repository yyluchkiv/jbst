package jbst.iam.domain.jwt;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.jwt.JwtAccessToken;

import static jbst.foundation.utilities.random.RandomUtility.randomString;

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

    public JwtAccessToken getJwtAccessToken() {
        return new JwtAccessToken(this.value);
    }
}
