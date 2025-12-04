package jbst.foundation.domain.geo;

import jbst.foundation.domain.tests.runners.AbstractFolderSerializationRunner;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;
import static org.assertj.core.api.Assertions.assertThat;

class GeoCountryFlagTest extends AbstractFolderSerializationRunner {

    private static Stream<Arguments> serializeTest() {
        return Stream.of(
                Arguments.of(new GeoCountryFlag("Ukraine", "Lviv", "🇺🇦", "U+1F1FA U+1F1E6"), "geo-country-flag-1.json"),
                Arguments.of(new GeoCountryFlag("United States", "US", "🇺🇸", "U+1F1FA U+1F1F8"), "geo-country-flag-2.json")
        );
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @ParameterizedTest
    @MethodSource("serializeTest")
    void serializeTest(GeoCountryFlag geoCountryFlag, String fileName) {
        // Act
        var json = this.writeValueAsString(geoCountryFlag);

        // Assert
        assertThat(json).isEqualTo(read(this.getFolder(), fileName));
    }
}
