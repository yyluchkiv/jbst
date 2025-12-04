package jbst.foundation.domain.events;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.base.IPAddress;
import jbst.foundation.domain.http.requests.JbstUserAgentHeader;

public record JbstEventAuthenticationMagicLinkFailure(
        JbstUserToken token,
        IPAddress ipAddress,
        JbstUserAgentHeader userAgentHeader
) {

    public static JbstEventAuthenticationMagicLinkFailure hardcoded() {
        return new JbstEventAuthenticationMagicLinkFailure(
                JbstUserToken.hardcodedMagicLink(),
                IPAddress.hardcoded(),
                JbstUserAgentHeader.hardcoded()
        );
    }
}
