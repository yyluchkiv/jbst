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
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.springframework.util.StringUtils.hasLength;

public record Username(@NotNull String value) {

    @JsonCreator
    public static Username of(String value) {
        return new Username(value);
    }

    public static Username fixed() {
        return of("jbst");
    }

    public static Username cron() {
        return of("cron");
    }

    public static Username ops() {
        return of("ops");
    }

    public static Username random() {
        return of(randomString());
    }

    public static Username unknown() {
        return of(JbstConstants.Strings.UNKNOWN);
    }

    public static Username dash() {
        return of(JbstConstants.Symbols.DASH);
    }

    @SuppressWarnings("unused")
    public static Username hyphen() {
        return of(JbstConstants.Symbols.HYPHEN);
    }

    public static List<String> asStrings(Set<Username> usernames) {
        return usernames.stream()
                .map(Username::value)
                .toList();
    }

    public static List<String> asStrings(List<Username> usernames) {
        return usernames.stream()
                .map(Username::value)
                .toList();
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
    @Constraint(validatedBy = ConstraintValidatorOnUsername.class)
    public @interface ValidUsername {
        String message() default "is invalid";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    public static class ConstraintValidatorOnUsername implements ConstraintValidator<ValidUsername, Username> {
        @Override
        public boolean isValid(Username username, ConstraintValidatorContext constraintValidatorContext) {
            return nonNull(username) && hasLength(username.value);
        }
    }
}
