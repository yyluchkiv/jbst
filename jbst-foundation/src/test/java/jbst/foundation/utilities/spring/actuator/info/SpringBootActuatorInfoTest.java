package jbst.foundation.utilities.spring.actuator.info;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.tests.runners.AbstractFolderSerializationRunner;
import jbst.foundation.feigns.spring.SpringBootClient;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static jbst.foundation.domain.tests.io.TestsIOUtils.readFile;
import static org.assertj.core.api.Assertions.assertThat;

class SpringBootActuatorInfoTest extends AbstractFolderSerializationRunner {

    private static Stream<Arguments> deserializeTest() {
        return Stream.of(
                Arguments.of(
                        SpringBootClient.SpringBootActuatorInfo.dash(),
                        "—",
                        true,
                        Version.dash(),
                        "info-1.json"
                ),
                Arguments.of(
                        new SpringBootClient.SpringBootActuatorInfo(
                                SpringBootClient.SpringBootActuatorInfo.SpringBootActuatorInfoGit.dash(),
                                new ArrayList<>(),
                                null,
                                null
                        ),
                        "—",
                        true,
                        Version.dash(),
                        "info-2.json"
                ),
                Arguments.of(
                        new SpringBootClient.SpringBootActuatorInfo(
                                SpringBootClient.SpringBootActuatorInfo.SpringBootActuatorInfoGit.dash(),
                                new ArrayList<>(List.of("dev", "prod")),
                                null,
                                null
                        ),
                        "dev",
                        false,
                        Version.dash(),
                        "info-3.json"
                ),
                Arguments.of(
                        new SpringBootClient.SpringBootActuatorInfo(
                                SpringBootClient.SpringBootActuatorInfo.SpringBootActuatorInfoGit.dash(),
                                null,
                                "stage",
                                null
                        ),
                        "stage",
                        false,
                        Version.dash(),
                        "info-4.json"
                ),
                Arguments.of(
                        new SpringBootClient.SpringBootActuatorInfo(
                                SpringBootClient.SpringBootActuatorInfo.SpringBootActuatorInfoGit.dash(),
                                null,
                                null,
                                null
                        ),
                        "—",
                        true,
                        Version.dash(),
                        "info-5.json"
                ),
                Arguments.of(
                        SpringBootClient.SpringBootActuatorInfo.hardcoded(),
                        "dev",
                        false,
                        Version.hardcoded(),
                        "info-6.json"
                )
        );
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    // serialization ignored deliberately

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("deserializeTest")
    void deserializeTest(SpringBootClient.SpringBootActuatorInfo springBootActuatorInfo, String profile, boolean isDash, Version version, String fileName) {
        // Arrange
        var json = readFile(this.getFolder(), fileName);
        var typeReference = new TypeReference<SpringBootClient.SpringBootActuatorInfo>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        assertThat(actual).isEqualTo(springBootActuatorInfo);
        assertThat(actual.getProfileOrDash()).isEqualTo(profile);
        assertThat(actual.isProfileDash()).isEqualTo(isDash);
        assertThat(actual.getMavenVersionOrDash()).isEqualTo(version);
    }
}
