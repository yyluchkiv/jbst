package jbst.foundation.feigns.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.tests.JbstUnitTests;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.actuate.health.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;
import static org.assertj.core.api.Assertions.assertThat;

class JbstSpringBootTest extends JbstUnitTests.Runners.BaseFolder {

    private static Stream<Arguments> healthArgs() {
        return Stream.of(
                Arguments.of(new JbstSpringBoot.SpringBootActuatorHealth(Status.UP), "health-1.json"),
                Arguments.of(new JbstSpringBoot.SpringBootActuatorHealth(Status.DOWN), "health-2.json"),
                Arguments.of(JbstSpringBoot.SpringBootActuatorHealth.unknown(), "health-3.json")
        );
    }

    private static Stream<Arguments> infoArgs() {
        return Stream.of(
                Arguments.of(
                        JbstSpringBoot.SpringBootActuatorInfo.dash(),
                        "—",
                        true,
                        Version.dash(),
                        "info-1.json"
                ),
                Arguments.of(
                        new JbstSpringBoot.SpringBootActuatorInfo(
                                JbstSpringBoot.SpringBootActuatorInfo.SpringBootActuatorInfoGit.dash(),
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
                        new JbstSpringBoot.SpringBootActuatorInfo(
                                JbstSpringBoot.SpringBootActuatorInfo.SpringBootActuatorInfoGit.dash(),
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
                        new JbstSpringBoot.SpringBootActuatorInfo(
                                JbstSpringBoot.SpringBootActuatorInfo.SpringBootActuatorInfoGit.dash(),
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
                        new JbstSpringBoot.SpringBootActuatorInfo(
                                JbstSpringBoot.SpringBootActuatorInfo.SpringBootActuatorInfoGit.dash(),
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
                        JbstSpringBoot.SpringBootActuatorInfo.fixed(),
                        "dev",
                        false,
                        Version.fixed(),
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
    @MethodSource("healthArgs")
    void healthTest(JbstSpringBoot.SpringBootActuatorHealth springBootActuatorHealth, String fileName) {
        // Arrange
        var json = read(this.getFolder(), fileName);
        var typeReference = new TypeReference<JbstSpringBoot.SpringBootActuatorHealth>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(springBootActuatorHealth);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("infoArgs")
    void infoTest(JbstSpringBoot.SpringBootActuatorInfo springBootActuatorInfo, String profile, boolean isDash, Version version, String fileName) {
        // Arrange
        var json = read(this.getFolder(), fileName);
        var typeReference = new TypeReference<JbstSpringBoot.SpringBootActuatorInfo>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        assertThat(actual).isEqualTo(springBootActuatorInfo);
        assertThat(actual.getProfileOrDash()).isEqualTo(profile);
        assertThat(actual.isProfileDash()).isEqualTo(isDash);
        assertThat(actual.getMavenVersionOrDash()).isEqualTo(version);
    }
}
