package jbst.foundation.domain.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jbst.foundation.domain.constants.JbstConstants;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.springframework.util.StringUtils.hasLength;

@SuppressWarnings("unused")
public record Version(@NotNull String value) {
    public static final Comparator<Version> NATURAL_ORDER = Comparator.comparing(Version::value);

    public static final Version VERSION_1_0 = Version.of("1.0");
    public static final Version VERSION_1_1 = Version.of("1.1");
    public static final Version VERSION_1_2 = Version.of("1.2");
    public static final Version VERSION_1_3 = Version.of("1.3");
    public static final Version VERSION_1_4 = Version.of("1.4");
    public static final Version VERSION_1_5 = Version.of("1.5");
    public static final Version VERSION_1_6 = Version.of("1.6");
    public static final Version VERSION_1_7 = Version.of("1.7");
    public static final Version VERSION_1_8 = Version.of("1.8");
    public static final Version VERSION_1_9 = Version.of("1.9");
    public static final Version VERSION_2_0 = Version.of("2.0");
    public static final Version VERSION_2_1 = Version.of("2.1");

    @JsonCreator
    public static Version of(String value) {
        return new Version(value);
    }

    public static Version hardcoded() {
        return of("v1.1");
    }

    public static Version random() {
        return of(randomString());
    }

    public static Version undefined() {
        return of(JbstConstants.Strings.UNDEFINED);
    }

    public static Version unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    public static Version dash() {
        return of(JbstConstants.Symbols.DASH);
    }

    @SuppressWarnings("unused")
    public static Version hyphen() {
        return of(JbstConstants.Symbols.HYPHEN);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }

    @Target({
            ElementType.FIELD,
            ElementType.METHOD
    })
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = ConstraintValidatorOnVersion.class)
    public @interface ValidVersion {
        String message() default "is invalid";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    public static class ConstraintValidatorOnVersion implements ConstraintValidator<ValidVersion, Version> {
        @Override
        public boolean isValid(Version version, ConstraintValidatorContext constraintValidatorContext) {
            return nonNull(version) && hasLength(version.value);
        }
    }
}
