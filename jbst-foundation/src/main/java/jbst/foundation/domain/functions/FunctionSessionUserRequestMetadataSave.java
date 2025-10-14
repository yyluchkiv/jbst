package jbst.foundation.domain.functions;

import jbst.foundation.domain.databases.JbstUserSession;
import org.jetbrains.annotations.NotNull;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.foundation.domain.tuples.TupleToggle;

public record FunctionSessionUserRequestMetadataSave(
        @NotNull Username username,
        @NotNull JbstUserSession session,
        @NotNull IPAddress clientIpAddr,
        UserAgentHeader userAgentHeader,
        TupleToggle<Boolean> metadataRenewCron,
        TupleToggle<Boolean> metadataRenewManually
) {
}
