package jbst.iam.converters.postgres;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jbst.foundation.domain.enums.UserCreationOption;

import static java.util.Objects.nonNull;

@Converter
public class PostgresUserCreationOptionConverter implements AttributeConverter<UserCreationOption, String> {

    @Override
    public String convertToDatabaseColumn(UserCreationOption obj) {
        return nonNull(obj) ? obj.getValue() : null;
    }

    @Override
    public UserCreationOption convertToEntityAttribute(String column) {
        return nonNull(column) ? UserCreationOption.find(column) : null;
    }
}
