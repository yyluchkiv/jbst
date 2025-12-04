package jbst.foundation.domain.functions;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.base.IPAddress;
import jbst.foundation.domain.http.requests.JbstUserAgentHeader;
import jbst.foundation.domain.tuples.TupleToggle;
import org.jetbrains.annotations.NotNull;

public record JbstFunctionSessionUserRequestMetadataSave(
        @NotNull Username username,
        @NotNull JbstUserSession session,
        @NotNull IPAddress clientIpAddr,
        JbstUserAgentHeader userAgentHeader,
        TupleToggle<Boolean> metadataRenewCron,
        TupleToggle<Boolean> metadataRenewManually
) {
}
