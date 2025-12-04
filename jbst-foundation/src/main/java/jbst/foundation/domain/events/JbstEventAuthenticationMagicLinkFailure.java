package jbst.foundation.domain.events;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentHeader;

public record JbstEventAuthenticationMagicLinkFailure(
        JbstUserToken token,
        IPAddress ipAddress,
        UserAgentHeader userAgentHeader
) {

    public static JbstEventAuthenticationMagicLinkFailure hardcoded() {
        return new JbstEventAuthenticationMagicLinkFailure(
                JbstUserToken.hardcodedMagicLink(),
                IPAddress.hardcoded(),
                UserAgentHeader.hardcoded()
        );
    }
}
