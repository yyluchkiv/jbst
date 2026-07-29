package jbst.foundation.domain.properties.base;

import tools.jackson.core.type.TypeReference;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.domain.tests.JbstUnitTests;
import jbst.foundation.domain.tuples.Tuple1;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.domain.enums.JbstSecurityJwtIncident.AUTHENTICATION_LOGIN;
import static jbst.foundation.domain.enums.JbstSecurityJwtIncident.REGISTER1;
import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;
import static org.assertj.core.api.Assertions.assertThat;

class IncidentTypeTest extends JbstUnitTests.Runners.BaseFolder {

    private static Stream<Arguments> serializeTest() {
        return Stream.of(
                Arguments.of(new Tuple1<>(REGISTER1), "incident-type-register1.json"),
                Arguments.of(new Tuple1<>(AUTHENTICATION_LOGIN), "incident-type-authentication-login.json")
        );
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @ParameterizedTest
    @MethodSource("serializeTest")
    void serialize(Tuple1<JbstSecurityJwtIncident> tuple1, String fileName) {
        // Act
        var json = this.writeValueAsString(tuple1);

        // Assert
        assertThat(json).isEqualTo(read(this.getFolder(), fileName));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("serializeTest")
    void deserializeTest(Tuple1<JbstSecurityJwtIncident> tuple1, String fileName) {
        // Arrange
        var json = read(this.getFolder(), fileName);
        var typeReference = new TypeReference<Tuple1<JbstSecurityJwtIncident>>() {};

        // Act
        var tuple = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(tuple).isEqualTo(tuple1);
    }
}
