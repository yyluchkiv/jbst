package jbst.foundation.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

import static jbst.foundation.domain.enums.JbstEnumsCreator.findEnumByValueIgnoreCaseOrThrow;
import static jbst.foundation.domain.random.JbstRandom.randomEnum;

@AllArgsConstructor
public enum JbstUserCreationOption implements EnumValue<String> {
    STANDARD("Standard"),
    MAGICLINK("MagicLink");

    private final String value;

    public static JbstUserCreationOption hardcoded() {
        return STANDARD;
    }

    public static JbstUserCreationOption random() {
        return randomEnum(JbstUserCreationOption.class);
    }

    @JsonCreator
    public static JbstUserCreationOption find(String value) {
        return findEnumByValueIgnoreCaseOrThrow(JbstUserCreationOption.class, value);
    }

    @JsonValue
    @Override
    public String getValue() {
        return this.value;
    }

    public boolean is(JbstUserCreationOption option) {
        return this.equals(option);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
