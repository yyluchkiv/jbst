package jbst.foundation.domain.time;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static jbst.foundation.domain.random.JbstRandom.randomElement;
import static jbst.foundation.domain.random.JbstRandom.randomIntegerGreaterThanZeroByBounds;

public record JbstTimeAmount(long amount, ChronoUnit unit) {

    public static JbstTimeAmount hardcoded() {
        return new JbstTimeAmount(30L, ChronoUnit.SECONDS);
    }

    public static JbstTimeAmount random() {
        return new JbstTimeAmount(
                randomIntegerGreaterThanZeroByBounds(15, 30),
                randomElement(Set.of(ChronoUnit.SECONDS, ChronoUnit.MINUTES, ChronoUnit.HOURS, ChronoUnit.DAYS))
        );
    }

    @SuppressWarnings("unused")
    public static JbstTimeAmount forever() {
        return new JbstTimeAmount(1L, ChronoUnit.FOREVER);
    }

    @JsonIgnore
    public long toSeconds() {
        return this.amount * this.unit().getDuration().toSeconds();
    }

    @JsonIgnore
    public long toMillis() {
        return this.amount * this.unit().getDuration().toMillis();
    }

    @Target({
            ElementType.FIELD,
            ElementType.METHOD
    })
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = ConstraintValidatorOnTimeAmount.class)
    public @interface ValidTimeAmount {
        String message() default "must be between than {minAmount} {minUnit} and {maxAmount} {maxUnit}";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
        int minAmount() default Integer.MIN_VALUE;
        ChronoUnit minUnit() default ChronoUnit.FOREVER;
        int maxAmount() default Integer.MAX_VALUE;
        ChronoUnit maxUnit() default ChronoUnit.FOREVER;
    }

    public static class ConstraintValidatorOnTimeAmount implements ConstraintValidator<JbstTimeAmount.ValidTimeAmount, JbstTimeAmount> {
        private int minAmount;
        private ChronoUnit minUnit;
        private int maxAmount;
        private ChronoUnit maxUnit;

        @Override
        public void initialize(JbstTimeAmount.ValidTimeAmount constraintAnnotation) {
            this.minAmount = constraintAnnotation.minAmount();
            this.minUnit = constraintAnnotation.minUnit();
            this.maxAmount = constraintAnnotation.maxAmount();
            this.maxUnit = constraintAnnotation.maxUnit();
        }

        @Override
        public boolean isValid(JbstTimeAmount configuration, ConstraintValidatorContext constraintValidatorContext) {
            long min = this.minAmount * this.minUnit.getDuration().toMillis();
            long ms = configuration.amount * configuration.unit.getDuration().toMillis();
            long max = this.maxAmount * this.maxUnit.getDuration().toMillis();
            return min <= ms && ms <= max;
        }
    }
}
