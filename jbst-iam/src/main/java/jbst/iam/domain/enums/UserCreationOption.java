package jbst.iam.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jbst.foundation.domain.enums.EnumValue;
import lombok.AllArgsConstructor;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.enums.EnumCreatorUtility.findEnumByValueIgnoreCaseOrThrow;

@AllArgsConstructor
public enum UserCreationOption implements EnumValue<String> {
    STANDARD("Standard");

    private final String value;

    public static UserCreationOption hardcoded() {
        return STANDARD;
    }

    @JsonCreator
    public static UserCreationOption find(String value) {
        return findEnumByValueIgnoreCaseOrThrow(UserCreationOption.class, value);
    }

    @JsonValue
    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Converter
    public class UserCreationOptionPostgresConverter implements AttributeConverter<UserCreationOption, String> {

        @Override
        public String convertToDatabaseColumn(UserCreationOption obj) {
            return nonNull(obj) ? obj.value : null;
        }

        @Override
        public UserCreationOption convertToEntityAttribute(String column) {
            return nonNull(column) ? UserCreationOption.find(column) : null;
        }
    }
}
