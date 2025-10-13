package jbst.foundation.utilities.development;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class DevelopmentUtility {
    private static final String SEPARATOR = "===================================== {} =====================================";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void printJsonAsDebug(Object object, String keyword) {
        LOGGER.debug(SEPARATOR, keyword);
        try {
            LOGGER.debug("{}\n", MAPPER.writeValueAsString(object));
        } catch (JsonProcessingException ex) {
            LOGGER.debug("Print json. Exception: {}", ex.getMessage());
        }
        LOGGER.debug(SEPARATOR, keyword);
    }

    public static void printJsonAsError(Object object, String keyword) {
        LOGGER.error(SEPARATOR, keyword);
        try {
            LOGGER.error("{}\n", MAPPER.writeValueAsString(object));
        } catch (JsonProcessingException ex) {
            LOGGER.error("Print json. Exception: {}", ex.getMessage());
        }
        LOGGER.error(SEPARATOR, keyword);
    }
}
