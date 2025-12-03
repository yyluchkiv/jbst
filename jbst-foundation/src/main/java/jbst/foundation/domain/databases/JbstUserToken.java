package jbst.foundation.domain.databases;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.enums.JbstUserTokenType;
import jbst.foundation.domain.ids.TokenId;
import jbst.foundation.domain.random.JbstRandom;

import static jbst.foundation.domain.enums.JbstUserTokenType.*;
import static jbst.foundation.domain.time.TimestampUtility.isPast;

public record JbstUserToken(
        TokenId id,
        Email email,
        String value,
        JbstUserTokenType type,
        long expiryTimestamp,
        boolean used
) {

    public static JbstUserToken hardcodedEmailConfirmation() {
        return new JbstUserToken(
                TokenId.hardcoded(),
                Email.hardcoded(),
                "V2orWAWX4xlvam9V7u5aUqpgriM6qd8qRsgGyqNw",
                EMAIL_CONFIRMATION,
                EMAIL_CONFIRMATION.getExpiryTimestamp(),
                false
        );
    }

    public static JbstUserToken hardcodedPasswordReset() {
        return new JbstUserToken(
                TokenId.hardcoded(),
                Email.hardcoded(),
                "0BF9F5865172B5C7DDE5C84048E8BE8150CFCC4C",
                PASSWORD_RESET,
                PASSWORD_RESET.getExpiryTimestamp(),
                false
        );
    }

    public static JbstUserToken hardcodedMagicLink() {
        return new JbstUserToken(
                TokenId.hardcoded(),
                Email.hardcoded(),
                "B3A85D887DB47A307330C93DC06787EF54A0F46F",
                MAGICLINK,
                MAGICLINK.getExpiryTimestamp(),
                false
        );
    }

    public static JbstUserToken random() {
        return new JbstUserToken(
                TokenId.random(),
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
