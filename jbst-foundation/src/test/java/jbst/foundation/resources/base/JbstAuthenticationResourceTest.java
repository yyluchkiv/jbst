package jbst.foundation.resources.base;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.assistants.userdetails.JbstJwtUserDetailsService;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.configurations.TestRunnerResources1;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestMagicLinkToken;
import jbst.foundation.domain.dto.requests.RequestUserLogin;
import jbst.foundation.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.events.EventAuthenticationLoginFailure;
import jbst.foundation.domain.exceptions.ExceptionEntity;
import jbst.foundation.domain.exceptions.ExceptionEntityType;
import jbst.foundation.domain.exceptions.tokens.JbstRefreshTokenDbNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstRefreshTokenExpiredException;
import jbst.foundation.domain.exceptions.tokens.JbstRefreshTokenInvalidException;
import jbst.foundation.domain.exceptions.tokens.JbstRefreshTokenNotFoundException;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.jwt.JwtTokenValidatedClaims;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.security.CurrentClientUser;
import jbst.foundation.domain.security.MagicLinkUserCredentials;
import jbst.foundation.domain.sessions.Session;
import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.extension.JbstExtensionService;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.services.JbstUsersService;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.services.base.JbstTokensService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import jbst.foundation.validators.base.JbstAuthenticationRequestsValidator;
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
class JbstAuthenticationResourceTest extends TestRunnerResources1 {

    private static Stream<Arguments> refreshTokenThrowCookieUnauthorizedExceptionsTest() {
        return Stream.of(
                Arguments.of(new JbstRefreshTokenNotFoundException()),
                Arguments.of(new JbstRefreshTokenInvalidException()),
                Arguments.of(new JbstRefreshTokenExpiredException(Username.random())),
                Arguments.of(new JbstRefreshTokenDbNotFoundException(Username.random()))
        );
    }

    // Authentication
    private final AuthenticationManager authenticationManager;
    // Session
    private final JbstSessionRegistry sessionRegistry;
    // Services
    private final JbstExtensionService extensionService;
    private final JbstUsersService usersService;
    private final JbstUsersSessionsService usersSessionsService;
    private final JbstTokensService tokensService;
    // Repositories
    private final JbstUsersRepository usersRepository;
    private final JbstUsersTokensRepository usersTokensRepository;
    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    private final JbstJwtUserDetailsService jwtUserDetailsService;
    // Tokens
    private final JbstTokensProvider tokensProvider;
    // Validators
    private final JbstAuthenticationRequestsValidator authenticationRequestsValidator;
    // Utilities
    private final JbstSecurityUtils securityUtils;
    // Publishers
    private final SecurityJwtEventsPublisher securityJwtPublisher;

