package jbst.iam.domain.db;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.time.TimeAmount;
import jbst.foundation.utilities.random.RandomUtility;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.domain.identifiers.TokenId;

import java.time.temporal.ChronoUnit;

import static jbst.foundation.utilities.time.TimestampUtility.getFutureRange;
import static jbst.foundation.utilities.time.TimestampUtility.isPast;

public record UserToken(
        TokenId id,
        Email email,
        Username username,
        String value,
        UserTokenType type,
        long expiryTimestamp,
        boolean used
) {

    public static UserToken hardcodedEmailConfirmation() {
        return new UserToken(
                TokenId.hardcoded(),
                Email.hardcoded(),
                Username.hardcoded(),
                "V2orWAWX4xlvam9V7u5aUqpgriM6qd8qRsgGyqNw",
                UserTokenType.EMAIL_CONFIRMATION,
                getFutureRange(new TimeAmount(24, ChronoUnit.HOURS)).to(),
                false
        );
    }

    public static UserToken hardcodedPasswordReset() {
        return new UserToken(
                TokenId.hardcoded(),
                Email.hardcoded(),
                Username.hardcoded(),
                "0BF9F5865172B5C7DDE5C84048E8BE8150CFCC4C",
                UserTokenType.PASSWORD_RESET,
                getFutureRange(new TimeAmount(24, ChronoUnit.HOURS)).to(),
                false
        );
    }

    public static UserToken random() {
        return new UserToken(
                TokenId.random(),
                Email.random(),
                Username.random(),
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
                Username.random(),
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
                this.username,
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
