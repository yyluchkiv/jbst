package jbst.foundation.domain.events;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.IPAddress;
import jbst.foundation.domain.http.requests.JbstUserAgentHeader;

public record JbstEventAuthenticationLoginFailure(
        Username username,
        Password password,
        IPAddress ipAddress,
        JbstUserAgentHeader userAgentHeader
) {

    public static JbstEventAuthenticationLoginFailure hardcoded() {
        return new JbstEventAuthenticationLoginFailure(
                Username.hardcoded(),
                Password.hardcoded(),
                IPAddress.hardcoded(),
                JbstUserAgentHeader.hardcoded()
        );
    }
}
