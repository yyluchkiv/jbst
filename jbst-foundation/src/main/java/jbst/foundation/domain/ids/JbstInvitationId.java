package jbst.foundation.domain.ids;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record JbstInvitationId(@NotNull String value) {

    @JsonCreator
    public static JbstInvitationId of(String value) {
        return new JbstInvitationId(value);
    }

    public static JbstInvitationId fixed() {
        return of("5EFCB2583361E1C7071E");
    }

    public static JbstInvitationId random() {
        return new JbstInvitationId(randomString());
    }

    @SuppressWarnings("unused")
    public static JbstInvitationId unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    @SuppressWarnings("NullableProblems")
    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
