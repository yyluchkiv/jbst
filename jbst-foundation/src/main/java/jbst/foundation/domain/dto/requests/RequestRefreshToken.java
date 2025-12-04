package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;

import static jbst.foundation.domain.random.JbstRandom.randomString;

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

    public JbstJwtRefreshToken getJwtRefreshToken() {
        return new JbstJwtRefreshToken(this.value);
    }
}
