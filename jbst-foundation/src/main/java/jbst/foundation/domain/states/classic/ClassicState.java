package jbst.foundation.domain.states.classic;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.enums.EnumValue;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.Set;

import static jbst.foundation.domain.enums.Status.getAnsiFormat;
import static jbst.foundation.utilities.enums.EnumCreatorUtility.findEnumByValueIgnoreCaseOrThrow;

@AllArgsConstructor
public enum ClassicState implements EnumValue<String> {
    DISABLED("Disabled", getAnsiFormat(128, 128, 128)), // Gray
    CREATED("Created", getAnsiFormat(173, 216, 230)), // Light Blue
    STARTING("Starting", getAnsiFormat(255, 165, 0)), // Orange
    ACTIVE("Active", getAnsiFormat(0, 128, 0)), // Green
    PAUSING("Pausing", getAnsiFormat(255, 215, 0)), // Gold
    PAUSED("Paused", getAnsiFormat(218, 165, 32)), // Dark Goldenrod
    STOPPING("Stopping", getAnsiFormat(255, 69, 0)), // Orange-Red
    TERMINATED("Terminated", getAnsiFormat(139, 0, 0)), // Dark Red
    COMPLETING("Completing", getAnsiFormat(100, 149, 237)), // Cornflower Blue
    COMPLETED("Completed", getAnsiFormat(0, 0, 255)); // Blue

    public static final Comparator<ClassicState> ORDINAL_COMPARATOR = Comparator.comparing(ClassicState::ordinal);

    private static AnsiFormat getAnsiFormat(int r, int g, int b) {
        return new AnsiFormat(Attribute.TEXT_COLOR(r, g, b), Attribute.BOLD());
    }

    private final String value;
    private final AnsiFormat ansiFormat;

    @JsonCreator
    public static ClassicState find(String value) {
        return findEnumByValueIgnoreCaseOrThrow(ClassicState.class, value);
    }

    @JsonValue
    @Override
    public String getValue() {
        return this.value;
    }

    @JsonIgnore
    public String formatAnsi() {
        return this.ansiFormat.format(this.value);
    }

    @Override
    public String toString() {
        return this.value;
    }

    public ClassicStatePermissions getPermissions() {
        return new ClassicStatePermissions(
                this.isDisabled(),
                Set.of(CREATED, TERMINATED).contains(this),
                Set.of(CREATED, ACTIVE, PAUSED, TERMINATED, COMPLETED).contains(this),
                this.isActive(),
                this.isActiveOrPaused()
        );
    }

    public boolean isDisabled() {
        return DISABLED.equals(this);
    }

    public boolean isCreated() {
        return CREATED.equals(this);
    }

    public boolean isStarting() {
        return STARTING.equals(this);
    }

    public boolean isActive() {
        return ACTIVE.equals(this);
    }

    public boolean isPaused() {
        return PAUSED.equals(this);
    }

    public boolean isStopping() {
        return STOPPING.equals(this);
    }

    public boolean isTerminated() {
        return TERMINATED.equals(this);
    }

    public boolean isCompleted() {
        return COMPLETED.equals(this);
    }

    public boolean isCreatedOrDisabled() {
        return this.isCreated() || this.isDisabled();
    }

    public boolean isActiveOrPaused() {
        return this.isActive() || this.isPaused();
    }

    public boolean isActiveOrCompleted() {
        return this.isActive() || this.isCompleted();
    }
}
