package jbst.foundation.domain.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import static jbst.foundation.domain.random.JbstRandom.randomString;

public record ObjectId(@NotNull String value) {
    @JsonCreator
    public static ObjectId of(String value) {
        return new ObjectId(value);
    }

    @JsonCreator
    public static ObjectId of(Integer value) {
        return new ObjectId(value.toString());
    }

    @JsonCreator
    public static ObjectId of(Long value) {
        return new ObjectId(value.toString());
    }

    public static ObjectId hardcoded() {
        return of("14411F887FF0758B05B3");
    }

    public static ObjectId random() {
        return of(randomString());
    }

    public static ObjectId undefined() {
        return of(JbstConstants.Strings.UNDEFINED);
    }

    public static ObjectId dash() {
        return of(JbstConstants.Symbols.DASH);
    }

    @SuppressWarnings("unused")
    public static ObjectId hyphen() {
        return of(JbstConstants.Symbols.HYPHEN);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
