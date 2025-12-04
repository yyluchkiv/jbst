package jbst.foundation.services.abstracts;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.JbstRequestUserToken;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.services.JbstUsersTokensService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class JbstAbstractUsersTokensService implements JbstUsersTokensService {

    // Repositories
    private final JbstUsersTokensRepository usersTokensRepository;
    private final JbstUsersRepository usersRepository;

    @Override
    public void confirmEmail(String token) throws JbstExceptions.UserEmailConfirmation {
        var userToken = this.usersTokensRepository.findByValueAsAnyOrNull(token);
        if (isNull(userToken)) {
            throw new JbstExceptions.UserEmailConfirmation();
        }
        this.usersRepository.confirmEmail(userToken.email());
        userToken = userToken.withUsed(true);
        this.usersTokensRepository.saveAs(userToken);
    }

    @Override
    public JbstUserToken saveAs(JbstRequestUserToken request) {
        return this.usersTokensRepository.saveAs(request);
    }

    @Override
    public JbstUserToken findOrCreate(JbstRequestUserToken request) {
        return this.usersTokensRepository.findOrCreate(request);
    }
}
