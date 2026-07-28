package jbst.foundation.domain.ids;

import tools.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.JbstUnitTests;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstInvitationIdTest extends JbstUnitTests.Runners.BaseFolderFile {
    private static final JbstInvitationId INVITATION_CODE_ID = JbstInvitationId.of("code123");

    @Override
    protected String getFileName() {
        return "invitation-id.json";
    }

    @Override
    protected String getFolder() {
        return "jsons";
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
        var typeReference = new TypeReference<JbstInvitationId>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(INVITATION_CODE_ID);
    }
}
