package jbst.foundation.domain.states.classic;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.runners.AbstractSerializationDeserializationRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ClassicStateTest extends AbstractSerializationDeserializationRunner {
    private static final ClassicState STATE = ClassicState.ACTIVE;

    private static Stream<Arguments> getPermissionsTest() {
        return Stream.of(
                Arguments.of(ClassicState.DISABLED, new ClassicStatePermissions(true, false, false, false, false)),
                Arguments.of(ClassicState.CREATED, new ClassicStatePermissions(false, true, true, false, false)),
                Arguments.of(ClassicState.STARTING, new ClassicStatePermissions(false, false, false, false, false)),
                Arguments.of(ClassicState.ACTIVE, new ClassicStatePermissions(false, false, true, true, true)),
                Arguments.of(ClassicState.IDLE, new ClassicStatePermissions(false, false, false, false, true)),
                Arguments.of(ClassicState.PAUSING, new ClassicStatePermissions(false, false, false, false, false)),
                Arguments.of(ClassicState.PAUSED, new ClassicStatePermissions(false, false, true, false, true)),
                Arguments.of(ClassicState.STOPPING, new ClassicStatePermissions(false, false, false, false, false)),
                Arguments.of(ClassicState.TERMINATED, new ClassicStatePermissions(false, true, true, false, false)),
                Arguments.of(ClassicState.COMPLETING, new ClassicStatePermissions(false, false, false, false, false)),
                Arguments.of(ClassicState.COMPLETED, new ClassicStatePermissions(false, false, true, false, false))
        );
    }

    @Override
    protected String getFileName() {
        return "classic-state-1.json";
    }

    @Override
    protected String getFolder() {
        return "states";
    }

    @Test
    void assertRequiredStatesTest() {
        // Assert
        assertThat(ClassicState.values()).hasSize(11);
    }

    @ParameterizedTest
    @EnumSource(ClassicState.class)
    void serializeTest(ClassicState state) {
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
        var typeReference = new TypeReference<ClassicState>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(STATE);
    }

    @ParameterizedTest
    @MethodSource("getPermissionsTest")
    void getPermissionsTest(ClassicState state, ClassicStatePermissions permissions) {
        // Act
        var actual = state.getPermissions();

        // Assert
        assertThat(actual).isEqualTo(permissions);
    }
}
