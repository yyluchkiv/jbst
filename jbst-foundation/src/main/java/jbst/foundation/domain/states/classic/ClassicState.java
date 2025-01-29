package jbst.foundation.domain.states.classic;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.enums.EnumValue;
import jbst.foundation.utilities.colors.ColorUtility;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.Set;

import static jbst.foundation.utilities.enums.EnumCreatorUtility.findEnumByValueIgnoreCaseOrThrow;

@AllArgsConstructor
public enum ClassicState implements EnumValue<String> {
    DISABLED("Disabled", getAnsiFormat("#808080")), // Gray
    CREATED("Created", getAnsiFormat("#ADD8E6")),  // Light Blue
    STARTING("Starting", getAnsiFormat("#FFA500")), // Orange
    ACTIVE("Active", getAnsiFormat("#008000")), // Green
    PAUSING("Pausing", getAnsiFormat("#FFD700")), // Gold
    PAUSED("Paused", getAnsiFormat("#DAA520")), // Dark Goldenrod
    STOPPING("Stopping", getAnsiFormat("#FF4500")), // Orange-Red
    TERMINATED("Terminated", getAnsiFormat("#8B0000")), // Dark Red
    COMPLETING("Completing", getAnsiFormat("#6495ED")), // Cornflower Blue
    COMPLETED("Completed", getAnsiFormat("#0000FF")); // Blue

    public static final Comparator<ClassicState> ORDINAL_COMPARATOR = Comparator.comparing(ClassicState::ordinal);

    private static AnsiFormat getAnsiFormat(String hex) {
        var rgb = ColorUtility.hexToRgb(hex);
        return new AnsiFormat(Attribute.TEXT_COLOR(rgb[0], rgb[1], rgb[2]), Attribute.BOLD());
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
