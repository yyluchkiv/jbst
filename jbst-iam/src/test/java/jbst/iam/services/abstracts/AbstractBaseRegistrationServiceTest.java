package jbst.iam.services.abstracts;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.repositories.InvitationsRepository;
import jbst.foundation.repositories.UsersRepository;
import jbst.iam.services.BaseRegistrationService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static jbst.foundation.utilities.random.EntityUtility.entity;
import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static jbst.foundation.utilities.random.RandomUtility.randomZoneId;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractBaseRegistrationServiceTest {
    @Configuration
    static class ContextConfiguration {
        @Bean
        InvitationsRepository invitationsRepository() {
            return mock(InvitationsRepository.class);
        }

        @Bean
        UsersRepository userRepository() {
            return mock(UsersRepository.class);
        }

        @Bean
        BCryptPasswordEncoder bCryptPasswordEncoder() {
            return mock(BCryptPasswordEncoder.class);
        }

        @Bean
        AbstractBaseRegistrationService registrationService() {
            return new AbstractBaseRegistrationService(
                    this.invitationsRepository(),
                    this.userRepository(),
                    this.bCryptPasswordEncoder()
            ) {};
        }
    }

    private final InvitationsRepository invitationsRepository;
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final BaseRegistrationService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.invitationsRepository,
                this.usersRepository,
                this.bCryptPasswordEncoder
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.invitationsRepository,
                this.usersRepository,
                this.bCryptPasswordEncoder
        );
    }

    @Test
    void register0Test() {
        // Arrange
        var requestUserRegistration0 = new RequestUserRegistration0(
                Email.random(),
                Username.random(),
                Password.random(),
                Password.random(),
                randomZoneId()
        );
        var hashPassword = randomString();
        when(this.bCryptPasswordEncoder.encode(requestUserRegistration0.password().value())).thenReturn(hashPassword);

        // Act
        this.componentUnderTest.register0(requestUserRegistration0);

        // Assert
        verify(this.bCryptPasswordEncoder).encode(requestUserRegistration0.password().value());
        verify(this.usersRepository).saveAs(requestUserRegistration0, Password.of(hashPassword));
    }

    @Test
    void register1Test() {
        // Arrange
        var requestUserRegistration1 = new RequestUserRegistration1(
                Username.random(),
                Password.random(),
                Password.random(),
                randomZoneId(),
                randomString()
        );
        var invitation = entity(JbstInvitation.class);
        when(this.invitationsRepository.findByCodeAsAny(requestUserRegistration1.code())).thenReturn(invitation);
        var hashPassword = randomString();
        when(this.bCryptPasswordEncoder.encode(requestUserRegistration1.password().value())).thenReturn(hashPassword);
        var invitationAC1 = ArgumentCaptor.forClass(JbstInvitation.class);
        var invitationAC2 = ArgumentCaptor.forClass(JbstInvitation.class);

        // Act
        this.componentUnderTest.register1(requestUserRegistration1);

        // Assert
        verify(this.invitationsRepository).findByCodeAsAny(requestUserRegistration1.code());
        verify(this.bCryptPasswordEncoder).encode(requestUserRegistration1.password().value());
        verify(this.usersRepository).saveAs(eq(requestUserRegistration1), eq(Password.of(hashPassword)), invitationAC1.capture());
        assertThat(invitationAC1.getValue().invited()).isEqualTo(requestUserRegistration1.username());
        verify(this.invitationsRepository).saveAs(invitationAC2.capture());
        assertThat(invitationAC2.getValue().invited()).isEqualTo(requestUserRegistration1.username());
    }
}
