package jbst.iam.domain.identifiers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record InvitationId(@NotNull String value) {

    @JsonCreator
    public static InvitationId of(String value) {
        return new InvitationId(value);
    }

    public static InvitationId hardcoded() {
        return of("5EFCB2583361E1C7071E");
    }

    public static InvitationId random() {
        return new InvitationId(UUID.randomUUID().toString());
    }

    @SuppressWarnings("unused")
    public static InvitationId unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    public UUID asUUID() {
        return UUID.fromString(this.value);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
