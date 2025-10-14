package jbst.foundation.validators;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.RequestUserUpdate1;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.repositories.UsersRepository;
import jbst.foundation.validators.abtracts.AbstractBaseUsersValidator;
import jbst.iam.configurations.TestConfigurationValidators;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.entityAlreadyUsed;
import static jbst.foundation.utilities.random.EntityUtility.entity;
import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static jbst.foundation.utilities.random.RandomUtility.randomZoneId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractBaseUsersValidatorTest {

    private static Stream<Arguments> validateUserChangePasswordRequestBasicArgs() {
        return Stream.of(
                Arguments.of(new RequestUserChangePasswordBasic(Password.of("simple"), Password.of(randomString())), "Passwords must be same"),
                Arguments.of(new RequestUserChangePasswordBasic(Password.of("Simple"), Password.of(randomString())), "Passwords must be same"),
                Arguments.of(new RequestUserChangePasswordBasic(Password.of("Simple1"), Password.of(randomString())), "Passwords must be same"),
                Arguments.of(new RequestUserChangePasswordBasic(Password.of("ComPLEx12"), Password.of("NoMatch")), "Passwords must be same"),
                Arguments.of(new RequestUserChangePasswordBasic(Password.of("ComPLEx12"), Password.of("ComPLEx12")), null)
        );
    }

    @Configuration
    @Import({
            TestConfigurationValidators.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final UsersRepository usersRepository;

        @Bean
        BaseUsersValidator baseUsersValidator() {
            return new AbstractBaseUsersValidator(
                    this.usersRepository
            ) {};
        }
    }

    private final UsersRepository usersRepository;

    private final BaseUsersValidator componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.usersRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.usersRepository
        );
    }

    @Test
    void validateUserUpdateRequest1EmailValidNoUserTest() {
        // Arrange
        var username = entity(Username.class);
        var email = Email.random();
        when(this.usersRepository.findByEmailAsJwtUserOrNull(email)).thenReturn(null);
        var requestUserUpdate1 = new RequestUserUpdate1(randomZoneId(), email, randomString());

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateUserUpdateRequest1(username, requestUserUpdate1));

        // Assert
        assertThat(throwable).isNull();
        verify(this.usersRepository).findByEmailAsJwtUserOrNull(email);
    }

    @Test
    void validateUserUpdateRequest1EmailValidUserFoundTest() {
        // Arrange
        var user= entity(JwtUser.class);
        var email = Email.random();
        when(this.usersRepository.findByEmailAsJwtUserOrNull(email)).thenReturn(user);
        var requestUserUpdate1 = new RequestUserUpdate1(randomZoneId(), email, randomString());

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateUserUpdateRequest1(user.username(), requestUserUpdate1));

        // Assert
        assertThat(throwable).isNull();
        verify(this.usersRepository).findByEmailAsJwtUserOrNull(email);
    }

    @Test
    void validateUserUpdateRequest1EmailValidTwoUsersTest() {
        // Arrange
        var username = Username.random();
        var user = JwtUser.hardcoded();
        when(this.usersRepository.findByEmailAsJwtUserOrNull(user.email())).thenReturn(user);
        var requestUserUpdate1 = new RequestUserUpdate1(randomZoneId(), user.email(), randomString());

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateUserUpdateRequest1(username, requestUserUpdate1));

        // Assert
        verify(this.usersRepository).findByEmailAsJwtUserOrNull(user.email());
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(entityAlreadyUsed("Email", user.email().value()));
    }

    @ParameterizedTest
    @MethodSource("validateUserChangePasswordRequestBasicArgs")
    void validateUserChangePasswordRequestBasic(RequestUserChangePasswordBasic request, String exceptionMessage) {
        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateUserChangePasswordRequestBasic(request));

        // Assert
        if (nonNull(exceptionMessage)) {
            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
            assertThat(throwable.getMessage()).isEqualTo(exceptionMessage);
        } else {
            assertThat(throwable).isNull();
        }
    }
}
