package jbst.foundation.domain.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class JbstNotification {
    @JsonProperty("nt")
    @NotNull
    private final Type type;
    @JsonProperty("m")
    @NotNull
    private final String message;

    public static JbstNotification info(String message) {
        return new JbstNotification(Type.INFO, message);
    }

    public static JbstNotification success(String message) {
        return new JbstNotification(Type.SUCCESS, message);
    }

    public static JbstNotification warning(String message) {
        return new JbstNotification(Type.WARNING, message);
    }

    public static JbstNotification error(String message) {
        return new JbstNotification(Type.ERROR, message);
    }

    public enum Type {
        INFO, SUCCESS, WARNING, ERROR
    }
}
