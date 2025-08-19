package jbst.iam.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.enums.EnumValue;
import lombok.AllArgsConstructor;

import static jbst.foundation.utilities.enums.EnumCreatorUtility.findEnumByValueIgnoreCaseOrThrow;
import static jbst.foundation.utilities.random.RandomUtility.randomEnum;

@AllArgsConstructor
public enum UserCreationOption implements EnumValue<String> {
    STANDARD("Standard");

    private final String value;

    public static UserCreationOption hardcoded() {
        return STANDARD;
    }

    public static UserCreationOption random() {
        return randomEnum(UserCreationOption.class);
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

    public boolean isStandard() {
        return UserCreationOption.STANDARD.equals(this);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
