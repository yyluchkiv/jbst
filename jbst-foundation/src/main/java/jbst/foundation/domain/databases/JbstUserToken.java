package jbst.foundation.domain.databases;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.enums.UserTokenType;
import jbst.foundation.domain.ids.TokenId;
import jbst.foundation.utilities.random.RandomUtility;

import static jbst.foundation.domain.enums.UserTokenType.*;
import static jbst.foundation.utilities.time.TimestampUtility.isPast;

public record JbstUserToken(
        TokenId id,
        Email email,
        String value,
        UserTokenType type,
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
                RandomUtility.randomString(),
                RandomUtility.randomEnum(UserTokenType.class),
                RandomUtility.randomLongGreaterThanZero(),
                RandomUtility.randomBoolean()
        );
    }

    public static JbstUserToken randomNotPersisted() {
        return new JbstUserToken(
                null,
                Email.random(),
                RandomUtility.randomString(),
                RandomUtility.randomEnum(UserTokenType.class),
                RandomUtility.randomLongGreaterThanZero(),
                RandomUtility.randomBoolean()
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

    public boolean isInvalid(UserTokenType expected) {
        return !this.type.equals(expected) || this.used || this.isExpired();
    }
}
