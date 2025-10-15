package jbst.foundation.resources.base;

import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.configurations.TestRunnerResources1;
import jbst.foundation.domain.dto.requests.RequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.RequestUserUpdate1;
import jbst.foundation.domain.dto.requests.RequestUserUpdate2;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.services.BaseUsersService;
import jbst.foundation.validators.BaseUsersValidator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstUsersResourceTest extends TestRunnerResources1 {

    // Services
    private final BaseUsersService baseUsersService;
    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Validators
    private final BaseUsersValidator baseUsersValidator;

    // Resource
    private final JbstUsersResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.baseUsersService,
                this.currentSessionAssistant,
                this.baseUsersValidator
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.baseUsersService,
                this.currentSessionAssistant,
                this.baseUsersValidator
        );
    }

    @Test
    void update1() throws Exception {
        // Arrange
        var request = RequestUserUpdate1.hardcoded();
        var user = JwtUser.hardcoded();
        when(this.currentSessionAssistant.getCurrentJwtUser()).thenReturn(user);

        // Act
        this.mvc.perform(
                post("/users/update1")
                        .content(this.objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.currentSessionAssistant).getCurrentJwtUser();
        verify(this.baseUsersValidator).validateUserUpdateRequest1(user.username(), request);
        verify(this.baseUsersService).updateUser1(user, request);
    }

    @Test
    void update2() throws Exception {
        // Arrange
        var request = RequestUserUpdate2.hardcoded();
        var user = JwtUser.hardcoded();
        when(this.currentSessionAssistant.getCurrentJwtUser()).thenReturn(user);

        // Act
        this.mvc.perform(
                        post("/users/update2")
                                .content(this.objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.currentSessionAssistant).getCurrentJwtUser();
        verify(this.baseUsersService).updateUser2(user, request);
    }

    @Test
    void changePasswordRequired() throws Exception {
        // Arrange
        var request = RequestUserChangePasswordBasic.hardcoded();
        var user = JwtUser.hardcoded();
        when(this.currentSessionAssistant.getCurrentJwtUser()).thenReturn(user);

        // Act
        this.mvc.perform(
                        post("/users/changePasswordRequired")
                                .content(this.objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.baseUsersValidator).validateUserChangePasswordRequestBasic(request);
        verify(this.currentSessionAssistant).getCurrentJwtUser();
        verify(this.baseUsersService).changePasswordRequired(user, request);
    }

    @Test
    void changePassword1() throws Exception {
        // Arrange
        var request = RequestUserChangePasswordBasic.hardcoded();
        var user = JwtUser.hardcoded();
        when(this.currentSessionAssistant.getCurrentJwtUser()).thenReturn(user);

        // Act
        this.mvc.perform(
                post("/users/changePassword1")
                        .content(this.objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.baseUsersValidator).validateUserChangePasswordRequestBasic(request);
        verify(this.currentSessionAssistant).getCurrentJwtUser();
        verify(this.baseUsersService).changePassword1(user, request);
    }
}
