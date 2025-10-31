package jbst.foundation.services.abstracts;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.RequestUserPasswordReset;
import jbst.foundation.domain.dto.requests.RequestUserUpdate1;
import jbst.foundation.domain.dto.requests.RequestUserUpdate2;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.exceptions.base.JbstUsernameAlreadyExistException;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.security.MagicLinkUserCredentials;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.tests.constants.TestsJunitConstants.FIVE_TIMES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractJbstUsersServiceTest {
    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstUsersTokensRepository usersTokensRepository() {
            return mock(JbstUsersTokensRepository.class);
        }

        @Bean
        JbstUsersRepository userRepository() {
            return mock(JbstUsersRepository.class);
        }

        @Bean
        BCryptPasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder(11);
        }

        @Bean
        AbstractJbstUsersService baseUserService() {
            return new AbstractJbstUsersService(
                    this.usersTokensRepository(),
                    this.userRepository(),
                    this.bCryptPasswordEncoder()
            ) {};
        }
    }

    private final JbstUsersTokensRepository usersTokensRepository;
    private final JbstUsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AbstractJbstUsersService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.usersTokensRepository,
                this.usersRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.usersTokensRepository,
                this.usersRepository
        );
    }

    @Test
    void findByEmailTest() {
        // Arrange
        var email = Email.hardcoded();
        var user = JwtUser.hardcoded();
        when(this.usersRepository.findByEmailAsJwtUserOrNull(email)).thenReturn(user);

        // Act
        var actual = this.componentUnderTest.findByEmail(email);

        // Assert
        assertThat(actual).isEqualTo(user);
        verify(this.usersRepository).findByEmailAsJwtUserOrNull(email);
    }

    @RepeatedTest(value = FIVE_TIMES)
    void safeSaveAlreadyExistsTest() {
        // Arrange
        var email = Email.hardcoded();
        var user = JwtUser.hardcoded(JbstUserCreationOption.MAGICLINK);
        var magicLinkUserCredentials = new MagicLinkUserCredentials(JbstUserToken.hardcodedMagicLink(), UKRAINE);
        when(this.usersRepository.findByEmailAsJwtUserOrNull(email)).thenReturn(user);

        // Act
        var credentials = this.componentUnderTest.saveOrGetMagicLinkCredentials(magicLinkUserCredentials);

        // Assert
        assertThat(credentials.username()).isEqualTo(Username.hardcoded());
        assertThat(credentials.password().value()).hasSize(20);
        verify(this.usersRepository).findByEmailAsJwtUserOrNull(email);
        ArgumentCaptor<Password> passwordAC = ArgumentCaptor.forClass(Password.class);
        verify(this.usersRepository).resetPassword(eq(credentials.username()), passwordAC.capture());
        assertThat(passwordAC.getValue().value()).hasSize(60);
    }

    @Test
    void safeSaveFirstIterationTest() throws JbstUsernameAlreadyExistException {
        // Arrange
        var creationOption = JbstUserCreationOption.MAGICLINK;
        var email = Email.hardcoded();
        var magicLinkUserCredentials = new MagicLinkUserCredentials(JbstUserToken.hardcodedMagicLink(), UKRAINE);
        var user = JwtUser.hardcoded(JbstUserCreationOption.MAGICLINK);
        var username = email.getUsername();
        when(this.usersRepository.findByEmailAsJwtUserOrNull(email)).thenReturn(null);
        when(this.usersRepository.saveAsOrThrow(eq(creationOption), eq(username), any(Password.class), eq(email), eq(UKRAINE))).thenReturn(user);

        // Act
        var credentials = this.componentUnderTest.saveOrGetMagicLinkCredentials(magicLinkUserCredentials);

        // Assert
        assertThat(credentials.username()).isEqualTo(Username.hardcoded());
        assertThat(credentials.password().value()).hasSize(20);
        verify(this.usersRepository).findByEmailAsJwtUserOrNull(email);
        verify(this.usersRepository).saveAsOrThrow(eq(creationOption), eq(username), any(Password.class), eq(email), eq(UKRAINE));
        ArgumentCaptor<Password> passwordAC = ArgumentCaptor.forClass(Password.class);
        verify(this.usersRepository).resetPassword(eq(credentials.username()), passwordAC.capture());
        assertThat(passwordAC.getValue().value()).hasSize(60);
    }

    @Test
    void safeSaveSecondIterationTest() throws JbstUsernameAlreadyExistException {
        // Arrange
        var creationOption = JbstUserCreationOption.MAGICLINK;
        var email = Email.hardcoded();
        var user = JwtUser.hardcoded(JbstUserCreationOption.MAGICLINK);
        var baseUsername = email.getUsername();
        var finalUsername = new Username(baseUsername.value() + "0");
        var magicLinkUserCredentials = new MagicLinkUserCredentials(JbstUserToken.hardcodedMagicLink(), UKRAINE);
        when(this.usersRepository.findByEmailAsJwtUserOrNull(email)).thenReturn(null);
        when(this.usersRepository.saveAsOrThrow(eq(creationOption), eq(baseUsername), any(Password.class), eq(email), eq(UKRAINE))).thenThrow(new JbstUsernameAlreadyExistException(baseUsername));
        when(this.usersRepository.saveAsOrThrow(eq(creationOption), eq(finalUsername), any(Password.class), eq(email), eq(UKRAINE))).thenReturn(user);

        // Act
        var credentials = this.componentUnderTest.saveOrGetMagicLinkCredentials(magicLinkUserCredentials);

        // Assert
        assertThat(credentials.username()).isEqualTo(Username.hardcoded());
        assertThat(credentials.password().value()).hasSize(20);
        verify(this.usersRepository).findByEmailAsJwtUserOrNull(email);
        verify(this.usersRepository).saveAsOrThrow(eq(creationOption), eq(baseUsername), any(Password.class), eq(email), eq(UKRAINE));
        verify(this.usersRepository).saveAsOrThrow(eq(creationOption), eq(finalUsername), any(Password.class), eq(email), eq(UKRAINE));
        ArgumentCaptor<Password> passwordAC = ArgumentCaptor.forClass(Password.class);
        verify(this.usersRepository).resetPassword(eq(credentials.username()), passwordAC.capture());
        assertThat(passwordAC.getValue().value()).hasSize(60);
    }

    @Test
    void updateUser1() {
        // Arrange
        var request = RequestUserUpdate1.hardcoded();
        var user = JwtUser.hardcoded();
        var userAC = ArgumentCaptor.forClass(JwtUser.class);

        // Act
        this.componentUnderTest.updateUser1(user, request);

        // Assert
        verify(this.usersRepository).saveAs(userAC.capture());
        assertThat(userAC.getValue().username()).isEqualTo(user.username());
        assertThat(userAC.getValue().zoneId()).isEqualTo(request.zoneId());
        assertThat(userAC.getValue().name()).isEqualTo(request.name());
        assertThat(userAC.getValue().email()).isEqualTo(request.email());
        // no verifications on static SecurityContextHolder
    }

    @Test
    void updateUser2() {
        // Arrange
        var request = RequestUserUpdate2.hardcoded();
        var user = JwtUser.hardcoded();
        var userAC = ArgumentCaptor.forClass(JwtUser.class);

        // Act
        this.componentUnderTest.updateUser2(user, request);

        // Assert
        verify(this.usersRepository).saveAs(userAC.capture());
        assertThat(userAC.getValue().username()).isEqualTo(user.username());
        assertThat(userAC.getValue().zoneId()).isEqualTo(request.zoneId());
        assertThat(userAC.getValue().name()).isEqualTo(request.name());
        // no verifications on static SecurityContextHolder
    }

    @RepeatedTest(FIVE_TIMES)
    void changePasswordRequired() {
        // Arrange
        var request = RequestUserChangePasswordBasic.hardcoded();
        var user = JwtUser.random();
        var userAC = ArgumentCaptor.forClass(JwtUser.class);

        // Act
        this.componentUnderTest.changePasswordRequired(user, request);

        // Assert
        verify(this.usersRepository).saveAs(userAC.capture());
        assertThat(userAC.getValue().passwordChangeRequired()).isFalse();
        assertThat(userAC.getValue().username()).isEqualTo(user.username());
        assertThat(
                this.bCryptPasswordEncoder.matches(
                        request.newPassword().value(),
                        userAC.getValue().getPassword()
                )
        ).isTrue();
        // no verifications on static SecurityContextHolder
    }

    @RepeatedTest(FIVE_TIMES)
    void changePassword1() {
        // Arrange
        var request = RequestUserChangePasswordBasic.hardcoded();
        var user = JwtUser.hardcoded();
        var userAC = ArgumentCaptor.forClass(JwtUser.class);

        // Act
        this.componentUnderTest.changePassword1(user, request);

        // Assert
        verify(this.usersRepository).saveAs(userAC.capture());
        assertThat(userAC.getValue().passwordChangeRequired()).isEqualTo(user.passwordChangeRequired());
        assertThat(userAC.getValue().username()).isEqualTo(user.username());
        assertThat(
                this.bCryptPasswordEncoder.matches(
                        request.newPassword().value(),
                        userAC.getValue().getPassword()
                )
        ).isTrue();
        // no verifications on static SecurityContextHolder
    }

    @Test
    void resetPasswordTest() {
        // Arrange
        var request = RequestUserPasswordReset.hardcoded();
        var userToken = JbstUserToken.hardcodedPasswordReset();
        when(this.usersTokensRepository.findByValueAsAnyOrNull(request.token())).thenReturn(userToken);
        var passwordAC = ArgumentCaptor.forClass(Password.class);

        // Act
        this.componentUnderTest.resetPassword(request);

        // Assert
        verify(this.usersTokensRepository).findByValueAsAnyOrNull(request.token());
        verify(this.usersRepository).resetPassword(eq(userToken.email()), passwordAC.capture());
        verify(this.usersTokensRepository).saveAs(userToken.withUsed(true));
        assertThat(
                this.bCryptPasswordEncoder.matches(
                        request.newPassword().value(),
                        passwordAC.getValue().value()
                )
        ).isTrue();
    }
}
