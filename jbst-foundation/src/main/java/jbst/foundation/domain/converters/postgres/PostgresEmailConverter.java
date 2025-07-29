package jbst.foundation.domain.converters.postgres;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jbst.foundation.domain.base.Email;

import static java.util.Objects.nonNull;

@Converter
public class PostgresEmailConverter implements AttributeConverter<Email, String> {

    @Override
    public String convertToDatabaseColumn(Email email) {
        return nonNull(email) ? email.value() : null;
    }

    @Override
    public Email convertToEntityAttribute(String value) {
        return nonNull(value) ? Email.of(value) : null;
    }
}
