package jbst.foundation.domain.dto.responses;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.ids.JbstInvitationId;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.random.JbstRandom.randomStringLetterOrNumbersOnly;

public record JbstResponseInvitation(
        JbstInvitationId id,
        Username owner,
        String authorities,
        String value,
        String invited,
        String usage
) {
    public static final Comparator<JbstResponseInvitation> INVITATION = comparing(JbstResponseInvitation::usage).thenComparing(JbstResponseInvitation::value);

    public static JbstResponseInvitation of(
            JbstInvitationId id,
            Username owner,
            String authorities,
            String value,
            Username invited
    ) {
        return new JbstResponseInvitation(
                id,
                owner,
                authorities,
                value,
                nonNull(invited) ? invited.value() : "",
                nonNull(invited) ? "Used" : "Unused"
        );
    }

    public static JbstResponseInvitation random(Username owner) {
        return JbstResponseInvitation.of(
                JbstInvitationId.random(),
                owner,
                "admin",
                randomStringLetterOrNumbersOnly(40),
                null
        );
    }

    public static JbstResponseInvitation random(Username owner, Username invited) {
        return JbstResponseInvitation.of(
                JbstInvitationId.random(),
                owner,
                "admin",
                randomStringLetterOrNumbersOnly(40),
                invited
        );
    }
}
