package jbst.foundation.domain.http.requests;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.tests.JbstUnitTests;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;
import static org.assertj.core.api.Assertions.assertThat;

class UserAgentDetailsTest extends JbstUnitTests.Runners.BaseFolder {

    private static Stream<Arguments> serializeTest() {
        return Stream.of(
                Arguments.of(UserAgentDetails.processed("Chrome", "macOS", "Desktop"), "user-agent-details-1.json"),
                Arguments.of(UserAgentDetails.processing(), "user-agent-details-2.json"),
                Arguments.of(UserAgentDetails.unknown("exception details"), "user-agent-details-3.json"),
                Arguments.of(UserAgentDetails.processed(null, "macOS", "Desktop"), "user-agent-details-4.json"),
                Arguments.of(UserAgentDetails.processed("Chrome", null, "Desktop"), "user-agent-details-5.json"),
                Arguments.of(UserAgentDetails.processed("Chrome", "macOS", null), "user-agent-details-6.json")
        );
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @ParameterizedTest
    @MethodSource("serializeTest")
    void serializeTest(UserAgentDetails userAgentDetails, String fileName) {
        // Act
        var json = this.writeValueAsString(userAgentDetails);

        // Assert
        assertThat(json).isEqualTo(read(this.getFolder(), fileName));
    }

    @RepeatedTest(10)
    void validTest() {
        // Act
        var actual = UserAgentDetails.valid();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getBrowser()).isEqualTo("Chrome");
        assertThat(actual.getPlatform()).isEqualTo("macOS");
        assertThat(actual.getDeviceType()).isEqualTo("Desktop");
        assertThat(actual.getExceptionDetails()).isEmpty();
        assertThat(actual.getWhat()).isEqualTo("Chrome, macOS on Desktop");
    }

    @RepeatedTest(10)
    void invalidTest() {
        // Act
        var actual = UserAgentDetails.invalid();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getBrowser()).isEqualTo(JbstConstants.Strings.UNKNOWN);
        assertThat(actual.getPlatform()).isEqualTo(JbstConstants.Strings.UNKNOWN);
        assertThat(actual.getDeviceType()).isEqualTo(JbstConstants.Strings.UNKNOWN);
        assertThat(actual.getExceptionDetails()).isEqualTo("User agent details are unknown");
        assertThat(actual.getWhat()).isEqualTo("Unknown, Unknown on Unknown");
    }

    @RepeatedTest(10)
    void randomTest() {
        // Act
        var userAgentDetails = UserAgentDetails.random();

        // Assert
        assertThat(userAgentDetails).isNotNull();
        assertThat(userAgentDetails.getBrowser()).isNotNull();
        assertThat(userAgentDetails.getPlatform()).isNotNull();
        assertThat(userAgentDetails.getDeviceType()).isNotNull();
        assertThat(userAgentDetails.getExceptionDetails()).isNotNull();
        assertThat(userAgentDetails.getWhat()).isNotNull();
    }
}
