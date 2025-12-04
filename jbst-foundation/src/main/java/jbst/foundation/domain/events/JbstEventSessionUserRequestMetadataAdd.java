package jbst.foundation.domain.events;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.enums.JbstAccountAccessMethod;
import jbst.foundation.domain.functions.JbstFunctionSessionUserRequestMetadataSave;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.foundation.domain.tuples.TupleToggle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record JbstEventSessionUserRequestMetadataAdd(
        @NotNull Username username,
        @Nullable Email email,
        @NotNull JbstUserSession session,
        @NotNull IPAddress clientIpAddr,
        @NotNull UserAgentHeader userAgentHeader,
        @NotNull JbstAccountAccessMethod accountAccessMethod
) {
    public JbstFunctionSessionUserRequestMetadataSave getSaveFunction() {
        return new JbstFunctionSessionUserRequestMetadataSave(
                this.username,
                this.session,
                this.clientIpAddr,
                this.userAgentHeader,
                TupleToggle.disabled(),
                TupleToggle.disabled()
        );
    }

    public boolean isUsernamePassword() {
        return this.accountAccessMethod.isUsernamePassword();
    }

    public boolean isSessionToken() {
        return this.accountAccessMethod.isSessionToken();
    }
}
