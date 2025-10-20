package jbst.foundation.services.abstracts;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestUserToken;
import jbst.foundation.domain.exceptions.tokens.JbstUserEmailConfirmException;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.services.JbstUsersTokensService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractJbstUsersTokensService implements JbstUsersTokensService {

    // Repositories
    private final JbstUsersTokensRepository usersTokensRepository;
    private final JbstUsersRepository usersRepository;

    @Override
    public void confirmEmail(String token) throws JbstUserEmailConfirmException {
        var userToken = this.usersTokensRepository.findByValueAsAnyOrNull(token);
        if (isNull(userToken)) {
            throw JbstUserEmailConfirmException.tokenNotFound();
        }
        this.usersRepository.confirmEmail(userToken.email());
        userToken = userToken.withUsed(true);
        this.usersTokensRepository.saveAs(userToken);
    }

    @Override
    public JbstUserToken saveAs(RequestUserToken request) {
        return this.usersTokensRepository.saveAs(request);
    }

    @Override
    public JbstUserToken findOrCreate(RequestUserToken request) {
        return this.usersTokensRepository.findOrCreate(request);
    }
}
