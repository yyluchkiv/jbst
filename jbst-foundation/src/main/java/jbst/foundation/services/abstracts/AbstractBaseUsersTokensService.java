package jbst.foundation.services.abstracts;

import jbst.foundation.domain.exceptions.tokens.UserEmailConfirmException;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestUserToken;
import jbst.foundation.repositories.UsersRepository;
import jbst.foundation.repositories.UsersTokensRepository;
import jbst.foundation.services.BaseUsersTokensService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseUsersTokensService implements BaseUsersTokensService {

    // Repositories
    private final UsersTokensRepository usersTokensRepository;
    private final UsersRepository usersRepository;

    @Override
    public void confirmEmail(String token) throws UserEmailConfirmException {
        var userToken = this.usersTokensRepository.findByValueAsAny(token);
        if (isNull(userToken)) {
            throw UserEmailConfirmException.tokenNotFound();
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
    public JbstUserToken getOrCreate(RequestUserToken request) {
        var token = this.usersTokensRepository.findByUserTokenValidOrNull(request);
        if (nonNull(token)) {
            return token;
        } else {
            return this.usersTokensRepository.saveAs(request);
        }
    }
}
