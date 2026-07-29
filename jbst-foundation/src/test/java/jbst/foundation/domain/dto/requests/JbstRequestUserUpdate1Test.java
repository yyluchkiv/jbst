package jbst.foundation.domain.dto.requests;

import tools.jackson.core.type.TypeReference;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.tests.JbstUnitTests;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static org.assertj.core.api.Assertions.assertThat;

class JbstRequestUserUpdate1Test extends JbstUnitTests.Runners.BaseFolderFile {
    private static final JbstRequestUserUpdate1 REQUEST = new JbstRequestUserUpdate1(
            UKRAINE,
            Email.fixed(),
            "Fixed Tests"
    );

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Override
    protected String getFileName() {
        return "user-update-1.json";
    }

    // serialization is not required for request-based dtos

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<JbstRequestUserUpdate1>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(REQUEST);
    }
}
