package jbst.foundation.domain.events;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentHeader;

public record EventAuthenticationLoginFailure(
        Username username,
        Password password,
        IPAddress ipAddress,
        UserAgentHeader userAgentHeader
) {

    public static EventAuthenticationLoginFailure hardcoded() {
        return new EventAuthenticationLoginFailure(
                Username.hardcoded(),
                Password.hardcoded(),
                IPAddress.hardcoded(),
                UserAgentHeader.hardcoded()
        );
    }
}
