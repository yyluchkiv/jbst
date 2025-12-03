package jbst.foundation.domain.jsons;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

@UtilityClass
public class JbstJsons {

    public static BigDecimal getJsonNodeValueAsBigDecimalOrZero(JsonNode jsonNode) {
        if (isNull(jsonNode)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(jsonNode.textValue());
    }

    public BigDecimal getJsonNodeFieldValueAsBigDecimalOrZero(JsonNode jsonNode, String fieldName) {
        if (isNull(jsonNode)) {
            return BigDecimal.ZERO;
        }
        return getJsonNodeValueAsBigDecimalOrZero(jsonNode.get(fieldName));
    }
}
