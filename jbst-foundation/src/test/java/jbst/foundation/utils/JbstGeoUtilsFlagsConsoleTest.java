package jbst.foundation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.runners.AbstractFolderSerializationRunner;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;

@Slf4j
class JbstGeoUtilsFlagsConsoleTest extends AbstractFolderSerializationRunner {

    record CountryFlagFull(String name, String code, String emoji, String unicode) {
    }

    record CountryFlagMin(String code, String emoji) {
    }

    @Override
    protected String getFolder() {
        return "tests-jsons";
    }

    @Test
    void readFileTest() throws JsonProcessingException {
        var flagsFullsJSON = read(this.getFolder(), "tests-geo-countries-flags.json");
        var typeReference = new TypeReference<List<CountryFlagFull>>() {};
        var flagsFulls = OBJECT_MAPPER.readValue(flagsFullsJSON, typeReference);
        var flags = flagsFulls.stream()
                .map(flag -> new CountryFlagMin(flag.code(), flag.emoji()))
                .collect(Collectors.toList());
        var flagsJSON = OBJECT_MAPPER.writeValueAsString(flags);
        LOGGER.info(flagsJSON);
    }
}
