package jbst.foundation.domain.converters.postgres;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jbst.foundation.domain.base.Password;

@Converter
public class PostgresPasswordConverter implements AttributeConverter<Password, String> {

    @Override
    public String convertToDatabaseColumn(Password password) {
        return password.value();
    }

    @Override
    public Password convertToEntityAttribute(String value) {
        return Password.of(value);
    }
}
