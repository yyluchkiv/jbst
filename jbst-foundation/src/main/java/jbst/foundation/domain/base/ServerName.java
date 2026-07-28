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

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.springframework.util.StringUtils.hasLength;

public record ServerName(@NotNull String value) {

    @JsonCreator
    public static ServerName of(String value) {
        return new ServerName(value);
    }

    public static ServerName fixed() {
        return of("jbst-server");
    }

    public static ServerName random() {
        return of(randomString());
    }

    public static ServerName dash() {
        return of(JbstConstants.Symbols.DASH);
    }

    @SuppressWarnings("unused")
    public static ServerName hyphen() {
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
    @Constraint(validatedBy = ConstraintValidatorOnServerName.class)
    public @interface ValidServerName {
        String message() default "is invalid";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    public static class ConstraintValidatorOnServerName implements ConstraintValidator<ValidServerName, ServerName> {
        @Override
        public boolean isValid(ServerName serverName, ConstraintValidatorContext constraintValidatorContext) {
            return nonNull(serverName) && hasLength(serverName.value);
        }
    }
}
