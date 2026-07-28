package jbst.foundation.services.abstracts;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.JbstRequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.JbstRequestUserPasswordReset;
import jbst.foundation.domain.dto.requests.JbstRequestUserUpdate1;
import jbst.foundation.domain.dto.requests.JbstRequestUserUpdate2;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
import jbst.foundation.domain.properties.configs.JbstPropertySecurity;
import jbst.foundation.domain.security.JbstMagicLinkUserCredentials;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.tests.stubbers.AbstractMockService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
import java.util.stream.Stream;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.random.JbstRandom.randomLongGreaterThanZero;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstAbstractUsersServiceTest {

    private static Stream<Arguments> usersPresenceTest() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(randomLongGreaterThanZero())
        );
    }

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstProperties jbstProperties() {
            return mock(JbstProperties.class);
        }

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
        AbstractMockService abstractMockService() {
            return mock(AbstractMockService.class);
        }

        @Bean
        JbstAbstractUsersService baseUserService() {
            return new JbstAbstractUsersService(
                    this.usersTokensRepository(),
                    this.userRepository(),
                    this.bCryptPasswordEncoder(),
                    this.jbstProperties()
            ) {
                @Override
                public long initUsers(List<JbstPropertyUserOnInit> usersOnInit) {
                    abstractMockService().executeInheritedMethod();
                    return 0;
                }
            };
        }
    }

    private final JbstUsersTokensRepository usersTokensRepository;
    private final JbstUsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JbstProperties jbstProperties;
    private final AbstractMockService abstractMockService;

    private final JbstAbstractUsersService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.usersTokensRepository,
                this.usersRepository,
                this.jbstProperties,
                this.abstractMockService
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.usersTokensRepository,
                this.usersRepository,
                this.jbstProperties,
                this.abstractMockService
        );
    }

    @ParameterizedTest
    @MethodSource("usersPresenceTest")
    void initUsers(long count) {
        // Arrange
        when(this.jbstProperties.getSecurity()).thenReturn(JbstPropertySecurity.fixed());
        when(this.usersRepository.count()).thenReturn(count);

        // Act
        this.componentUnderTest.initUsers();

        // Assert
        verify(this.jbstProperties).getSecurity();
        verify(this.usersRepository).count();
        if (count == 0) {
            verify(this.abstractMockService).executeInheritedMethod();
        }
    }

    @Test
    void findByEmailTest() {
        // Arrange
        var email = Email.fixed();
        var user = JbstJwtUser.fixed();
        when(this.usersRepository.findByEmailAsJwtUserOrNull(email)).thenReturn(user);

        // Act
        var actual = this.componentUnderTest.findByEmail(email);

        // Assert
        assertThat(actual).isEqualTo(user);
        verify(this.usersRepository).findByEmailAsJwtUserOrNull(email);
    }

    @RepeatedTest(5)
    void safeSaveAlreadyExistsTest() {
        // Arrange
        var email = Email.fixed();
        var user = JbstJwtUser.fixed(JbstUserCreationOption.MAGICLINK);
        var magicLinkUserCredentials = new JbstMagicLinkUserCredentials(JbstUserToken.fixedMagicLink(), UKRAINE);
        when(this.usersRepository.findByEmailAsJwtUserOrNull(email)).thenReturn(user);

        // Act
        var credentials = this.componentUnderTest.saveOrGetMagicLinkCredentials(magicLinkUserCredentials);

        // Assert
        assertThat(credentials.username()).isEqualTo(Username.fixed());
        assertThat(credentials.password().value()).hasSize(20);
        verify(this.usersRepository).findByEmailAsJwtUserOrNull(email);
        ArgumentCaptor<Password> passwordAC = ArgumentCaptor.forClass(Password.class);
        verify(this.usersRepository).resetPassword(eq(credentials.username()), passwordAC.capture());
        assertThat(passwordAC.getValue().value()).hasSize(60);
    }

    @Test
    void safeSaveFirstIterationTest() throws JbstExceptions.UsernameAlreadyExist {
        // Arrange
        var creationOption = JbstUserCreationOption.MAGICLINK;
        var email = Email.fixed();
        var magicLinkUserCredentials = new JbstMagicLinkUserCredentials(JbstUserToken.fixedMagicLink(), UKRAINE);
        var user = JbstJwtUser.fixed(JbstUserCreationOption.MAGICLINK);
        var username = email.getUsername();
        when(this.usersRepository.findByEmailAsJwtUserOrNull(email)).thenReturn(null);
        when(this.usersRepository.saveAsOrThrow(eq(creationOption), eq(username), any(Password.class), eq(email), eq(UKRAINE))).thenReturn(user);

        // Act
        var credentials = this.componentUnderTest.saveOrGetMagicLinkCredentials(magicLinkUserCredentials);

        // Assert
        assertThat(credentials.username()).isEqualTo(Username.fixed());
        assertThat(credentials.password().value()).hasSize(20);
        verify(this.usersRepository).findByEmailAsJwtUserOrNull(email);
        verify(this.usersRepository).saveAsOrThrow(eq(creationOption), eq(username), any(Password.class), eq(email), eq(UKRAINE));
        ArgumentCaptor<Password> passwordAC = ArgumentCaptor.forClass(Password.class);
        verify(this.usersRepository).resetPassword(eq(credentials.username()), passwordAC.capture());
        assertThat(passwordAC.getValue().value()).hasSize(60);
    }

    @Test
    void safeSaveSecondIterationTest() throws JbstExceptions.UsernameAlreadyExist {
        // Arrange
        var creationOption = JbstUserCreationOption.MAGICLINK;
        var email = Email.fixed();
        var user = JbstJwtUser.fixed(JbstUserCreationOption.MAGICLINK);
        var baseUsername = email.getUsername();
        var finalUsername = new Username(baseUsername.value() + "0");
        var magicLinkUserCredentials = new JbstMagicLinkUserCredentials(JbstUserToken.fixedMagicLink(), UKRAINE);
        when(this.usersRepository.findByEmailAsJwtUserOrNull(email)).thenReturn(null);
        when(this.usersRepository.saveAsOrThrow(eq(creationOption), eq(baseUsername), any(Password.class), eq(email), eq(UKRAINE))).thenThrow(new JbstExceptions.UsernameAlreadyExist(baseUsername));
        when(this.usersRepository.saveAsOrThrow(eq(creationOption), eq(finalUsername), any(Password.class), eq(email), eq(UKRAINE))).thenReturn(user);

        // Act
        var credentials = this.componentUnderTest.saveOrGetMagicLinkCredentials(magicLinkUserCredentials);

        // Assert
        assertThat(credentials.username()).isEqualTo(Username.fixed());
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
        var request = JbstRequestUserUpdate1.fixed();
        var user = JbstJwtUser.fixed();
        var userAC = ArgumentCaptor.forClass(JbstJwtUser.class);

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
        var request = JbstRequestUserUpdate2.fixed();
        var user = JbstJwtUser.fixed();
        var userAC = ArgumentCaptor.forClass(JbstJwtUser.class);

        // Act
        this.componentUnderTest.updateUser2(user, request);

        // Assert
        verify(this.usersRepository).saveAs(userAC.capture());
        assertThat(userAC.getValue().username()).isEqualTo(user.username());
        assertThat(userAC.getValue().zoneId()).isEqualTo(request.zoneId());
        assertThat(userAC.getValue().name()).isEqualTo(request.name());
        // no verifications on static SecurityContextHolder
    }

    @RepeatedTest(5)
    void changePasswordRequired() {
        // Arrange
        var request = JbstRequestUserChangePasswordBasic.fixed();
        var user = JbstJwtUser.random();
        var userAC = ArgumentCaptor.forClass(JbstJwtUser.class);

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

    @RepeatedTest(5)
    void changePassword1() {
        // Arrange
        var request = JbstRequestUserChangePasswordBasic.fixed();
        var user = JbstJwtUser.fixed();
        var userAC = ArgumentCaptor.forClass(JbstJwtUser.class);

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
        var request = JbstRequestUserPasswordReset.fixed();
        var userToken = JbstUserToken.fixedPasswordReset();
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
