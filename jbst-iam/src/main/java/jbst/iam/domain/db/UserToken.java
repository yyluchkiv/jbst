package jbst.iam.domain.db;

import jbst.foundation.domain.base.Email;
import jbst.foundation.utilities.random.RandomUtility;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.domain.identifiers.TokenId;

import static jbst.foundation.utilities.time.TimestampUtility.isPast;
import static jbst.iam.domain.enums.UserTokenType.EMAIL_CONFIRMATION;

public record UserToken(
        TokenId id,
        Email email,
        String value,
        UserTokenType type,
        long expiryTimestamp,
        boolean used
) {

    public static UserToken hardcodedEmailConfirmation() {
        return new UserToken(
                TokenId.hardcoded(),
                Email.hardcoded(),
                "V2orWAWX4xlvam9V7u5aUqpgriM6qd8qRsgGyqNw",
                EMAIL_CONFIRMATION,
                EMAIL_CONFIRMATION.getExpiryTimestamp(),
                false
        );
    }

    public static UserToken hardcodedPasswordReset() {
        return new UserToken(
                TokenId.hardcoded(),
                Email.hardcoded(),
                "0BF9F5865172B5C7DDE5C84048E8BE8150CFCC4C",
                UserTokenType.PASSWORD_RESET,
                UserTokenType.PASSWORD_RESET.getExpiryTimestamp(),
                false
        );
    }

    public static UserToken random() {
        return new UserToken(
                TokenId.random(),
                Email.random(),
                RandomUtility.randomString(),
                RandomUtility.randomEnum(UserTokenType.class),
                RandomUtility.randomLongGreaterThanZero(),
                RandomUtility.randomBoolean()
        );
    }

    public static UserToken randomNotPersisted() {
        return new UserToken(
                null,
                Email.random(),
                RandomUtility.randomString(),
                RandomUtility.randomEnum(UserTokenType.class),
                RandomUtility.randomLongGreaterThanZero(),
                RandomUtility.randomBoolean()
        );
    }

    public UserToken withUsed(boolean used) {
        return new UserToken(
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
}
