package jbst.iam.utils;

import jbst.foundation.services.emails.domain.EmailHTML;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.functions.FunctionAccountAccessed;
import org.jetbrains.annotations.NotNull;

public interface UserEmailUtils {
    String getSubject(@NotNull String eventName);
    EmailHTML getAccountAccessedHTML(@NotNull FunctionAccountAccessed function);
    EmailHTML getEmailConfirmationHTML(@NotNull UserToken userToken);
    EmailHTML getPasswordResetHTML(@NotNull UserToken userToken);
    EmailHTML getMagicLinkHTML(@NotNull UserToken userToken);
}
