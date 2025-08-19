package jbst.iam.services;

import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.functions.FunctionAccountAccessed;
import org.springframework.scheduling.annotation.Async;

public interface UsersEmailsService {
    @Async
    void executeEmailConfirmation(UserToken userToken);
    @Async
    void executePasswordReset(UserToken userToken);
    @Async
    void executeMagicLinkEmail(UserToken userToken);
    @Async
    void executeAuthenticationLogin(FunctionAccountAccessed function);
    @Async
    void executeSessionRefreshed(FunctionAccountAccessed function);
}
