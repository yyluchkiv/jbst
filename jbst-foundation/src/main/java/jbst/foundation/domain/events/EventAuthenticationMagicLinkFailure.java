package jbst.foundation.domain.events;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentHeader;

public record EventAuthenticationMagicLinkFailure(
        JbstUserToken token,
        IPAddress ipAddress,
        UserAgentHeader userAgentHeader
) {

    public static EventAuthenticationMagicLinkFailure hardcoded() {
        return new EventAuthenticationMagicLinkFailure(
                JbstUserToken.hardcodedMagicLink(),
                IPAddress.hardcoded(),
                UserAgentHeader.hardcoded()
        );
    }
}
