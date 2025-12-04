package jbst.foundation.domain.dto.requests;

import jakarta.validation.constraints.NotEmpty;

import java.util.HashSet;
import java.util.Set;

import static jbst.foundation.domain.random.JbstRandom.randomStringsAsList;

public record JbstRequestNewInvitationParams(
        @NotEmpty Set<String> authorities
) {

    public static JbstRequestNewInvitationParams hardcoded() {
        return new JbstRequestNewInvitationParams(new HashSet<>(Set.of("invitations:read", "invitations:write")));
    }

    public static JbstRequestNewInvitationParams random() {
        return new JbstRequestNewInvitationParams(new HashSet<>(randomStringsAsList(3)));
    }
}
