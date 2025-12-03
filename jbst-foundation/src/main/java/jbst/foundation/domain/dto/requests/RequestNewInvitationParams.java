package jbst.foundation.domain.dto.requests;

import jakarta.validation.constraints.NotEmpty;

import java.util.HashSet;
import java.util.Set;

import static jbst.foundation.domain.random.JbstRandom.randomStringsAsList;

public record RequestNewInvitationParams(
        @NotEmpty Set<String> authorities
) {

    public static RequestNewInvitationParams hardcoded() {
        return new RequestNewInvitationParams(new HashSet<>(Set.of("invitations:read", "invitations:write")));
    }

    public static RequestNewInvitationParams random() {
        return new RequestNewInvitationParams(new HashSet<>(randomStringsAsList(3)));
    }
}
