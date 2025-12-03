package jbst.foundation.domain.geo;

import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.tests.io.TestsIOUtils;
import jbst.foundation.domain.tests.runners.AbstractFolderSerializationRunner;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GeoLocationTest extends AbstractFolderSerializationRunner {

    private static Stream<Arguments> serializeTest() {
        return Stream.of(
                Arguments.of(GeoLocation.processed(IPAddress.localhost(), "Ukraine", "UA", "🇺🇦", "Lviv"), "geo-location-1.json"),
                Arguments.of(GeoLocation.unknown(IPAddress.localhost(), "exception details"), "geo-location-2.json"),
                Arguments.of(GeoLocation.processing(IPAddress.localhost()), "geo-location-3.json"),
                Arguments.of(GeoLocation.processed(IPAddress.localhost(), null, null, null, "Lviv"), "geo-location-4.json"),
                Arguments.of(GeoLocation.unknown(null, "exception details"), "geo-location-5.json")
        );
    }

    private static Stream<Arguments> getWhereTest() {
        return Stream.of(
                Arguments.of(GeoLocation.processed(IPAddress.localhost(), "Ukraine", "UA", "🇺🇦", "Lviv"), "🇺🇦 Ukraine, Lviv"),
                Arguments.of(GeoLocation.processed(IPAddress.localhost(), "Ukraine", "UA", "🇺🇦", ""), "🇺🇦 Ukraine"),
                Arguments.of(GeoLocation.processed(IPAddress.localhost(), "Ukraine", "UA", "🇺🇦", " "), "🇺🇦 Ukraine"),
                Arguments.of(GeoLocation.processed(IPAddress.localhost(), "Ukraine", "UA", "🇺🇦", "    "), "🇺🇦 Ukraine"),
                Arguments.of(GeoLocation.processed(IPAddress.localhost(), "Ukraine", "UA", "🇺🇦", null), "🇺🇦 Ukraine"),
                Arguments.of(GeoLocation.processed(IPAddress.localhost(), null, "UA", "🇺🇦", "Lviv"), "🇺🇦 Unknown"),
                Arguments.of(GeoLocation.processed(IPAddress.localhost(), "", "UA", "🇺🇦", "Lviv"), "🏴‍ Unknown"),
                Arguments.of(GeoLocation.processed(IPAddress.localhost(), "", "UA", "🇺🇦", "Lviv"), "🏴‍ Unknown"),
                Arguments.of(GeoLocation.processed(IPAddress.localhost(), "  ", "UA", "🇺🇦", "Lviv"), "🏴‍ Unknown"),
                Arguments.of(GeoLocation.processed(IPAddress.localhost(), "     ", "UA", "🇺🇦", "Lviv"), "🏴‍ Unknown")
        );
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @ParameterizedTest
    @MethodSource("serializeTest")
    void serializeTest(GeoLocation geoLocation, String fileName) {
        // Act
        var json = this.writeValueAsString(geoLocation);

        // Assert
        assertThat(json).isEqualTo(TestsIOUtils.readFile(this.getFolder(), fileName));
    }

    @ParameterizedTest
    @MethodSource("getWhereTest")
    void getWhereTest(GeoLocation geoLocation, String expected) {
        // Act
        var actual = geoLocation.getWhere();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @RepeatedTest(10)
    void validTest() {
        // Act
        var actual = GeoLocation.valid();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getIpAddr()).isNotNull();
        assertThat(actual.getCountry()).isEqualTo("Ukraine");
        assertThat(actual.getCountryCode()).isEqualTo("UA");
        assertThat(actual.getCountryFlag()).isEqualTo("🇺🇦");
        assertThat(actual.getCity()).isEqualTo("Lviv");
        assertThat(actual.getExceptionDetails()).isEmpty();
        assertThat(actual.getWhere()).isEqualTo("🇺🇦 Ukraine, Lviv");
    }

    @RepeatedTest(10)
    void invalidTest() {
        // Act
        var actual = GeoLocation.invalid();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getIpAddr()).isNotNull();
        assertThat(actual.getCountry()).isEqualTo(JbstConstants.Strings.UNKNOWN);
        assertThat(actual.getCity()).isEqualTo(JbstConstants.Strings.UNKNOWN);
        assertThat(actual.getExceptionDetails()).isEqualTo("Location is unknown");
        assertThat(actual.getWhere()).isEqualTo("🏴‍ Unknown, Unknown");
    }

    @RepeatedTest(10)
    void randomTest() {
        // Act
        var actual = GeoLocation.random();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getIpAddr()).isNotNull();
        assertThat(actual.getCountry()).isNotNull();
        assertThat(actual.getExceptionDetails()).isNotNull();
        assertThat(actual.getWhere()).isNotNull();
    }
}
