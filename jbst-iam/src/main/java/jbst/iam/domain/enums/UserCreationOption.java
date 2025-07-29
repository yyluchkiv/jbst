package jbst.iam.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.enums.EnumValue;
import lombok.AllArgsConstructor;

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
}
