package jbst.foundation.utilities.development;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jbst.foundation.domain.constants.JbstConstants;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({"unused", "LoggingSimilarMessage"})
@Slf4j
@UtilityClass
public class DevelopmentUtility {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void printJsonAsDebug(Object object, String keyword) {
        LOGGER.debug(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
        try {
            LOGGER.debug("{}:\n\n{}\n", keyword, MAPPER.writeValueAsString(object));
        } catch (JsonProcessingException ex) {
            LOGGER.debug("JSON printing failure: {}", ex.getMessage());
        }
        LOGGER.debug(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
    }

    public static void printJsonAsError(Object object, String keyword) {
        LOGGER.error(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
        try {
            LOGGER.debug("{}:\n\n{}\n", keyword, MAPPER.writeValueAsString(object));
        } catch (JsonProcessingException ex) {
            LOGGER.debug("JSON printing failure: {}", ex.getMessage());
        }
        LOGGER.error(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
    }
}
