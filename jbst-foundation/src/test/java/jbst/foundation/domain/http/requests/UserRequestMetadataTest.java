package jbst.foundation.domain.http.requests;

import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.tests.io.TestsIOUtils;
import jbst.foundation.domain.tests.runners.AbstractFolderSerializationRunner;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UserRequestMetadataTest extends AbstractFolderSerializationRunner {

    private static Stream<Arguments> serializeTest() {
        return Stream.of(
                Arguments.of(UserRequestMetadata.processing(IPAddress.localhost()), "user-request-metadata-1.json"),
                Arguments.of(UserRequestMetadata.processed(
                        GeoLocation.processed(IPAddress.localhost(), "Ukraine", "UA", "🇺🇦", "Lviv"),
                        UserAgentDetails.processed("Chrome", "MacOS", "Mobile")
                ), "user-request-metadata-2.json"),
                Arguments.of(UserRequestMetadata.processed(
                        GeoLocation.unknown(IPAddress.localhost(), "exception details on geo location"),
                        UserAgentDetails.unknown("exception details on user agent")
                ), "user-request-metadata-3.json"),
                Arguments.of(UserRequestMetadata.processed(
                        GeoLocation.processed(IPAddress.localhost(), "Ukraine", "UA", "🇺🇦", "Lviv"),
                        UserAgentDetails.unknown("exception details on user agent")
                ), "user-request-metadata-4.json"),
                Arguments.of(UserRequestMetadata.processed(
                        GeoLocation.unknown(IPAddress.localhost(), "exception details on geo location"),
                        UserAgentDetails.processed("Chrome", "MacOS", "Mobile")
                ), "user-request-metadata-5.json")
        );
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @ParameterizedTest
    @MethodSource("serializeTest")
    void serializeTest(UserRequestMetadata userRequestMetadata, String fileName) {
        // Act
        var json = this.writeValueAsString(userRequestMetadata);

        // Assert
        assertThat(json).isEqualTo(TestsIOUtils.readFile(this.getFolder(), fileName));
    }

    @RepeatedTest(10)
    void validTest() {
        // Act
        var actual = UserRequestMetadata.valid();

        // Assert
        assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getStatus()).isEqualTo(Status.COMPLETED);
        Assertions.assertThat(actual.getGeoLocation().getIpAddr()).isNotNull();
        Assertions.assertThat(actual.getGeoLocation().getCountry()).isEqualTo("Ukraine");
        Assertions.assertThat(actual.getGeoLocation().getCity()).isEqualTo("Lviv");
        Assertions.assertThat(actual.getGeoLocation().getExceptionDetails()).isEmpty();
        Assertions.assertThat(actual.getGeoLocation().getWhere()).isEqualTo("🇺🇦 Ukraine, Lviv");
        assertThat(actual.getUserAgentDetails().getBrowser()).isEqualTo("Chrome");
        assertThat(actual.getUserAgentDetails().getPlatform()).isEqualTo("macOS");
        assertThat(actual.getUserAgentDetails().getDeviceType()).isEqualTo("Desktop");
        assertThat(actual.getUserAgentDetails().getExceptionDetails()).isEmpty();
        assertThat(actual.getUserAgentDetails().getWhat()).isEqualTo("Chrome, macOS on Desktop");
        Assertions.assertThat(actual.getWhatTuple2().a()).isEqualTo("Chrome");
        Assertions.assertThat(actual.getWhatTuple2().b()).isEqualTo("Chrome, macOS on Desktop");
        Assertions.assertThat(actual.getWhereTuple3().a()).isNotNull();
        Assertions.assertThat(actual.getWhereTuple3().a().split("\\.")).hasSize(4);
        Assertions.assertThat(actual.getWhereTuple3().b()).isEqualTo("🇺🇦");
        Assertions.assertThat(actual.getWhereTuple3().c()).isEqualTo("🇺🇦 Ukraine, Lviv");
    }

    @RepeatedTest(10)
    void invalidTest() {
        // Act
        var actual = UserRequestMetadata.invalid();

        // Assert
        assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getStatus()).isEqualTo(Status.COMPLETED);
        Assertions.assertThat(actual.getGeoLocation().getIpAddr()).isNotNull();
        Assertions.assertThat(actual.getGeoLocation().getCountry()).isEqualTo("Unknown");
        Assertions.assertThat(actual.getGeoLocation().getCity()).isEqualTo("Unknown");
        Assertions.assertThat(actual.getGeoLocation().getExceptionDetails()).isEqualTo("Location is unknown");
        Assertions.assertThat(actual.getGeoLocation().getWhere()).isEqualTo("🏴‍ Unknown, Unknown");
        assertThat(actual.getUserAgentDetails().getBrowser()).isEqualTo("Unknown");
        assertThat(actual.getUserAgentDetails().getPlatform()).isEqualTo("Unknown");
        assertThat(actual.getUserAgentDetails().getDeviceType()).isEqualTo("Unknown");
        assertThat(actual.getUserAgentDetails().getExceptionDetails()).isEqualTo("User agent details are unknown");
        assertThat(actual.getUserAgentDetails().getWhat()).isEqualTo("Unknown, Unknown on Unknown");
        Assertions.assertThat(actual.getWhatTuple2().a()).isEqualTo("Unknown");
        Assertions.assertThat(actual.getWhatTuple2().b()).isEqualTo("Unknown, Unknown on Unknown");
        Assertions.assertThat(actual.getWhereTuple3().a()).isNotNull();
        Assertions.assertThat(actual.getWhereTuple3().a().split("\\.")).hasSize(4);
        Assertions.assertThat(actual.getWhereTuple3().b()).isEqualTo("🏴‍");
        Assertions.assertThat(actual.getWhereTuple3().c()).isEqualTo("🏴‍ Unknown, Unknown");
    }

    @RepeatedTest(10)
    void randomTest() {
        // Act
        var actual = UserRequestMetadata.random();

        // Assert
        assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getStatus()).isNotNull();
        Assertions.assertThat(actual.getGeoLocation().getIpAddr()).isNotNull();
        Assertions.assertThat(actual.getGeoLocation().getCountry()).isNotNull();
        Assertions.assertThat(actual.getGeoLocation().getCity()).isNotNull();
        Assertions.assertThat(actual.getGeoLocation().getExceptionDetails()).isNotNull();
        Assertions.assertThat(actual.getGeoLocation().getWhere()).isNotNull();
        assertThat(actual.getUserAgentDetails().getBrowser()).isNotNull();
        assertThat(actual.getUserAgentDetails().getPlatform()).isNotNull();
        assertThat(actual.getUserAgentDetails().getDeviceType()).isNotNull();
        assertThat(actual.getUserAgentDetails().getExceptionDetails()).isNotNull();
        assertThat(actual.getUserAgentDetails().getWhat()).isNotNull();
        Assertions.assertThat(actual.getWhatTuple2().a()).isNotNull();
        Assertions.assertThat(actual.getWhatTuple2().b()).isNotNull();
        Assertions.assertThat(actual.getWhereTuple3().a()).isNotNull();
        Assertions.assertThat(actual.getWhereTuple3().b()).isNotNull();
        Assertions.assertThat(actual.getWhereTuple3().c()).isNotNull();
    }
}
