package jbst.foundation.domain.databases;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.beans.Transient;

import static jbst.foundation.domain.random.JbstRandom.randomIntegerGreaterThanZeroByBounds;

// WARNING: used in postgre as jsonb → use @Transient + @JsonIgnore
@Data
public class JbstUserEmailDetails {
    private final boolean required;
    private final boolean confirmed;

    private JbstUserEmailDetails(
            boolean required,
            boolean confirmed
    ) {
        this.required = required;
        this.confirmed = confirmed;
    }

    public static JbstUserEmailDetails unnecessary() {
        return new JbstUserEmailDetails(false, false);
    }

    public static JbstUserEmailDetails required() {
        return new JbstUserEmailDetails(true, false);
    }

    public static JbstUserEmailDetails confirmed() {
        return new JbstUserEmailDetails(true, true);
    }

    public static JbstUserEmailDetails random() {
        var i = randomIntegerGreaterThanZeroByBounds(1, 2);
        return switch (i) {
            case 1 -> JbstUserEmailDetails.unnecessary();
            case 2 -> JbstUserEmailDetails.required();
            default -> JbstUserEmailDetails.confirmed();
        };
    }

    @Transient
    @JsonIgnore
    public boolean isEnabled() {
        if (this.required) {
            return this.confirmed;
        } else {
            return true;
        }
    }

}
