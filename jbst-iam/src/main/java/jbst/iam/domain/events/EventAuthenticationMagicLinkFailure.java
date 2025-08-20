package jbst.iam.domain.events;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.iam.domain.dto.requests.RequestMagicLinkToken;

public record EventAuthenticationMagicLinkFailure(
        Email email,
        RequestMagicLinkToken token,
        IPAddress ipAddress,
        UserAgentHeader userAgentHeader
) {

    public static EventAuthenticationMagicLinkFailure hardcoded() {
        return new EventAuthenticationMagicLinkFailure(
                Email.hardcoded(),
                RequestMagicLinkToken.hardcoded(),
                IPAddress.hardcoded(),
                UserAgentHeader.hardcoded()
        );
    }
}
