package jbst.foundation.domain.jsons;

import lombok.experimental.UtilityClass;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Jackson 3 changed serialization defaults: alphabetical property ordering and toString()-based enums.
 * All jbst mappers go through this factory to keep the Jackson 2 wire format — existing API consumers
 * and persisted JSON rely on declaration-order properties and name()-based enum values.
 * <p>
 * Deliberately NOT configureForJackson2(): that preset also drops implicit creator-name detection,
 * which jbst relies on for Lombok-generated constructors (Jackson 3 no longer reads
 * java.beans @ConstructorProperties).
 */
@UtilityClass
public class JbstObjectMappers {

    public static JsonMapper.Builder jackson2CompatibleBuilder() {
        return JsonMapper.builder()
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(EnumFeature.READ_ENUMS_USING_TO_STRING, EnumFeature.WRITE_ENUMS_USING_TO_STRING);
    }

    public static ObjectMapper jackson2Compatible() {
        return jackson2CompatibleBuilder().build();
    }
}
