package jbst.foundation.services.abstracts;

import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestUserToken;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.random.JbstRandom;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersTokensRepository;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractJbstUsersTokensServiceTest {

    private static Stream<Arguments> confirmEmailTest() {
        return Stream.of(
                Arguments.of(
                        null,
                        new JbstExceptions.UserEmailConfirmation()
                ),
                Arguments.of(
                        JbstUserToken.random(),
                        null
                )
        );
    }

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {

        @Bean
        JbstUsersTokensRepository usersTokensRepository() {
            return mock(JbstUsersTokensRepository.class);
        }

        @Bean
        JbstUsersRepository usersRepository() {
            return mock(JbstUsersRepository.class);
        }

        @Bean
        AbstractJbstUsersTokensService abstractBaseUsersTokensService() {
            return new AbstractJbstUsersTokensService(
                    this.usersTokensRepository(),
                    this.usersRepository()
            ) {};
        }
    }

    private final JbstUsersTokensRepository usersTokensRepository;
    private final JbstUsersRepository usersRepository;

    private final AbstractJbstUsersTokensService componentUnderTest;

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

    @SuppressWarnings("DataFlowIssue")
    @ParameterizedTest
    @MethodSource("confirmEmailTest")
    void confirmEmailTest(
            JbstUserToken userToken,
            JbstExceptions.UserEmailConfirmation exception
    ) {
        // Arrange
        var token = JbstRandom.randomStringLetterOrNumbersOnly(36);
        when(this.usersTokensRepository.findByValueAsAnyOrNull(token)).thenReturn(userToken);
        var email = nonNull(userToken) ? userToken.email() : null;

        // Act
        var actual = catchThrowable(() -> this.componentUnderTest.confirmEmail(token));

        // Assert
        verify(this.usersTokensRepository).findByValueAsAnyOrNull(token);
        if (nonNull(exception)) {
            assertThat(actual)
                    .isInstanceOf(JbstExceptions.UserEmailConfirmation.class)
                    .hasMessage(exception.getMessage());
        } else {
            verify(this.usersRepository).confirmEmail(email);
            verify(this.usersTokensRepository).saveAs(userToken.withUsed(true));
        }
    }

    @Test
    void saveAsTest() {
        // Arrange
        var request = RequestUserToken.hardcoded();
        var userToken = JbstUserToken.hardcodedEmailConfirmation();
        when(this.usersTokensRepository.saveAs(request)).thenReturn(userToken);

        // Act
        var actual = this.componentUnderTest.saveAs(request);

        // Assert
        assertThat(actual).isEqualTo(userToken);
        verify(this.usersTokensRepository).saveAs(request);
    }

    @Test
    void findOrCreateTest() {
        // Arrange
        var request = RequestUserToken.hardcoded();

        // Arrange
        this.componentUnderTest.findOrCreate(request);

        // Arrange
        verify(this.usersTokensRepository).findOrCreate(request);
    }
}
