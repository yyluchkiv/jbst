package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record JbstRequestRefreshToken(String value) {
    public static JbstRequestRefreshToken fixed() {
        return new JbstRequestRefreshToken("AE3C542E4368A21EA007");
    }

    public static JbstRequestRefreshToken random() {
        return new JbstRequestRefreshToken(randomString());
    }

    @SuppressWarnings("unused")
    public static JbstRequestRefreshToken unknown() {
        return new JbstRequestRefreshToken(JbstConstants.Strings.UNKNOWN);
    }

    public JbstJwtRefreshToken getJwtRefreshToken() {
        return new JbstJwtRefreshToken(this.value);
    }
}
