package jbst.foundation.domain.dto.responses;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.ids.JbstInvitationId;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.random.JbstRandom.randomStringLetterOrNumbersOnly;

public record ResponseInvitation(
        JbstInvitationId id,
        Username owner,
        String authorities,
        String value,
        String invited,
        String usage
) {
    public static final Comparator<ResponseInvitation> INVITATION = comparing(ResponseInvitation::usage).thenComparing(ResponseInvitation::value);

    public static ResponseInvitation of(
            JbstInvitationId id,
            Username owner,
            String authorities,
            String value,
            Username invited
    ) {
        return new ResponseInvitation(
                id,
                owner,
                authorities,
                value,
                nonNull(invited) ? invited.value() : "",
                nonNull(invited) ? "Used" : "Unused"
        );
    }

    public static ResponseInvitation random(Username owner) {
        return ResponseInvitation.of(
                JbstInvitationId.random(),
                owner,
                "admin",
                randomStringLetterOrNumbersOnly(40),
                null
        );
    }

    public static ResponseInvitation random(Username owner, Username invited) {
        return ResponseInvitation.of(
                JbstInvitationId.random(),
                owner,
                "admin",
                randomStringLetterOrNumbersOnly(40),
                invited
        );
    }
}
