package jbst.iam.domain.events;

import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.iam.domain.dto.requests.RequestMagicLinkToken;

public record EventAuthenticationMagicLinkFailure(
        RequestMagicLinkToken token,
        IPAddress ipAddress,
        UserAgentHeader userAgentHeader
) {

    public static EventAuthenticationMagicLinkFailure hardcoded() {
        return new EventAuthenticationMagicLinkFailure(
                RequestMagicLinkToken.hardcoded(),
                IPAddress.hardcoded(),
                UserAgentHeader.hardcoded()
        );
    }
}
