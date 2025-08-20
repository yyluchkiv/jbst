package jbst.iam.domain.events;

import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.iam.domain.db.UserToken;

public record EventAuthenticationMagicLinkFailure(
        UserToken token,
        IPAddress ipAddress,
        UserAgentHeader userAgentHeader
) {

    public static EventAuthenticationMagicLinkFailure hardcoded() {
        return new EventAuthenticationMagicLinkFailure(
                UserToken.hardcodedMagicLink(),
                IPAddress.hardcoded(),
                UserAgentHeader.hardcoded()
        );
    }
}
