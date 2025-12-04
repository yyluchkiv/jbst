package jbst.foundation.validators.abstracts;

import jbst.foundation.configurations.TestConfigurationValidators;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestUserPasswordReset;
import jbst.foundation.domain.enums.JbstUserTokenType;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.ids.JbstTokenId;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.time.TimestampUtility;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.validators.JbstUsersTokensValidator;
import jbst.foundation.validators.abtracts.AbstractJbstUsersTokensValidator;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.Duration;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.random.JbstRandom.randomStringLetterOrNumbersOnly;
import static jbst.foundation.domain.time.JbstTime.getFutureTimestamp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractJbstUsersTokensValidatorTest {

    private static Stream<Arguments> validateExecuteConfirmEmailTest() {
        return Stream.of(
                Arguments.of(
                        JwtUser.hardcoded(Email.hardcoded(), JbstUserEmailDetails.required()),
                        null
                ),
                Arguments.of(
                        JwtUser.hardcoded(null, JbstUserEmailDetails.unnecessary()),
                        new IllegalArgumentException("User email already confirmed")
                ),
                Arguments.of(
                        JwtUser.hardcoded(null, JbstUserEmailDetails.confirmed()),
                        new IllegalArgumentException("User email already confirmed")
                ),
                Arguments.of(
                        JwtUser.hardcoded(null, JbstUserEmailDetails.required()),
                        new IllegalArgumentException("User email is missing")
                ),
                Arguments.of(
                        JwtUser.hardcoded(Email.hardcoded(), JbstUserEmailDetails.unnecessary()),
                        new IllegalArgumentException("User email already confirmed")
                ),
                Arguments.of(
                        JwtUser.hardcoded(Email.hardcoded(), JbstUserEmailDetails.confirmed()),
                        new IllegalArgumentException("User email already confirmed")
                )
        );
    }

    private static Stream<Arguments> validateEmailConfirmationTokenTest() {
        var oneDay = Duration.ofHours(24L);
        var expiredTimestamp = TimestampUtility.getPastTimestamp(oneDay).value();
        var validTimestamp = getFutureTimestamp(oneDay).value();
        return Stream.of(
                Arguments.of(
                        null,
                        JbstExceptions.UserTokenValidation.notFound()
                ),
                Arguments.of(
                        new JbstUserToken(
                                JbstTokenId.random(),
                                Email.random(),
                                randomStringLetterOrNumbersOnly(255),
                                JbstUserTokenType.EMAIL_CONFIRMATION,
                                validTimestamp,
                                true
                        ),
                        JbstExceptions.UserTokenValidation.used()
                ),
                Arguments.of(
                        new JbstUserToken(
                                JbstTokenId.random(),
                                Email.random(),
                                randomStringLetterOrNumbersOnly(255),
                                JbstUserTokenType.EMAIL_CONFIRMATION,
                                expiredTimestamp,
                                false
                        ),
                        JbstExceptions.UserTokenValidation.expired()
                ),
                Arguments.of(
                        new JbstUserToken(
                                JbstTokenId.random(),
                                Email.random(),
                                randomStringLetterOrNumbersOnly(255),
                                JbstUserTokenType.PASSWORD_RESET,
                                validTimestamp,
                                false
                        ),
                        JbstExceptions.UserTokenValidation.invalidType()
                ),
                Arguments.of(
                        new JbstUserToken(
                                JbstTokenId.random(),
                                Email.random(),
                                randomStringLetterOrNumbersOnly(255),
                                JbstUserTokenType.EMAIL_CONFIRMATION,
                                validTimestamp,
                                false
                        ),
                        null
                )
        );
    }

    private static Stream<Arguments> validateExecuteResetPasswordTest() {
        return Stream.of(
                Arguments.of(
                        null,
                        JbstExceptions.PasswordReset.userNotFound()
                ),
                Arguments.of(
                        JwtUser.hardcoded(Email.hardcoded(), JbstUserEmailDetails.unnecessary()),
                        null
                ),
                Arguments.of(
                        JwtUser.hardcoded(Email.hardcoded(), JbstUserEmailDetails.required()),
                        JbstExceptions.PasswordReset.emailNotConfirmed()
                ),
                Arguments.of(
                        JwtUser.hardcoded(Email.hardcoded(), JbstUserEmailDetails.confirmed()),
                        null
                ),
                Arguments.of(
                        JwtUser.hardcoded(null, JbstUserEmailDetails.unnecessary()),
                        JbstExceptions.PasswordReset.emailMissing()
                ),
                Arguments.of(
                        JwtUser.hardcoded(null, JbstUserEmailDetails.required()),
                        JbstExceptions.PasswordReset.emailMissing()
                ),
                Arguments.of(
                        JwtUser.hardcoded(null, JbstUserEmailDetails.confirmed()),
                        JbstExceptions.PasswordReset.emailMissing()
                )
        );
    }

    private static Stream<Arguments> validatePasswordResetTest() {
        var oneDay = Duration.ofHours(24L);
        var expiredTimestamp = TimestampUtility.getPastTimestamp(oneDay).value();
        var validTimestamp = getFutureTimestamp(oneDay).value();
        return Stream.of(
                Arguments.of(
                        RequestUserPasswordReset.hardcoded(),
                        null,
                        JbstExceptions.UserTokenValidation.notFound()
                ),
                Arguments.of(
                        new RequestUserPasswordReset(
                                randomStringLetterOrNumbersOnly(255),
                                Password.of("655c0667533246a9afdb197466001934"),
                                Password.of("e4f937b04d9f44519ed58346b9aa67ff")

                        ),
                        JbstUserToken.hardcodedPasswordReset(),
                        new IllegalArgumentException("Passwords must be same")
                ),
                Arguments.of(
                        RequestUserPasswordReset.hardcoded(),
                        new JbstUserToken(
                                JbstTokenId.random(),
                                Email.random(),
                                randomStringLetterOrNumbersOnly(255),
                                JbstUserTokenType.PASSWORD_RESET,
                                validTimestamp,
                                true
                        ),
                        JbstExceptions.UserTokenValidation.used()
                ),
                Arguments.of(
                        RequestUserPasswordReset.hardcoded(),
                        new JbstUserToken(
                                JbstTokenId.random(),
                                Email.random(),
                                randomStringLetterOrNumbersOnly(255),
                                JbstUserTokenType.PASSWORD_RESET,
                                expiredTimestamp,
                                false
                        ),
                        JbstExceptions.UserTokenValidation.expired()
                ),
                Arguments.of(
                        RequestUserPasswordReset.hardcoded(),
                        new JbstUserToken(
                                JbstTokenId.random(),
                                Email.random(),
                                randomStringLetterOrNumbersOnly(255),
                                JbstUserTokenType.EMAIL_CONFIRMATION,
                                validTimestamp,
                                false
                        ),
                        JbstExceptions.UserTokenValidation.invalidType()
                ),
                Arguments.of(
                        RequestUserPasswordReset.hardcoded(),
                        new JbstUserToken(
                                JbstTokenId.random(),
                                Email.random(),
                                randomStringLetterOrNumbersOnly(255),
                                JbstUserTokenType.PASSWORD_RESET,
                                validTimestamp,
                                false
                        ),
                        null
                )
        );
    }

    @Configuration
    @Import({
            TestConfigurationValidators.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstUsersTokensRepository usersTokensRepository;

        @Bean
        JbstUsersTokensValidator baseUsersEmailsTokensRequestsValidator() {
            return new AbstractJbstUsersTokensValidator(
                    this.usersTokensRepository
            ) {};
        }

    }

    private final JbstUsersTokensRepository usersTokensRepository;

    private final JbstUsersTokensValidator componentUnderTest;

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
    @MethodSource("validateExecuteConfirmEmailTest")
    void validateExecuteConfirmEmailTest(JwtUser user, IllegalArgumentException expected) {
        // Act
        var actual = catchThrowable(() -> this.componentUnderTest.validateExecuteConfirmEmail(user));

        if (nonNull(expected)) {
            assertThat(actual)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expected.getMessage());
        } else {
            assertThat(actual).isNull();
        }
    }

    @ParameterizedTest
    @MethodSource("validateEmailConfirmationTokenTest")
    void validateEmailConfirmationTokenTest(
            JbstUserToken userToken,
            JbstExceptions.UserTokenValidation expected
    ) {
        // Arrange
        var token = randomStringLetterOrNumbersOnly(255);
        when(this.usersTokensRepository.findByValueAsAnyOrNull(token)).thenReturn(userToken);

        // Act
        var actual = catchThrowable(() -> this.componentUnderTest.validateEmailConfirmationToken(token));

        // Assert
        verify(this.usersTokensRepository).findByValueAsAnyOrNull(token);
        if (nonNull(expected)) {
            assertThat(actual)
                    .isInstanceOf(JbstExceptions.UserTokenValidation.class)
                    .hasMessage(expected.getMessage());
        } else {
            assertThat(actual).isNull();
        }
    }

    @ParameterizedTest
    @MethodSource("validateExecuteResetPasswordTest")
    void validateExecuteResetPasswordTest(JwtUser user, JbstExceptions.PasswordReset expected) {
        // Act
        var actual = catchThrowable(() -> this.componentUnderTest.validateExecuteResetPassword(user));

        if (nonNull(expected)) {
            assertThat(actual)
                    .isInstanceOf(JbstExceptions.PasswordReset.class)
                    .hasMessage(expected.getMessage());
        } else {
            assertThat(actual).isNull();
        }
    }

    @ParameterizedTest
    @MethodSource("validatePasswordResetTest")
    void validatePasswordResetTest(
            RequestUserPasswordReset request,
            JbstUserToken userToken,
            Exception expected
    ) {
        // Arrange
        var token = request.token();
        when(this.usersTokensRepository.findByValueAsAnyOrNull(request.token())).thenReturn(userToken);

        // Act
        var actual = catchThrowable(() -> this.componentUnderTest.validatePasswordReset(request));

        // Assert
        if (expected instanceof JbstExceptions.UserTokenValidation) {
            verify(this.usersTokensRepository).findByValueAsAnyOrNull(token);
            assertThat(actual).hasMessage(expected.getMessage());
        } else if (expected instanceof IllegalArgumentException) {
            verify(this.usersTokensRepository, never()).findByValueAsAnyOrNull(token);
            assertThat(actual).hasMessage(expected.getMessage());
        } else {
            verify(this.usersTokensRepository).findByValueAsAnyOrNull(token);
            assertThat(actual).isNull();
        }
    }

}
