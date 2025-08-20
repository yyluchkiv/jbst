package jbst.iam.validators.base;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestMagicLinkToken;
import jbst.iam.domain.dto.requests.RequestUserLogin;
import jbst.iam.domain.exceptions.LoginException;
import jbst.iam.repositories.UsersTokensRepository;
import jbst.iam.validators.BaseAuthenticationRequestsValidator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BaseAuthenticationRequestsValidatorImplTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        UsersTokensRepository usersTokensRepository() {
            return mock(UsersTokensRepository.class);
        }

        @Bean
        public BaseAuthenticationRequestsValidator authenticationRequestsValidator() {
            return new BaseBaseAuthenticationRequestsValidatorImpl(
                    this.usersTokensRepository()
            );
        }
    }

    private static Stream<Arguments> validateLoginStandardTest() {
        return Stream.of(
                Arguments.of(
                        new RequestUserLogin(null, Password.of("admin")),
                        invalidAttribute("username")
                ),
                Arguments.of(
                        new RequestUserLogin(Username.of("admin"), null),
                        invalidAttribute("password")
                ),
                Arguments.of(
                        new RequestUserLogin(Username.of("admin"), Password.of("admin")), null
                ),
                Arguments.of(
                        new RequestUserLogin(Username.of("user"), Password.of("password")), null
                )
        );
    }

    private static Stream<Arguments> validateLoginMagicLinkTest() {
        return Stream.of(
                Arguments.of(
                        RequestMagicLinkToken.hardcoded(),
                        null,
                        "Invalid magic link token: E4944FFE506B2838A8F667D95C5FB28DB3ABAE54"
                ),
                Arguments.of(
                        RequestMagicLinkToken.hardcoded(),
                        UserToken.hardcodedEmailConfirmation(),
                        "Invalid magic link token: E4944FFE506B2838A8F667D95C5FB28DB3ABAE54"
                ),
                Arguments.of(
                        RequestMagicLinkToken.hardcoded(),
                        UserToken.hardcodedPasswordReset(),
                        "Invalid magic link token: E4944FFE506B2838A8F667D95C5FB28DB3ABAE54"
                ),
                Arguments.of(
                        RequestMagicLinkToken.hardcoded(),
                        UserToken.hardcodedMagicLink(),
                        null
                )
        );
    }

    // Repositories
    private final UsersTokensRepository usersTokensRepository;

    private final BaseAuthenticationRequestsValidator componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.usersTokensRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.usersTokensRepository
        );
    }

    @ParameterizedTest
    @MethodSource("validateLoginStandardTest")
    void validateLoginStandardTest(RequestUserLogin request, String exceptionMessage) {
        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateLoginStandard(request));

        // Assert
        if (nonNull(exceptionMessage)) {
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(exceptionMessage);
        } else {
            assertThat(throwable).isNull();
        }
    }

    @ParameterizedTest
    @MethodSource("validateLoginMagicLinkTest")
    void validateLoginMagicLinkTest(RequestMagicLinkToken request, UserToken userToken, String exceptionMessage) {
        // Arrange
        when(this.usersTokensRepository.findByValueAsAny(request.value())).thenReturn(userToken);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateLoginMagicLink(request));

        // Assert
        verify(this.usersTokensRepository).findByValueAsAny(request.value());
        if (nonNull(exceptionMessage)) {
            assertThat(throwable)
                    .isInstanceOf(LoginException.class)
                    .hasMessage(exceptionMessage);
        } else {
            assertThat(throwable).isNull();
        }
    }
}
