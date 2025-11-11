package jbst.foundation.domain.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.random.RandomUtility.randomLongGreaterThanZero;
import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

public record Timestamp(long value) {

    @JsonCreator
    public static Timestamp of(long value) {
        return new Timestamp(value);
    }

    // Wednesday, April 3, 2024 10:36:10 AM
    public static Timestamp hardcoded() {
        return of(1712140570L);
    }

    public static Timestamp random() {
        return of(randomLongGreaterThanZero());
    }

    public static Timestamp unknown() {
        return of(-1L);
    }

    public static Timestamp current() {
        return of(getCurrentTimestamp());
    }

    @JsonValue
    public long getValue() {
        return this.value;
    }

    @NotNull
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Target({
            ElementType.FIELD,
            ElementType.METHOD
    })
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = ConstraintValidatorOnTimestamp.class)
    public @interface ValidTimestamp {
        String message() default "is invalid";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    public static class ConstraintValidatorOnTimestamp implements ConstraintValidator<ValidTimestamp, Timestamp> {
        @Override
        public boolean isValid(Timestamp timestamp, ConstraintValidatorContext constraintValidatorContext) {
            return nonNull(timestamp) && timestamp.value > 0;
        }
    }
}
