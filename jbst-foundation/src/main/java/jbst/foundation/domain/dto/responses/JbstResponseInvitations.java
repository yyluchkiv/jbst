package jbst.foundation.domain.dto.responses;

import java.util.List;
import java.util.Set;

public record JbstResponseInvitations(
        Set<String> authorities,
        List<JbstResponseInvitation> invitations
) {
}
