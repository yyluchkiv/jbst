package jbst.foundation.domain.development;

import tools.jackson.core.JacksonException;
import jbst.foundation.domain.jsons.JbstObjectMappers;
import tools.jackson.databind.ObjectMapper;
import jbst.foundation.domain.constants.JbstConstants;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({"unused", "LoggingSimilarMessage"})
@Slf4j
@UtilityClass
public class JbstDevelopment {
    private static final ObjectMapper MAPPER = JbstObjectMappers.jackson2Compatible();

    public static void printJsonAsDebug(Object object, String keyword) {
        LOGGER.debug(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
        try {
            LOGGER.debug("{}:\n\n{}\n", keyword, MAPPER.writeValueAsString(object));
        } catch (JacksonException ex) {
            LOGGER.debug("JSON printing failure: {}", ex.getMessage());
        }
        LOGGER.debug(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
    }

    public static void printJsonAsError(Object object, String keyword) {
        LOGGER.error(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
        try {
            LOGGER.error("{}:\n\n{}\n", keyword, MAPPER.writeValueAsString(object));
        } catch (JacksonException ex) {
            LOGGER.error("JSON printing failure: {}", ex.getMessage());
        }
        LOGGER.error(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
    }
}