    // Resource
    private final JbstAuthenticationResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.authenticationManager,
                this.sessionRegistry,
                this.extensionService,
                this.usersService,
                this.usersSessionsService,
                this.tokensService,
                this.usersRepository,
                this.usersTokensRepository,
                this.currentSessionAssistant,
                this.jwtUserDetailsService,
                this.tokensProvider,
                this.authenticationRequestsValidator,
                this.securityUtils,
                this.securityJwtPublisher
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.authenticationManager,
                this.sessionRegistry,
                this.extensionService,
                this.usersService,
                this.usersSessionsService,
                this.tokensService,
                this.usersRepository,
                this.usersTokensRepository,
                this.currentSessionAssistant,
                this.jwtUserDetailsService,
                this.tokensProvider,
                this.authenticationRequestsValidator,
                this.securityUtils,
                this.securityJwtPublisher
        );
    }

    @Test
    void loginStandardTest() throws Exception {
        // Arrange
        var request = RequestUserLogin.hardcoded();
        when(this.authenticationRequestsValidator.validateLoginStandard(request)).thenReturn(new UsernamePasswordCredentials(
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
        when(this.currentSessionAssistant.getCurrentClientUser()).thenReturn(CurrentClientUser.hardcoded());

        // Act
        this.mvc.perform(
                        post("/authentication/login/standard")
                                .content(this.objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", equalTo("jbst")))
                .andExpect(jsonPath("$.email", equalTo("tests@yyluchkiv.com")))
                .andExpect(jsonPath("$.name", equalTo("JBST")))
                .andExpect(jsonPath("$.zoneId", equalTo("Europe/Kyiv")))
                .andExpect(jsonPath("$.authorities", notNullValue()))
                .andExpect(jsonPath("$.attributes", notNullValue()));

        // Assert
        verify(this.authenticationRequestsValidator).validateLoginStandard(request);
        verify(this.authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(username.value(), password.value()));
        verify(this.jwtUserDetailsService).loadUserByUsername(username.value());
        verify(this.securityUtils).createJwtAccessToken(user.getJwtTokenCreationParams());
        verify(this.securityUtils).createJwtRefreshToken(user.getJwtTokenCreationParams());
        verify(this.usersSessionsService).save(eq(user), eq(accessToken), eq(refreshToken), any(HttpServletRequest.class));
        verify(this.tokensProvider).createResponseAccessToken(eq(accessToken), any(HttpServletResponse.class));
        verify(this.tokensProvider).createResponseRefreshToken(eq(refreshToken), any(HttpServletResponse.class));
        // no verifications on static SecurityContextHolder
        verify(this.sessionRegistry).register(new Session(username, accessToken, refreshToken));
        verify(this.extensionService).authenticateAsStandard(eq(Username.hardcoded()), any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(this.currentSessionAssistant).getCurrentClientUser();
    }

    @Test
    void loginMagicLinkTest() throws Exception {
        // Arrange
        var request = RequestMagicLinkToken.hardcoded();
        var userToken = JbstUserToken.hardcodedMagicLink();
        var magicLinkUserCredentials = new MagicLinkUserCredentials(userToken, request.zoneId());
        var user = JwtUser.hardcoded(UserCreationOption.MAGICLINK);
        var credentials = new UsernamePasswordCredentials(user.username(), user.password());
        when(this.authenticationRequestsValidator.validateLoginMagicLink(request)).thenReturn(magicLinkUserCredentials);
        when(this.usersService.saveOrGetMagicLinkCredentials(magicLinkUserCredentials)).thenReturn(credentials);
        when(this.jwtUserDetailsService.loadUserByUsername(user.username().value())).thenReturn(user);
        var accessToken = JwtAccessToken.random();
        var refreshToken = JwtRefreshToken.random();
        when(this.securityUtils.createJwtAccessToken(user.getJwtTokenCreationParams())).thenReturn(accessToken);
        when(this.securityUtils.createJwtRefreshToken(user.getJwtTokenCreationParams())).thenReturn(refreshToken);
        when(this.currentSessionAssistant.getCurrentClientUser()).thenReturn(CurrentClientUser.hardcoded());

        // Act
        this.mvc.perform(
                        post("/authentication/login/magiclink")
                                .content(this.objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", equalTo("jbst")))
                .andExpect(jsonPath("$.email", equalTo("tests@yyluchkiv.com")))
                .andExpect(jsonPath("$.name", equalTo("JBST")))
                .andExpect(jsonPath("$.zoneId", equalTo("Europe/Kyiv")))
                .andExpect(jsonPath("$.authorities", notNullValue()))
                .andExpect(jsonPath("$.attributes", notNullValue()));

        // Assert
        verify(this.authenticationRequestsValidator).validateLoginMagicLink(request);
        verify(this.usersService).saveOrGetMagicLinkCredentials(magicLinkUserCredentials);
        verify(this.usersTokensRepository).saveAs(userToken.withUsed(true));
        verify(this.authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(this.jwtUserDetailsService).loadUserByUsername(user.username().value());
        verify(this.securityUtils).createJwtAccessToken(user.getJwtTokenCreationParams());
        verify(this.securityUtils).createJwtRefreshToken(user.getJwtTokenCreationParams());
        verify(this.usersSessionsService).save(eq(user), eq(accessToken), eq(refreshToken), any(HttpServletRequest.class));
        verify(this.tokensProvider).createResponseAccessToken(eq(accessToken), any(HttpServletResponse.class));
        verify(this.tokensProvider).createResponseRefreshToken(eq(refreshToken), any(HttpServletResponse.class));
        verify(this.sessionRegistry).register(new Session(user.username(), accessToken, refreshToken));
        verify(this.extensionService).authenticateAsMagicLink(eq(Username.hardcoded()), any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(this.currentSessionAssistant).getCurrentClientUser();
    }

    @Test
    void authenticateStandardWithInvalidCredentialsTest() throws Exception {
        // Arrange
        var request = RequestUserLogin.hardcoded();
        when(this.authenticationRequestsValidator.validateLoginStandard(request)).thenReturn(new UsernamePasswordCredentials(
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
        verify(this.authenticationRequestsValidator).validateLoginStandard(request);
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
