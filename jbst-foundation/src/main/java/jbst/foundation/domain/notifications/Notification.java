package jbst.foundation.domain.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode
@ToString
public class Notification {
    @JsonProperty("nt")
    private final Type type;
    @JsonProperty("m")
    private final String message;

    public Notification(@NotNull Type type, @NotNull String message) {
        this.type = type;
        this.message = message;
    }

    public static Notification info(String message) {
        return new Notification(Type.INFO, message);
    }

    public static Notification success(String message) {
        return new Notification(Type.SUCCESS, message);
    }

    public static Notification warning(String message) {
        return new Notification(Type.WARNING, message);
    }

    public static Notification error(String message) {
        return new Notification(Type.ERROR, message);
    }

    public enum Type {
        INFO, SUCCESS, WARNING, ERROR
    }
}
