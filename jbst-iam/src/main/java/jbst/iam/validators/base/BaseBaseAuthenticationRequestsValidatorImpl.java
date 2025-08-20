package jbst.iam.validators.base;

import jbst.iam.domain.dto.requests.RequestUserLogin;
import jbst.iam.validators.BaseAuthenticationRequestsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static jbst.foundation.domain.asserts.Asserts.assertNonNullOrThrow;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseBaseAuthenticationRequestsValidatorImpl implements BaseAuthenticationRequestsValidator {

    @Override
    public void validateAuthenticationStandard(RequestUserLogin requestUserLogin) {
        var username = requestUserLogin.username();
        var password = requestUserLogin.password();

        assertNonNullOrThrow(username, invalidAttribute("username"));
        assertNonNullOrThrow(password, invalidAttribute("password"));
    }
}
