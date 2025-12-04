package jbst.foundation.resources.base;

import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.configurations.TestRunnerResources1;
import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.JbstRequestUserEmail;
import jbst.foundation.domain.dto.requests.JbstRequestUserPasswordReset;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.random.JbstRandom;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.services.JbstUsersService;
import jbst.foundation.services.JbstUsersTokensService;
import jbst.foundation.services.base.JbstUsersEmailsService;
import jbst.foundation.validators.JbstUsersTokensValidator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.stubbing.Stubber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstUsersTokensResourceTest extends TestRunnerResources1 {

    private static Stream<Arguments> confirmEmailTest() {
        return Stream.of(
                Arguments.of(
                        JbstExceptions.UserTokenValidation.expired(), null, null, 0
                ),
                Arguments.of(
                        null, new JbstExceptions.UserEmailConfirmation(), null, 0
                ),
                Arguments.of(
                        null, null, new IllegalArgumentException(), 0
                ),
                Arguments.of(
                        null, null, null, 1
                )
        );
    }

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final JbstUsersTokensService usersTokensService;
    private final JbstUsersService usersService;
    private final JbstUsersEmailsService usersEmailsService;
    // Validators
    private final JbstUsersTokensValidator usersTokensValidator;
    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;

    // Resource
    private final JbstUsersTokensResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.currentSessionAssistant,
                this.usersTokensService,
                this.usersEmailsService,
                this.usersService,
                this.usersTokensValidator,
                this.incidentsPublisher
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.currentSessionAssistant,
                this.usersTokensService,
                this.usersEmailsService,
                this.usersService,
                this.usersTokensValidator,
                this.incidentsPublisher
        );
    }

    @Test
    void executeConfirmEmail() throws Exception {
        // Arrange
        var user = JbstJwtUser.hardcoded();
        when(this.currentSessionAssistant.getCurrentJwtUser()).thenReturn(user);
        var requestUserToken = user.getRequestUserTokenAsEmailConfirmation();
        var userToken = JbstUserToken.hardcodedEmailConfirmation();
        when(this.usersTokensService.findOrCreate(eq(requestUserToken))).thenReturn(userToken);

        // Act
        this.mvc.perform(
                post("/tokens/email/confirm")
        ).andExpect(status().isOk());
        this.mvc.perform(
                post("/tokens/email/confirm")
        ).andExpect(status().isTooManyRequests());

        // Arrange
        verify(this.currentSessionAssistant, times(2)).getCurrentJwtUser();
        verify(this.usersTokensValidator, times(2)).validateExecuteConfirmEmail(user);
        verify(this.usersTokensService).findOrCreate(eq(requestUserToken));
        verify(this.usersEmailsService).executeEmailConfirmation(userToken);
    }

    @ParameterizedTest
    @MethodSource("confirmEmailTest")
    void confirmEmailTest(
            JbstExceptions.UserTokenValidation validationException,
            JbstExceptions.UserEmailConfirmation confirmException,
            RuntimeException runtimeException,
            int code
    ) throws Exception {
        // Arrange
        var token = JbstRandom.randomStringLetterOrNumbersOnly(36);
        Function<Exception, Stubber> doThrowNonNull = ex -> nonNull(ex) ? doThrow(ex) : doNothing();
        doThrowNonNull.apply(validationException).when(this.usersTokensValidator).validateEmailConfirmationToken(token);
        doThrowNonNull.apply(confirmException).when(this.usersTokensService).confirmEmail(token);
        if (nonNull(runtimeException)) {
            doThrow(runtimeException).when(this.usersTokensService).confirmEmail(token);
        }
        var expectedLocation = "http://127.0.0.1:3000/email-confirmation?code=" + code;

        // Act
        this.mvc.perform(
                        get("/tokens/email/confirm?token=%s".formatted(token))
                ).andExpect(status().isFound())
                .andExpect(header().string("Location", expectedLocation));

        // Assert
        verify(this.usersTokensValidator).validateEmailConfirmationToken(token);
        var tokensServiceInvoked = Stream.of(nonNull(confirmException), nonNull(runtimeException), code == 1).anyMatch(b -> b);
        var confirmEmailInvocations = tokensServiceInvoked ? times(1) : times(0);
        verify(this.usersTokensService, confirmEmailInvocations).confirmEmail(token);
        if (nonNull(runtimeException)) {
            verify(this.incidentsPublisher).publishThrowable(any());
        }
    }

    @Test
    void executeResetPasswordTest() throws Exception {
        // Arrange
        var request = JbstRequestUserEmail.hardcoded();
        var user = JbstJwtUser.hardcoded(request.email(), JbstUserEmailDetails.confirmed());
        when(this.usersService.findByEmail(request.email())).thenReturn(user);
        var requestUserToken = user.getRequestUserTokenAsPasswordReset();
        var userToken = JbstUserToken.hardcodedPasswordReset();
        when(this.usersTokensService.findOrCreate(requestUserToken)).thenReturn(userToken);

        // Act
        this.mvc.perform(
                post("/tokens/password/reset")
                        .content(this.objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // Assert
        verify(this.usersService).findByEmail(request.email());
        verify(this.usersTokensValidator).validateExecuteResetPassword(user);
        verify(this.usersTokensService).findOrCreate(requestUserToken);
        verify(this.usersEmailsService).executePasswordReset(userToken);
    }

    @Test
    void resetPasswordTest() throws Exception {
        // Arrange
        var request = JbstRequestUserPasswordReset.hardcoded();

        // Act
        this.mvc.perform(
                patch("/tokens/password/reset")
                        .content(this.objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // Assert
        verify(this.usersTokensValidator).validatePasswordReset(request);
        verify(this.usersService).resetPassword(request);
    }

}
