package jbst.iam.domain.functions;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.domain.enums.AccountAccessMethod;
import org.jetbrains.annotations.NotNull;

public record FunctionAccountAccessed(
        @NotNull Username username,
        @NotNull Email to,
        @NotNull UserRequestMetadata userRequestMetadata,
        @NotNull AccountAccessMethod accountAccessMethod
) {

    public static FunctionAccountAccessed hardcoded() {
        return hardcoded(AccountAccessMethod.USERNAME_PASSWORD);
    }

    public static FunctionAccountAccessed hardcoded(AccountAccessMethod accountAccessMethod) {
        return new FunctionAccountAccessed(
                Username.hardcoded(),
                Email.hardcoded(),
                UserRequestMetadata.valid(),
                accountAccessMethod
        );
    }
}
