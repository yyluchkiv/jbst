package jbst.foundation.domain.states;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.JbstUnitTests;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class JbstStateClassicTest extends JbstUnitTests.Runners.BaseFolderFile {
    private static final JbstStateClassic STATE = JbstStateClassic.ACTIVE;

    private static Stream<Arguments> getPermissionsTest() {
        return Stream.of(
                Arguments.of(JbstStateClassic.DISABLED, new JbstStateClassic.Permissions(true, false, false, false, false)),
                Arguments.of(JbstStateClassic.CREATED, new JbstStateClassic.Permissions(false, true, true, false, false)),
                Arguments.of(JbstStateClassic.STARTING, new JbstStateClassic.Permissions(false, false, false, false, false)),
                Arguments.of(JbstStateClassic.ACTIVE, new JbstStateClassic.Permissions(false, false, true, true, true)),
                Arguments.of(JbstStateClassic.PAUSING, new JbstStateClassic.Permissions(false, false, false, false, false)),
                Arguments.of(JbstStateClassic.PAUSED, new JbstStateClassic.Permissions(false, false, true, false, true)),
                Arguments.of(JbstStateClassic.STOPPING, new JbstStateClassic.Permissions(false, false, false, false, false)),
                Arguments.of(JbstStateClassic.TERMINATED, new JbstStateClassic.Permissions(false, true, true, false, false)),
                Arguments.of(JbstStateClassic.COMPLETING, new JbstStateClassic.Permissions(false, false, false, false, false)),
                Arguments.of(JbstStateClassic.COMPLETED, new JbstStateClassic.Permissions(false, false, true, false, false))
        );
    }

    @Override
    protected String getFileName() {
        return "classic-state.json";
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Test
    void assertRequiredStatesTest() {
        // Assert
        assertThat(JbstStateClassic.values()).hasSize(10);
    }

    @ParameterizedTest
    @EnumSource(JbstStateClassic.class)
    void serializeTest(JbstStateClassic state) {
        // Act
        var json = this.writeValueAsString(state).replace("\"", "");

        // Assert
        assertThat(json).isEqualTo(state.getValue());
    }

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<JbstStateClassic>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(STATE);
    }

    @ParameterizedTest
    @MethodSource("getPermissionsTest")
    void getPermissionsTest(JbstStateClassic state, JbstStateClassic.Permissions permissions) {
        // Act
        var actual = state.getPermissions();

        // Assert
        assertThat(actual).isEqualTo(permissions);
    }
}
