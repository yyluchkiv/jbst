package jbst.foundation.validators.base;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestMagicLinkToken;
import jbst.foundation.domain.dto.requests.RequestUserLogin;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.repositories.JbstUsersTokensRepository;
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
import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.strings.JbstMessages.invalidAttribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstAuthenticationValidatorTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstUsersTokensRepository usersTokensRepository() {
            return mock(JbstUsersTokensRepository.class);
        }

        @Bean
        public JbstAuthenticationValidator authenticationRequestsValidator() {
            return new JbstAuthenticationValidator(
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
                        JbstUserToken.hardcodedEmailConfirmation(),
                        "Invalid magic link token: E4944FFE506B2838A8F667D95C5FB28DB3ABAE54"
                ),
                Arguments.of(
                        RequestMagicLinkToken.hardcoded(),
                        JbstUserToken.hardcodedPasswordReset(),
                        "Invalid magic link token: E4944FFE506B2838A8F667D95C5FB28DB3ABAE54"
                ),
                Arguments.of(
                        RequestMagicLinkToken.hardcoded(),
                        JbstUserToken.hardcodedMagicLink(),
                        null
                )
        );
    }

    // Repositories
    private final JbstUsersTokensRepository usersTokensRepository;

    private final JbstAuthenticationValidator componentUnderTest;

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
        // Act + Assert
        var throwable = catchThrowable(() -> {
            // Act
            var credentials = this.componentUnderTest.validateLoginStandard(request);

            // Assert
            assertThat(credentials.username()).isEqualTo(request.username());
            assertThat(credentials.password()).isEqualTo(request.password());
        });

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
    void validateLoginMagicLinkTest(RequestMagicLinkToken request, JbstUserToken userToken, String exceptionMessage) {
        // Arrange
        when(this.usersTokensRepository.findByValueAsAnyOrNull(request.value())).thenReturn(userToken);

        // Act + Assert
        var throwable = catchThrowable(() -> {
            // Act
            var credentials = this.componentUnderTest.validateLoginMagicLink(request);

            // Assert
            assertThat(credentials.userToken()).isEqualTo(userToken);
            assertThat(credentials.zoneId()).isEqualTo(UKRAINE);
        });

        // Assert
        verify(this.usersTokensRepository).findByValueAsAnyOrNull(request.value());
        if (nonNull(exceptionMessage)) {
            assertThat(throwable)
                    .isInstanceOf(JbstExceptions.Login.class)
                    .hasMessage(exceptionMessage);
        } else {
            assertThat(throwable).isNull();
        }
    }
}
