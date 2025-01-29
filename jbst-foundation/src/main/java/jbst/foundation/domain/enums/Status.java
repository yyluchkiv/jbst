package jbst.foundation.domain.enums;

import com.diogonunes.jcolor.AnsiFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static jbst.foundation.utilities.colors.AnsiUtility.getBoldHexAnsiFormat;

// Lombok
@AllArgsConstructor
@Getter
public enum Status {
    // MAIN
    ENABLED("ENABLED", getBoldHexAnsiFormat("#000000")),
    DISABLED("DISABLED", getBoldHexAnsiFormat("#000000")),

    SCHEDULED("SCHEDULED", getBoldHexAnsiFormat("#0000FF")),
    STARTED("STARTED", getBoldHexAnsiFormat("#0000FF")),
    COMPLETED("COMPLETED", getBoldHexAnsiFormat("#008000")),

    FAILURE("FAILURE", getBoldHexAnsiFormat("#FF0000")),
    SUCCESS("SUCCESS", getBoldHexAnsiFormat("#008000")),

    // PROGRESS

    PROGRESS_0("PROGRESS: 0%", getBoldHexAnsiFormat("#DAA520")),
    PROGRESS_20("PROGRESS: 20%", getBoldHexAnsiFormat("#DAA520")),
    PROGRESS_25("PROGRESS: 25%", getBoldHexAnsiFormat("#DAA520")),
    PROGRESS_33("PROGRESS: 33%", getBoldHexAnsiFormat("#DAA520")),
    PROGRESS_40("PROGRESS: 40%", getBoldHexAnsiFormat("#DAA520")),
    PROGRESS_50("PROGRESS: 50%", getBoldHexAnsiFormat("#DAA520")),
    PROGRESS_60("PROGRESS: 60%", getBoldHexAnsiFormat("#DAA520")),
    PROGRESS_66("PROGRESS: 66%", getBoldHexAnsiFormat("#DAA520")),
    PROGRESS_75("PROGRESS: 75%", getBoldHexAnsiFormat("#DAA520")),
    PROGRESS_80("PROGRESS: 80%", getBoldHexAnsiFormat("#DAA520"));

    public static Status of(boolean toggle) {
        if (toggle) {
            return Status.ENABLED;
        }
        return Status.DISABLED;
    }

    private final String value;
    private final AnsiFormat ansiFormat;

    @Override
    public String toString() {
        return this.value;
    }

    @JsonIgnore
    public String asANSI() {
        return this.ansiFormat.format(this.value);
    }

    public boolean isStarted() {
        return STARTED.equals(this);
    }

    public boolean isCompleted() {
        return COMPLETED.equals(this);
    }
}
