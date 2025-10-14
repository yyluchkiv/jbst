package jbst.foundation.domain.databases;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.beans.Transient;

import static jbst.foundation.utilities.random.RandomUtility.randomIntegerGreaterThanZeroByBounds;

// WARNING: used in postgre as jsonb → use @Transient + @JsonIgnore
@Data
public class UserEmailDetails {
    private final boolean required;
    private final boolean confirmed;

    private UserEmailDetails(
            boolean required,
            boolean confirmed
    ) {
        this.required = required;
        this.confirmed = confirmed;
    }

    public static UserEmailDetails unnecessary() {
        return new UserEmailDetails(false, false);
    }

    public static UserEmailDetails required() {
        return new UserEmailDetails(true, false);
    }

    public static UserEmailDetails confirmed() {
        return new UserEmailDetails(true, true);
    }

    public static UserEmailDetails random() {
        var i = randomIntegerGreaterThanZeroByBounds(1, 2);
        return switch (i) {
            case 1 -> UserEmailDetails.unnecessary();
            case 2 -> UserEmailDetails.required();
            default -> UserEmailDetails.confirmed();
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
