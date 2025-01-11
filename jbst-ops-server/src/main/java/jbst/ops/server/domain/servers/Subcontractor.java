package jbst.ops.server.domain.servers;

import jbst.foundation.domain.base.Email;

@Deprecated(forRemoval = true)
public record Subcontractor(
        SubcontractorId subcontractorId,
        Email email
) {
}
