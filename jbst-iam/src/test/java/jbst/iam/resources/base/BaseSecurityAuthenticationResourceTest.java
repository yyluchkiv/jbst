package jbst.iam.resources.base;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.exceptions.ExceptionEntity;
import jbst.foundation.domain.exceptions.ExceptionEntityType;
import jbst.foundation.domain.exceptions.tokens.RefreshTokenDbNotFoundException;
import jbst.foundation.domain.exceptions.tokens.RefreshTokenExpiredException;
import jbst.foundation.domain.exceptions.tokens.RefreshTokenInvalidException;
import jbst.foundation.domain.exceptions.tokens.RefreshTokenNotFoundException;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.jwt.JwtTokenValidatedClaims;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.utils.JbstSecurityUtils;
import jbst.iam.assistants.current.CurrentSessionAssistant;
import jbst.iam.assistants.userdetails.JwtUserDetailsService;
import jbst.iam.configurations.TestRunnerResources1;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestMagicLinkToken;
import jbst.foundation.domain.dto.requests.RequestUserLogin;
import jbst.foundation.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.events.EventAuthenticationLoginFailure;
import jbst.foundation.domain.security.CurrentClientUser;
import jbst.foundation.domain.sessions.Session;
import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.repositories.UsersRepository;
import jbst.foundation.repositories.UsersTokensRepository;
import jbst.foundation.services.BaseUsersService;
import jbst.foundation.services.BaseUsersSessionsService;
import jbst.foundation.services.TokensService;
import jbst.foundation.sessions.SessionRegistry;
import jbst.foundation.tokens.facade.TokensProvider;
import jbst.foundation.validators.BaseAuthenticationRequestsValidator;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BaseSecurityAuthenticationResourceTest extends TestRunnerResources1 {

    private static Stream<Arguments> refreshTokenThrowCookieUnauthorizedExceptionsTest() {
        return Stream.of(
                Arguments.of(new RefreshTokenNotFoundException()),
                Arguments.of(new RefreshTokenInvalidException()),
                Arguments.of(new RefreshTokenExpiredException(Username.random())),
                Arguments.of(new RefreshTokenDbNotFoundException(Username.random()))
        );
    }

    // Authentication
    private final AuthenticationManager authenticationManager;
    // Session
    private final SessionRegistry sessionRegistry;
    // Services
    private final BaseUsersService baseUsersService;
    private final BaseUsersSessionsService baseUsersSessionsService;
    private final TokensService tokensService;
    // Repositories
    private final UsersRepository usersRepository;
    private final UsersTokensRepository usersTokensRepository;
    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    private final JwtUserDetailsService jwtUserDetailsService;
    // Tokens
    private final TokensProvider tokensProvider;
    // Validators
    private final BaseAuthenticationRequestsValidator baseAuthenticationRequestsValidator;
    // Utilities
    private final JbstSecurityUtils securityUtils;
    // Publishers
    private final SecurityJwtEventsPublisher securityJwtPublisher;

    // Resource
    private final BaseSecurityAuthenticationResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.authenticationManager,
                this.sessionRegistry,
                this.baseUsersService,
                this.baseUsersSessionsService,
                this.tokensService,
                this.usersRepository,
                this.usersTokensRepository,
                this.currentSessionAssistant,
                this.jwtUserDetailsService,
                this.tokensProvider,
                this.baseAuthenticationRequestsValidator,
                this.securityUtils,
                this.securityJwtPublisher
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.authenticationManager,
                this.sessionRegistry,
                this.baseUsersService,
                this.baseUsersSessionsService,
                this.tokensService,
                this.usersRepository,
                this.usersTokensRepository,
                this.currentSessionAssistant,
                this.jwtUserDetailsService,
                this.tokensProvider,
                this.baseAuthenticationRequestsValidator,
                this.securityUtils,
                this.securityJwtPublisher
        );
    }

    @Test
    void loginStandardTest() throws Exception {
        // Arrange
        var request = RequestUserLogin.hardcoded();
        when(this.baseAuthenticationRequestsValidator.validateLoginStandard(request)).thenReturn(new UsernamePasswordCredentials(
                request.username(),
                request.password()
        ));
        var username = request.username();
        var password = request.password();
        var user = JwtUser.hardcoded();
        when(this.jwtUserDetailsService.loadUserByUsername(username.value())).thenReturn(user);
        var accessToken = JwtAccessToken.random();
        var refreshToken = JwtRefreshToken.random();
        when(this.securityUtils.createJwtAccessToken(user.getJwtTokenCreationParams())).thenReturn(accessToken);
        when(this.securityUtils.createJwtRefreshToken(user.getJwtTokenCreationParams())).thenReturn(refreshToken);
        var currentClientUser = CurrentClientUser.random();
        when(this.currentSessionAssistant.getCurrentClientUser()).thenReturn(currentClientUser);

        // Act
        this.mvc.perform(
                        post("/authentication/login/standard")
                                .content(this.objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", equalTo(currentClientUser.getUsername().value())))
                .andExpect(jsonPath("$.email", equalTo(currentClientUser.getEmail().value())))
                .andExpect(jsonPath("$.name", equalTo(currentClientUser.getName())))
                .andExpect(jsonPath("$.zoneId", equalTo(currentClientUser.getZoneId().getId())))
                .andExpect(jsonPath("$.authorities", notNullValue()))
                .andExpect(jsonPath("$.attributes", notNullValue()));

        // Assert
        verify(this.baseAuthenticationRequestsValidator).validateLoginStandard(request);
        verify(this.authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(username.value(), password.value()));
        verify(this.jwtUserDetailsService).loadUserByUsername(username.value());
        verify(this.securityUtils).createJwtAccessToken(user.getJwtTokenCreationParams());
        verify(this.securityUtils).createJwtRefreshToken(user.getJwtTokenCreationParams());
        verify(this.baseUsersSessionsService).save(eq(user), eq(accessToken), eq(refreshToken), any(HttpServletRequest.class));
        verify(this.tokensProvider).createResponseAccessToken(eq(accessToken), any(HttpServletResponse.class));
        verify(this.tokensProvider).createResponseRefreshToken(eq(refreshToken), any(HttpServletResponse.class));
        // no verifications on static SecurityContextHolder
        verify(this.sessionRegistry).register(new Session(username, accessToken, refreshToken));
        verify(this.currentSessionAssistant).getCurrentClientUser();
    }

    @Test
    void loginMagicLinkTest() throws Exception {
        // Arrange
        var request = RequestMagicLinkToken.hardcoded();
        var userToken = JbstUserToken.hardcodedMagicLink();
        when(this.baseAuthenticationRequestsValidator.validateLoginMagicLink(request)).thenReturn(userToken);

        var user = JwtUser.hardcodedMagicLink();
        var userCreationOption = UserCreationOption.MAGICLINK;
        when(this.baseUsersService.safeSave(userCreationOption, userToken.email(), request.zoneId())).thenReturn(user);
        when(this.jwtUserDetailsService.loadUserByUsername(user.username().value())).thenReturn(user);

        var accessToken = JwtAccessToken.random();
        var refreshToken = JwtRefreshToken.random();
        when(this.securityUtils.createJwtAccessToken(user.getJwtTokenCreationParams())).thenReturn(accessToken);
        when(this.securityUtils.createJwtRefreshToken(user.getJwtTokenCreationParams())).thenReturn(refreshToken);

        var currentClientUser = CurrentClientUser.random();
        when(this.currentSessionAssistant.getCurrentClientUser()).thenReturn(currentClientUser);

        // Act
        this.mvc.perform(
                        post("/authentication/login/magic-link")
                                .content(this.objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", equalTo(currentClientUser.getUsername().value())))
                .andExpect(jsonPath("$.email", equalTo(currentClientUser.getEmail().value())))
                .andExpect(jsonPath("$.name", equalTo(currentClientUser.getName())))
                .andExpect(jsonPath("$.zoneId", equalTo(currentClientUser.getZoneId().getId())))
                .andExpect(jsonPath("$.authorities", notNullValue()))
                .andExpect(jsonPath("$.attributes", notNullValue()));

        // Assert
        verify(this.baseAuthenticationRequestsValidator).validateLoginMagicLink(request);
        verify(this.baseUsersService).safeSave(userCreationOption, userToken.email(), request.zoneId());
        verify(this.usersTokensRepository).saveAs(userToken.withUsed(true));
        verify(this.authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(this.jwtUserDetailsService).loadUserByUsername(user.username().value());
        verify(this.securityUtils).createJwtAccessToken(user.getJwtTokenCreationParams());
        verify(this.securityUtils).createJwtRefreshToken(user.getJwtTokenCreationParams());
        verify(this.baseUsersSessionsService).save(eq(user), eq(accessToken), eq(refreshToken), any(HttpServletRequest.class));
        verify(this.tokensProvider).createResponseAccessToken(eq(accessToken), any(HttpServletResponse.class));
        verify(this.tokensProvider).createResponseRefreshToken(eq(refreshToken), any(HttpServletResponse.class));
        verify(this.sessionRegistry).register(new Session(user.username(), accessToken, refreshToken));
        verify(this.currentSessionAssistant).getCurrentClientUser();
    }

    @Test
    void authenticateStandardWithInvalidCredentialsTest() throws Exception {
        // Arrange
        var request = RequestUserLogin.hardcoded();
        when(this.baseAuthenticationRequestsValidator.validateLoginStandard(request)).thenReturn(new UsernamePasswordCredentials(
                request.username(),
                request.password()
        ));
        var username = request.username();
        var password = request.password();
        var authenticationToken = new UsernamePasswordAuthenticationToken(username.value(), password.value());
        var exception = new BadCredentialsException("Bad credentials");
        var exceptionEntity = new ExceptionEntity(
                ExceptionEntityType.ERROR,
                exception.getMessage(),
                exception.getMessage()
        );
        when(this.authenticationManager.authenticate(authenticationToken)).thenThrow(exception);

        // Act
        this.mvc.perform(
                        post("/authentication/login/standard")
                                .content(this.objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.exceptionEntityType", equalTo(exceptionEntity.getExceptionEntityType().name())))
                .andExpect(jsonPath("$.attributes", equalTo(exceptionEntity.getAttributes())))
                .andExpect(jsonPath("$.timestamp", Matchers.greaterThan(exceptionEntity.getTimestamp())));

        // Assert
        verify(this.baseAuthenticationRequestsValidator).validateLoginStandard(request);
        verify(this.authenticationManager).authenticate(authenticationToken);
        verify(this.securityJwtPublisher).publishAuthenticationLoginFailure(any(EventAuthenticationLoginFailure.class));
    }

    @Test
    void logoutNoJwtRefreshTokenTest() throws Exception {
        // Arrange
        var requestAccessToken = new RequestAccessToken(null);
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);

        // Act
        this.mvc.perform(post("/authentication/logout"))
                .andExpect(status().isOk());

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
    }

    @Test
    void logoutInvalidJwtRefreshTokenTest() throws Exception {
        // Arrange
        var requestAccessToken = RequestAccessToken.random();
        var accessToken = requestAccessToken.getJwtAccessToken();
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        when(this.securityUtils.validate(accessToken)).thenReturn(JwtTokenValidatedClaims.invalid(accessToken));

        // Act
        this.mvc.perform(post("/authentication/logout"))
                .andExpect(status().isOk());

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.securityUtils).validate(accessToken);
    }

    @Test
    void logoutTest() throws Exception {
        // Arrange
        var httpSession = mock(HttpSession.class);
        var username = Username.random();
        var requestAccessToken = RequestAccessToken.random();
        var accessToken = requestAccessToken.getJwtAccessToken();
        var claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(username.value());
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        var validatedClaims = JwtTokenValidatedClaims.valid(accessToken, claims);
        when(this.securityUtils.validate(requestAccessToken.getJwtAccessToken())).thenReturn(validatedClaims);

        // Act
        this.mvc.perform(post("/authentication/logout")
                        .with(request -> {
                            request.setSession(httpSession);
                            return request;
                        })
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.securityUtils).validate(accessToken);
        verify(this.sessionRegistry).logout(username, accessToken);
        verify(this.tokensProvider).clearTokens(any(HttpServletResponse.class));
        verify(httpSession).invalidate();
        // no verifications on static SecurityContextHolder
    }

    @Test
    void logoutNullSessionTest() throws Exception {
        // Arrange
        var username = Username.random();
        var requestAccessToken = RequestAccessToken.random();
        var accessToken = requestAccessToken.getJwtAccessToken();
        var claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(username.value());
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        var validatedClaims = JwtTokenValidatedClaims.valid(accessToken, claims);
        when(this.securityUtils.validate(accessToken)).thenReturn(validatedClaims);

        // Act
        this.mvc.perform(post("/authentication/logout"))
                .andExpect(status().isOk());

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.securityUtils).validate(accessToken);
        verify(this.sessionRegistry).logout(username, accessToken);
        verify(this.tokensProvider).clearTokens(any(HttpServletResponse.class));
        // no verifications on static SecurityContextHolder
    }

    @ParameterizedTest
    @MethodSource("refreshTokenThrowCookieUnauthorizedExceptionsTest")
    void refreshTokenThrowCookieUnauthorizedExceptionsTest(Exception exception) throws Exception {
        // Arrange
        when(this.tokensService.refreshSessionOrThrow(any(HttpServletRequest.class), any(HttpServletResponse.class))).thenThrow(exception);

        // Act
        this.mvc.perform(post("/authentication/refreshToken"))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.exceptionEntityType", equalTo("ERROR")))
                .andExpect(jsonPath("$.attributes.shortMessage", equalTo(exception.getMessage())))
                .andExpect(jsonPath("$.attributes.fullMessage", equalTo(exception.getMessage())));

        // Assert
        verify(this.tokensService).refreshSessionOrThrow(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(this.tokensProvider).clearTokens(any());
        reset(
                this.tokensService,
                this.tokensProvider
        );
    }

    @Test
    void refreshTokenValidTest() throws Exception {
        // Arrange
        var response = ResponseRefreshTokens.random();
        when(this.tokensService.refreshSessionOrThrow(any(HttpServletRequest.class), any(HttpServletResponse.class))).thenReturn(response);

        // Act
        this.mvc.perform(post("/authentication/refreshToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", equalTo(response.accessToken().value())))
                .andExpect(jsonPath("$.refreshToken", equalTo(response.refreshToken().value())));

        // Assert
        verify(this.tokensService).refreshSessionOrThrow(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}
