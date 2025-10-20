package jbst.foundation.domain.enums;

import jbst.foundation.domain.time.TimeAmount;

import static java.time.temporal.ChronoUnit.*;
import static jbst.foundation.utilities.time.TimestampUtility.getFutureRange;

public enum UserTokenType {
    EMAIL_CONFIRMATION,
    PASSWORD_RESET,
    MAGIC_LINK; // TODO [YYL] rename

    public boolean isEmailConfirmation() {
        return EMAIL_CONFIRMATION.equals(this);
    }

    public boolean isPasswordReset() {
        return PASSWORD_RESET.equals(this);
    }

    public boolean isMagicLink() {
        return MAGIC_LINK.equals(this);
    }

    public boolean isEmailConfirmationOrPasswordReset() {
        return this.isEmailConfirmation() || this.isPasswordReset();
    }

    public long getExpiryTimestamp() {
        if (this.isEmailConfirmationOrPasswordReset()) {
            return getFutureRange(new TimeAmount(24, HOURS)).to();
        }
        if (this.isMagicLink()) {
            return getFutureRange(new TimeAmount(10, MINUTES)).to();
        }
        // fallback
        return getFutureRange(new TimeAmount(1, SECONDS)).to();
    }
}
