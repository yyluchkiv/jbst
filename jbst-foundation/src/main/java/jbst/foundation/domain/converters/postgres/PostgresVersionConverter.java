package jbst.foundation.domain.converters.postgres;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jbst.foundation.domain.base.Version;

@SuppressWarnings("unused")
@Converter
public class PostgresVersionConverter implements AttributeConverter<Version, String> {

    @Override
    public String convertToDatabaseColumn(Version version) {
        return version.value();
    }

    @Override
    public Version convertToEntityAttribute(String value) {
        return new Version(value);
    }
}
