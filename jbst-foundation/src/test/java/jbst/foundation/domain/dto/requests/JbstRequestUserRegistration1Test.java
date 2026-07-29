package jbst.foundation.domain.dto.requests;

import tools.jackson.core.type.TypeReference;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.tests.JbstUnitTests;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static org.assertj.core.api.Assertions.assertThat;

class JbstRequestUserRegistration1Test extends JbstUnitTests.Runners.BaseFolderFile {
    private static final JbstRequestUserRegistration1 REQUEST = new JbstRequestUserRegistration1(
            Username.fixed(),
            Password.of("password123"),
            Password.of("password123"),
            UKRAINE,
            "TJ5veLJvqi78AARpiDVXQ9u0q9rbo3zpE6LtbWBH"
    );

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Override
    protected String getFileName() {
        return "user-registration-1.json";
    }

    // serialization is not required for request-based dtos

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<JbstRequestUserRegistration1>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(REQUEST);
    }
}
