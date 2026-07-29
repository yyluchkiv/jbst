package jbst.foundation.domain.dto.requests;

import tools.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.JbstUnitTests;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JbstRequestNewInvitationParamsTest extends JbstUnitTests.Runners.BaseFolderFile {
    private static final JbstRequestNewInvitationParams REQUEST = new JbstRequestNewInvitationParams(
            Set.of("admin", "user")
    );

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Override
    protected String getFileName() {
        return "new-invitation-code-params-1.json";
    }

    // serialization is not required for request-based dtos

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<JbstRequestNewInvitationParams>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(REQUEST);
    }
}
