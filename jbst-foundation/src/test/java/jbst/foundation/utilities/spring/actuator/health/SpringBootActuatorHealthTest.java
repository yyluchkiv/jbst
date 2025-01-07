package jbst.foundation.utilities.spring.actuator.health;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.runners.AbstractFolderSerializationRunner;
import jbst.foundation.feigns.spring.SpringBootClient;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.actuate.health.Status;

import java.util.stream.Stream;

import static jbst.foundation.domain.tests.io.TestsIOUtils.readFile;
import static org.assertj.core.api.Assertions.assertThat;

class SpringBootActuatorHealthTest extends AbstractFolderSerializationRunner {

    private static Stream<Arguments> deserializeTest() {
        return Stream.of(
                Arguments.of(new SpringBootClient.SpringBootActuatorHealth(Status.UP), "health-1.json"),
                Arguments.of(new SpringBootClient.SpringBootActuatorHealth(Status.DOWN), "health-2.json"),
                Arguments.of(SpringBootClient.SpringBootActuatorHealth.unknown(), "health-3.json")
        );
    }

    @Override
    protected String getFolder() {
        return "spring/actuator/health";
    }

    // serialization ignored deliberately

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("deserializeTest")
    void deserializeTest(SpringBootClient.SpringBootActuatorHealth springBootActuatorHealth, String fileName) {
        // Arrange
        var json = readFile(this.getFolder(), fileName);
        var typeReference = new TypeReference<SpringBootClient.SpringBootActuatorHealth>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(springBootActuatorHealth);
    }
}
