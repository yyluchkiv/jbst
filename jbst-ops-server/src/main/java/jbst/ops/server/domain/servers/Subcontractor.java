package jbst.ops.server.domain.servers;

import jbst.foundation.domain.base.Email;

public record Subcontractor(
        SubcontractorId subcontractorId,
        Email email
) {
}
