package jbst.iam.services;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.iam.domain.functions.FunctionAccountAccessed;
import org.springframework.scheduling.annotation.Async;

public interface UsersEmailsService {
    @Async
    void executeMagicLink(JbstUserToken userToken);
    @Async
    void executeEmailConfirmation(JbstUserToken userToken);
    @Async
    void executePasswordReset(JbstUserToken userToken);
    @Async
    void executeAuthenticationLogin(FunctionAccountAccessed function);
    @Async
    void executeSessionRefreshed(FunctionAccountAccessed function);
}
