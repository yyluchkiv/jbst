package jbst.foundation.services;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.functions.FunctionAccountAccessed;
import org.springframework.scheduling.annotation.Async;

public interface UsersEmailsService {
    @Async
    void executeMagicLink(JbstUserToken userToken);
    @Async
    void executeEmailConfirmation(JbstUserToken userToken);
    @Async
    void executePasswordReset(JbstUserToken userToken);
    @Async
    void executeAccountAccessed(FunctionAccountAccessed function);
}
