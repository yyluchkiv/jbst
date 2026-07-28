package jbst.foundation.utils;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.JbstUnitTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;

@Slf4j
class JbstGeoUtilsFlagsConsoleTest extends JbstUnitTests.Runners.BaseFolder {

    record CountryFlagFull(String name, String code, String emoji, String unicode) {
    }

    record CountryFlagMin(String code, String emoji) {
    }

    @Override
    protected String getFolder() {
        return "tests-jsons";
    }

    @Test
    void readFileTest() throws JacksonException {
        var flagsFullsJSON = read(this.getFolder(), "tests-geo-countries-flags.json");
        var typeReference = new TypeReference<List<CountryFlagFull>>() {};
        var flagsFulls = OBJECT_MAPPER.readValue(flagsFullsJSON, typeReference);
        var flags = flagsFulls.stream()
                .map(flag -> new CountryFlagMin(flag.code(), flag.emoji()))
                .toList();
        var flagsJSON = OBJECT_MAPPER.writeValueAsString(flags);
        LOGGER.info(flagsJSON);
    }
}
