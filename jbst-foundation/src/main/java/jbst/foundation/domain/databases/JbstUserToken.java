package jbst.foundation.domain.databases;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.enums.JbstUserTokenType;
import jbst.foundation.domain.ids.JbstTokenId;
import jbst.foundation.domain.random.JbstRandom;

import static jbst.foundation.domain.enums.JbstUserTokenType.*;
import static jbst.foundation.domain.time.JbstTime.isPast;

public record JbstUserToken(
        JbstTokenId id,
        Email email,
        String value,
        JbstUserTokenType type,
        long expiryTimestamp,
        boolean used
) {

    public static JbstUserToken fixedEmailConfirmation() {
        return new JbstUserToken(
                JbstTokenId.fixed(),
                Email.fixed(),
                "V2orWAWX4xlvam9V7u5aUqpgriM6qd8qRsgGyqNw",
                EMAIL_CONFIRMATION,
                EMAIL_CONFIRMATION.getExpiryTimestamp(),
                false
        );
    }

    public static JbstUserToken fixedPasswordReset() {
        return new JbstUserToken(
                JbstTokenId.fixed(),
                Email.fixed(),
                "0BF9F5865172B5C7DDE5C84048E8BE8150CFCC4C",
                PASSWORD_RESET,
                PASSWORD_RESET.getExpiryTimestamp(),
                false
        );
    }

    public static JbstUserToken fixedMagicLink() {
        return new JbstUserToken(
                JbstTokenId.fixed(),
                Email.fixed(),
                "B3A85D887DB47A307330C93DC06787EF54A0F46F",
                MAGICLINK,
                MAGICLINK.getExpiryTimestamp(),
                false
        );
    }

    public static JbstUserToken random() {
        return new JbstUserToken(
                JbstTokenId.random(),
                Email.random(),
                JbstRandom.randomString(),
                JbstRandom.randomEnum(JbstUserTokenType.class),
                JbstRandom.randomLongGreaterThanZero(),
                JbstRandom.randomBoolean()
        );
    }

    public static JbstUserToken randomNotPersisted() {
        return new JbstUserToken(
                null,
                Email.random(),
                JbstRandom.randomString(),
                JbstRandom.randomEnum(JbstUserTokenType.class),
                JbstRandom.randomLongGreaterThanZero(),
                JbstRandom.randomBoolean()
        );
    }

    public JbstUserToken withUsed(boolean used) {
        return new JbstUserToken(
                this.id,
                this.email,
                this.value,
                this.type,
                this.expiryTimestamp,
                used
        );
    }

    public boolean isExpired() {
        return isPast(this.expiryTimestamp);
    }

    public boolean isInvalid(JbstUserTokenType expected) {
        return !this.type.equals(expected) || this.used || this.isExpired();
    }
}
