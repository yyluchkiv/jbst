package jbst.iam.services.abstracts;

import jbst.foundation.domain.exceptions.tokens.UserEmailConfirmException;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestUserToken;
import jbst.iam.repositories.UsersRepository;
import jbst.iam.repositories.UsersTokensRepository;
import jbst.iam.services.BaseUsersTokensService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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
    public UserToken saveAs(RequestUserToken request) {
        return this.usersTokensRepository.saveAs(request);
    }

    @Override
    public UserToken getOrCreate(RequestUserToken request) {
        var token = this.usersTokensRepository.findByEmailValidOrNull(request);
        if (nonNull(token)) {
            return token;
        } else {
            return this.usersTokensRepository.saveAs(request);
        }
    }

}
