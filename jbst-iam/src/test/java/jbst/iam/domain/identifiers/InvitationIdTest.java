package jbst.iam.domain.identifiers;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.ids.InvitationId;
import jbst.foundation.domain.tests.runners.AbstractSerializationDeserializationRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvitationIdTest extends AbstractSerializationDeserializationRunner {
    private static final InvitationId INVITATION_CODE_ID = InvitationId.of("code123");

    @Override
    protected String getFileName() {
        return "invitation-id-1.json";
    }

    @Override
    protected String getFolder() {
        return "dto/identifiers";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(INVITATION_CODE_ID);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<InvitationId>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(INVITATION_CODE_ID);
    }
}
