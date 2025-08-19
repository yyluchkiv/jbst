package jbst.iam.domain.dto.requests;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.time.TimeAmount;
import jbst.iam.domain.enums.UserTokenType;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static jbst.foundation.utilities.time.TimestampUtility.getFutureRange;

public record RequestUserToken(
        Email email,
        UserTokenType type
) {

    public static RequestUserToken hardcoded() {
        return new RequestUserToken(
                Email.hardcoded(),
                UserTokenType.EMAIL_CONFIRMATION
        );
    }

    public long getExpiryTimestamp() {
        if (this.type.isEmailConfirmationOrPasswordReset()) {
            return getFutureRange(new TimeAmount(24, HOURS)).to();
        }
        if (this.type.isMagicLink()) {
            return getFutureRange(new TimeAmount(10, MINUTES)).to();
        }
        // fallback
        return getFutureRange(new TimeAmount(5, MINUTES)).to();
    }
}
