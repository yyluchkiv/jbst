package jbst.foundation.domain.converters.postgres;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.ZoneId;

import static java.util.Objects.nonNull;

@Converter
public class PostgresZoneIdConverter implements AttributeConverter<ZoneId, String> {

    @Override
    public String convertToDatabaseColumn(ZoneId zoneId) {
        return nonNull(zoneId) ? zoneId.getId() : null;
    }

    @Override
    public ZoneId convertToEntityAttribute(String value) {
        return nonNull(value) ? ZoneId.of(value) : null;
    }
}
