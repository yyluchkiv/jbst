package jbst.foundation.domain.jwt;

import jbst.foundation.domain.constants.JbstConstants;

import static jbst.foundation.utilities.random.RandomUtility.randomString;

public record RequestRefreshToken(String value) {
    public static RequestRefreshToken hardcoded() {
        return new RequestRefreshToken("AE3C542E4368A21EA007");
    }

    public static RequestRefreshToken random() {
        return new RequestRefreshToken(randomString());
    }

    @SuppressWarnings("unused")
    public static RequestRefreshToken unknown() {
        return new RequestRefreshToken(JbstConstants.Strings.UNKNOWN);
    }

    public JwtRefreshToken getJwtRefreshToken() {
        return new JwtRefreshToken(this.value);
    }
}
