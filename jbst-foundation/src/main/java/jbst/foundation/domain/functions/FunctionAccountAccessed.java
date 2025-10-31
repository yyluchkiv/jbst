package jbst.foundation.domain.functions;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.enums.JbstAccountAccessMethod;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.domain.tuples.Tuple2;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public record FunctionAccountAccessed(
        @NotNull Username username,
        @NotNull Email to,
        @NotNull UserRequestMetadata userRequestMetadata,
        @NotNull JbstAccountAccessMethod accountAccessMethod
) {

    public static FunctionAccountAccessed hardcoded() {
        return hardcoded(JbstAccountAccessMethod.USERNAME_PASSWORD);
    }

    public static FunctionAccountAccessed hardcoded(JbstAccountAccessMethod accountAccessMethod) {
        return new FunctionAccountAccessed(
                Username.hardcoded(),
                Email.hardcoded(),
                UserRequestMetadata.valid(),
                accountAccessMethod
        );
    }

    public String getTemplateName(Function<Tuple2<String, String>, String> templateNameFNC) {
        var jbstTemplateName = "jbst-account-accessed";
        if (this.accountAccessMethod.isUsernamePassword()) {
            return templateNameFNC.apply(new Tuple2<>(
                    "server-account-accessed-username-password",
                    jbstTemplateName
            ));
        }
        if (this.accountAccessMethod.isSessionToken()) {
            return templateNameFNC.apply(new Tuple2<>(
                    "server-account-accessed-session-token",
                    jbstTemplateName
            ));
        }
        return jbstTemplateName;
    }
}
