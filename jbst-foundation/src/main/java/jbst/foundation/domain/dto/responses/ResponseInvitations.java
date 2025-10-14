package jbst.foundation.domain.dto.responses;

import java.util.List;
import java.util.Set;

public record ResponseInvitations(
        Set<String> authorities,
        List<ResponseInvitation> invitations
) {
}
