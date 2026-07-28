package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record JbstRequestAccessToken(String value) {
    public static JbstRequestAccessToken fixed() {
        return new JbstRequestAccessToken("8CF7449A7D1766DE33AD");
    }

    public static JbstRequestAccessToken random() {
        return new JbstRequestAccessToken(randomString());
    }

    @SuppressWarnings("unused")
    public static JbstRequestAccessToken unknown() {
        return new JbstRequestAccessToken(JbstConstants.Strings.UNKNOWN);
    }

    public JbstJwtAccessToken getJwtAccessToken() {
        return new JbstJwtAccessToken(this.value);
    }
}
