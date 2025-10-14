package jbst.iam.utils;

import jbst.foundation.services.emails.domain.EmailHTML;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.iam.domain.functions.FunctionAccountAccessed;
import org.jetbrains.annotations.NotNull;

public interface UserEmailUtils {
    String getSubject(@NotNull String eventName);
    EmailHTML getAccountAccessedHTML(@NotNull FunctionAccountAccessed function);
    EmailHTML getMagicLinkHTML(@NotNull JbstUserToken userToken);
    EmailHTML getEmailConfirmationHTML(@NotNull JbstUserToken userToken);
    EmailHTML getPasswordResetHTML(@NotNull JbstUserToken userToken);
}
