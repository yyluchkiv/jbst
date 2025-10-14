package jbst.foundation.domain.events;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.functions.FunctionSessionUserRequestMetadataSave;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.foundation.domain.tuples.TupleToggle;
import org.jetbrains.annotations.NotNull;

public record EventSessionUserRequestMetadataRenew(
        @NotNull Username username,
        @NotNull JbstUserSession session,
        @NotNull IPAddress clientIpAddr,
        @NotNull UserAgentHeader userAgentHeader,
        @NotNull TupleToggle<Boolean> metadataRenewCron,
        @NotNull TupleToggle<Boolean> metadataRenewManually
) {
    public FunctionSessionUserRequestMetadataSave getSaveFunction() {
        return new FunctionSessionUserRequestMetadataSave(
                this.username,
                this.session,
                this.clientIpAddr,
                this.userAgentHeader,
                this.metadataRenewCron,
                this.metadataRenewManually
        );
    }
}
