package jbst.foundation.resources.base;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.assistants.userdetails.JbstUserDetailsService;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.configurations.TestRunnerResources1;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.dto.requests.JbstRequestMagicLinkToken;
import jbst.foundation.domain.dto.requests.JbstRequestUserLogin;
import jbst.foundation.domain.dto.responses.JbstResponseRefreshTokens;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.events.JbstEventAuthenticationLoginFailure;
import jbst.foundation.domain.exceptions.JbstExceptionResponse;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import jbst.foundation.domain.jwt.JbstJwtTokenValidatedClaims;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.security.JbstCurrentClientUser;
import jbst.foundation.domain.security.JbstMagicLinkUserCredentials;
import jbst.foundation.domain.sessions.JbstSession;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.extension.JbstExtensionService;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.services.JbstUsersService;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.services.base.JbstTokensService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import jbst.foundation.validators.base.JbstAuthenticationValidator;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstAuthenticationResourceTest extends TestRunnerResources1 {

    private static Stream<Arguments> refreshTokenThrowCookieUnauthorizedExceptionsTest() {
        return Stream.of(
                Arguments.of(new JbstExceptions.RefreshTokenNotFound()),
                Arguments.of(new JbstExceptions.RefreshTokenInvalid()),
                Arguments.of(new JbstExceptions.RefreshTokenExpired(Username.random())),
                Arguments.of(new JbstExceptions.RefreshTokenDbNotFound(Username.random()))
        );
    }

    // Authentication
    private final AuthenticationManager authenticationManager;
    // Extension
    private final JbstExtensionService extensionService;
    // Session
    private final JbstSessionRegistry sessionRegistry;
    // Services
    private final JbstUsersService usersService;
    private final JbstUsersSessionsService usersSessionsService;
    private final JbstTokensService tokensService;
    // Repositories
    private final JbstUsersRepository usersRepository;
    private final JbstUsersTokensRepository usersTokensRepository;
    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    private final JbstUserDetailsService jwtUserDetailsService;
    // Tokens
    private final JbstTokensProvider tokensProvider;
    // Validators
    private final JbstAuthenticationValidator authenticationRequestsValidator;
    // Utilities
    private final JbstSecurityUtils securityUtils;
    // Publishers
    private final JbstEventsPublisher eventsPublisher;

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
                this.eventsPublisher
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
                this.eventsPublisher
        );
    }

    @Test
    void loginStandardTest() throws Exception {
        // Arrange
        var request = JbstRequestUserLogin.hardcoded();
        when(this.authenticationRequestsValidator.validateLoginStandard(request)).thenReturn(new UsernamePasswordCredentials(
                request.username(),
                request.password()
        ));
        var username = request.username();
        var password = request.password();
        var user = JbstJwtUser.hardcoded();
        when(this.jwtUserDetailsService.loadUserByUsername(username.value())).thenReturn(user);
        var accessToken = JbstJwtAccessToken.random();
        var refreshToken = JbstJwtRefreshToken.random();
        when(this.securityUtils.createJwtAccessToken(user.getJwtTokenCreationParams())).thenReturn(accessToken);
        when(this.securityUtils.createJwtRefreshToken(user.getJwtTokenCreationParams())).thenReturn(refreshToken);
        when(this.currentSessionAssistant.getCurrentClientUser()).thenReturn(JbstCurrentClientUser.hardcoded());

        // Act
        this.mvc.perform(
                        post("/authentication/login/standard")
                                .content(this.objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", Matchers.equalTo("jbst")))
                .andExpect(jsonPath("$.email", Matchers.equalTo("tests@yyluchkiv.com")))
                .andExpect(jsonPath("$.name", Matchers.equalTo("JBST")))
                .andExpect(jsonPath("$.zoneId", Matchers.equalTo("Europe/Kyiv")))
                .andExpect(jsonPath("$.authorities", Matchers.notNullValue()))
                .andExpect(jsonPath("$.attributes", Matchers.notNullValue()));

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
        verify(this.sessionRegistry).register(new JbstSession(username, accessToken, refreshToken));
        verify(this.extensionService).authenticateAsStandard(eq(Username.hardcoded()), any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(this.currentSessionAssistant).getCurrentClientUser();
    }

    @Test
    void loginMagicLinkTest() throws Exception {
        // Arrange
        var request = JbstRequestMagicLinkToken.hardcoded();
        var userToken = JbstUserToken.hardcodedMagicLink();
        var magicLinkUserCredentials = new JbstMagicLinkUserCredentials(userToken, request.zoneId());
        var user = JbstJwtUser.hardcoded(JbstUserCreationOption.MAGICLINK);
        var credentials = new UsernamePasswordCredentials(user.username(), user.password());
        when(this.authenticationRequestsValidator.validateLoginMagicLink(request)).thenReturn(magicLinkUserCredentials);
        when(this.usersService.saveOrGetMagicLinkCredentials(magicLinkUserCredentials)).thenReturn(credentials);
        when(this.jwtUserDetailsService.loadUserByUsername(user.username().value())).thenReturn(user);
        var accessToken = JbstJwtAccessToken.random();
        var refreshToken = JbstJwtRefreshToken.random();
        when(this.securityUtils.createJwtAccessToken(user.getJwtTokenCreationParams())).thenReturn(accessToken);
        when(this.securityUtils.createJwtRefreshToken(user.getJwtTokenCreationParams())).thenReturn(refreshToken);
        when(this.currentSessionAssistant.getCurrentClientUser()).thenReturn(JbstCurrentClientUser.hardcoded());

        // Act
        this.mvc.perform(
                        post("/authentication/login/magiclink")
                                .content(this.objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", Matchers.equalTo("jbst")))
                .andExpect(jsonPath("$.email", Matchers.equalTo("tests@yyluchkiv.com")))
                .andExpect(jsonPath("$.name", Matchers.equalTo("JBST")))
                .andExpect(jsonPath("$.zoneId", Matchers.equalTo("Europe/Kyiv")))
                .andExpect(jsonPath("$.authorities", Matchers.notNullValue()))
                .andExpect(jsonPath("$.attributes", Matchers.notNullValue()));

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
        verify(this.sessionRegistry).register(new JbstSession(user.username(), accessToken, refreshToken));
        verify(this.extensionService).authenticateAsMagicLink(eq(Username.hardcoded()), any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(this.currentSessionAssistant).getCurrentClientUser();
    }

    @Test
    void authenticateStandardWithInvalidCredentialsTest() throws Exception {
        // Arrange
        var request = JbstRequestUserLogin.hardcoded();
        when(this.authenticationRequestsValidator.validateLoginStandard(request)).thenReturn(new UsernamePasswordCredentials(
                request.username(),
                request.password()
        ));
        var username = request.username();
        var password = request.password();
        var authenticationToken = new UsernamePasswordAuthenticationToken(username.value(), password.value());
        var exception = new BadCredentialsException("Bad credentials");
        var exceptionResponse = JbstExceptionResponse.of(
                JbstExceptionResponse.Type.ERROR,
                exception
        );
        when(this.authenticationManager.authenticate(authenticationToken)).thenThrow(exception);

        // Act
        this.mvc.perform(
                        post("/authentication/login/standard")
                                .content(this.objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$.jbsTimestamp", Matchers.greaterThan(exceptionResponse.getJbsTimestamp())))
                .andExpect(jsonPath("$.jbstType", Matchers.equalTo(exceptionResponse.getJbstType().name())))
                .andExpect(jsonPath("$.jbstMessageOnClient", Matchers.equalTo(exceptionResponse.getJbstMessageOnClient())))
                .andExpect(jsonPath("$.jbstAttributes", Matchers.equalTo(exceptionResponse.getJbstAttributes())));

        // Assert
        verify(this.authenticationRequestsValidator).validateLoginStandard(request);
        verify(this.authenticationManager).authenticate(authenticationToken);
        verify(this.eventsPublisher).publishAuthenticationLoginFailure(any(JbstEventAuthenticationLoginFailure.class));
    }

    @Test
    void logoutNoJwtRefreshTokenTest() throws Exception {
        // Arrange
        var requestAccessToken = new JbstRequestAccessToken(null);
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
        var requestAccessToken = JbstRequestAccessToken.random();
        var accessToken = requestAccessToken.getJwtAccessToken();
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        when(this.securityUtils.validate(accessToken)).thenReturn(JbstJwtTokenValidatedClaims.invalid(accessToken));

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
        var requestAccessToken = JbstRequestAccessToken.random();
        var accessToken = requestAccessToken.getJwtAccessToken();
        var claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(username.value());
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        var validatedClaims = JbstJwtTokenValidatedClaims.valid(accessToken, claims);
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
        var requestAccessToken = JbstRequestAccessToken.random();
        var accessToken = requestAccessToken.getJwtAccessToken();
        var claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(username.value());
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        var validatedClaims = JbstJwtTokenValidatedClaims.valid(accessToken, claims);
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
                .andDo(print())
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$.jbsTimestamp", Matchers.notNullValue()))
                .andExpect(jsonPath("$.jbstType", Matchers.equalTo("ERROR")))
                .andExpect(jsonPath("$.jbstMessageOnClient", Matchers.notNullValue()))
                .andExpect(jsonPath("$.jbstAttributes", Matchers.notNullValue()));

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
        var response = JbstResponseRefreshTokens.random();
        when(this.tokensService.refreshSessionOrThrow(any(HttpServletRequest.class), any(HttpServletResponse.class))).thenReturn(response);

        // Act
        this.mvc.perform(post("/authentication/refreshToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", Matchers.equalTo(response.accessToken().value())))
                .andExpect(jsonPath("$.refreshToken", Matchers.equalTo(response.refreshToken().value())));

        // Assert
        verify(this.tokensService).refreshSessionOrThrow(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}
