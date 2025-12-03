package jbst.foundation.domain.enums;

import java.time.Duration;

import static jbst.foundation.domain.time.TimestampUtility.getFutureTimestamp;

public enum JbstUserTokenType {
    EMAIL_CONFIRMATION,
    PASSWORD_RESET,
    MAGICLINK;

    public boolean isEmailConfirmation() {
        return EMAIL_CONFIRMATION.equals(this);
    }

    public boolean isPasswordReset() {
        return PASSWORD_RESET.equals(this);
    }

    public boolean isMagicLink() {
        return MAGICLINK.equals(this);
    }

    public boolean isEmailConfirmationOrPasswordReset() {
        return this.isEmailConfirmation() || this.isPasswordReset();
    }

    public long getExpiryTimestamp() {
        if (this.isEmailConfirmationOrPasswordReset()) {
            return getFutureTimestamp(Duration.ofHours(24L)).value();
        }
        if (this.isMagicLink()) {
            return getFutureTimestamp(Duration.ofMinutes(10L)).value();
        }
        // fallback
        return getFutureTimestamp(Duration.ofSeconds(1L)).value();
    }
}
