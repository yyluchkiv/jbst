package jbst.foundation.domain.states;

import com.diogonunes.jcolor.AnsiFormat;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.enums.JbstEnumValue;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.Set;

import static jbst.foundation.domain.colors.JbstANSI.getBoldHexAnsiFormat;
import static jbst.foundation.domain.enums.JbstEnumsCreator.findEnumByValueIgnoreCaseOrThrow;

@SuppressWarnings("unused")
@AllArgsConstructor
public enum JbstStateClassic implements JbstEnumValue<String> {
    DISABLED("Disabled", getBoldHexAnsiFormat("#808080")), // Gray
    CREATED("Created", getBoldHexAnsiFormat("#ADD8E6")),  // Light Blue
    STARTING("Starting", getBoldHexAnsiFormat("#FFA500")), // Orange
    ACTIVE("Active", getBoldHexAnsiFormat("#008000")), // Green
    PAUSING("Pausing", getBoldHexAnsiFormat("#FFD700")), // Gold
    PAUSED("Paused", getBoldHexAnsiFormat("#DAA520")), // Dark Goldenrod
    STOPPING("Stopping", getBoldHexAnsiFormat("#FF4500")), // Orange-Red
    TERMINATED("Terminated", getBoldHexAnsiFormat("#8B0000")), // Dark Red
    COMPLETING("Completing", getBoldHexAnsiFormat("#6495ED")), // Cornflower Blue
    COMPLETED("Completed", getBoldHexAnsiFormat("#0000FF")); // Blue

    public static final Comparator<JbstStateClassic> ORDINAL_COMPARATOR = Comparator.comparing(JbstStateClassic::ordinal);

    private final String value;
    private final AnsiFormat ansiFormat;

    @JsonCreator
    public static JbstStateClassic find(String value) {
        return findEnumByValueIgnoreCaseOrThrow(JbstStateClassic.class, value);
    }

    @JsonValue
    @Override
    public String getValue() {
        return this.value;
    }

    @JsonIgnore
    public String asANSI() {
        return this.ansiFormat.format(this.value);
    }

    @Override
    public String toString() {
        return this.value;
    }

    public Permissions getPermissions() {
        return new Permissions(
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

    // =================================================================================================================
    // CLASSES
    // =================================================================================================================
    public record Permissions(
            boolean disabled,
            boolean startPermitted,
            boolean restartPermitted,
            boolean pausePermitted,
            boolean stopPermitted
    ) { }
}
