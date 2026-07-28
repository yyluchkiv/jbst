package jbst.foundation.services.abstracts;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesFixed;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.enums.JbstAccountAccessMethod;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.enums.JbstStatus;
import jbst.foundation.domain.events.JbstEventSessionUserRequestMetadataAdd;
import jbst.foundation.domain.events.JbstEventSessionUserRequestMetadataRenew;
import jbst.foundation.domain.functions.JbstFunctionSessionUserRequestMetadataSave;
import jbst.foundation.domain.base.IPAddress;
import jbst.foundation.domain.http.requests.JbstUserAgentHeader;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.domain.tuples.TupleToggle;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import jbst.foundation.utils.JbstGeoUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static jbst.foundation.domain.databases.JbstUserSession.randomPersistedSession;
import static jbst.foundation.domain.http.JbstHttpServletRequests.getClientIpAddr;
import static jbst.foundation.domain.random.JbstRandom.randomIPv4;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static jbst.foundation.domain.strings.JbstMessages.entityAccessDenied;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;
import static jbst.foundation.domain.tuples.TuplePresence.present;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstAbstractUsersSessionsServiceTest {

    private static Stream<Arguments> saveUserRequestMetadataTest() {
        return Stream.of(
                Arguments.of(TupleToggle.disabled(), TupleToggle.disabled(), false, false),
                Arguments.of(TupleToggle.disabled(), TupleToggle.enabled(true), false, true),
                Arguments.of(TupleToggle.disabled(), TupleToggle.enabled(false), false, false),
                Arguments.of(TupleToggle.enabled(true), TupleToggle.disabled(), true, false),
                Arguments.of(TupleToggle.enabled(false), TupleToggle.disabled(), false, false),
                Arguments.of(TupleToggle.enabled(false), TupleToggle.enabled(false), false, false),
                Arguments.of(TupleToggle.enabled(false), TupleToggle.enabled(true), false, true),
                Arguments.of(TupleToggle.enabled(true), TupleToggle.enabled(false), true, false),
                Arguments.of(TupleToggle.enabled(true), TupleToggle.enabled(true), true, true)
        );
    }

    private static Stream<Arguments> renewUserRequestMetadataArgs() {
        return Stream.of(
                Arguments.of(false, false, TupleToggle.disabled(), TupleToggle.disabled()),
                Arguments.of(true, false, TupleToggle.enabled(true), TupleToggle.disabled()),
                Arguments.of(false, true, TupleToggle.disabled(), TupleToggle.enabled(true)),
                Arguments.of(true, true, TupleToggle.enabled(true), TupleToggle.enabled(true))
        );
    }

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesFixed.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstProperties jbstProperties;

        @Bean
        JbstEventsPublisher eventsPublisher() {
            return mock(JbstEventsPublisher.class);
        }

        @Bean
        JbstUsersSessionsRepository usersSessionsRepository() {
            return mock(JbstUsersSessionsRepository.class);
        }

        @Bean
        public JbstSecurityUtils securityUtils() {
            return new JbstSecurityUtils(
                    this.jbstProperties
            );
        }

        @Bean
        JbstGeoUtils geoUtils() {
            return mock(JbstGeoUtils.class);
        }

        @Bean
        JbstAbstractUsersSessionsService abstractTokensContextThrowerService() {
            return new JbstAbstractUsersSessionsService(
                    this.eventsPublisher(),
                    this.usersSessionsRepository(),
                    this.geoUtils(),
                    this.securityUtils()
            ) {};
        }
    }

    // Publishers
    private final JbstEventsPublisher eventsPublisher;
    // Repositories
    private final JbstUsersSessionsRepository usersSessionsRepository;
    // Utils
    private final JbstGeoUtils geoUtils;

    private final JbstAbstractUsersSessionsService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.eventsPublisher,
                this.usersSessionsRepository,
                this.geoUtils
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.eventsPublisher,
                this.usersSessionsRepository,
                this.geoUtils
        );
    }

    @Test
    void assertAccess() {
        when(this.usersSessionsRepository.isPresent(JbstUserSessionId.fixed(), Username.fixed())).thenReturn(TuplePresence.present(JbstUserSession.randomPersistedSession()));

        // Act
        this.componentUnderTest.assertAccess(Username.fixed(), JbstUserSessionId.fixed());

        // Assert
        verify(this.usersSessionsRepository).isPresent(JbstUserSessionId.fixed(), Username.fixed());
    }

    @Test
    void assertAccessNoAccess() {
        when(this.usersSessionsRepository.isPresent(JbstUserSessionId.fixed(), Username.fixed())).thenReturn(TuplePresence.absent());

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.assertAccess(Username.fixed(), JbstUserSessionId.fixed()));

        // Assert
        verify(this.usersSessionsRepository).isPresent(JbstUserSessionId.fixed(), Username.fixed());
        assertThat(throwable)
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage(entityAccessDenied("Session", JbstUserSessionId.fixed().value()));
    }

    private static Stream<Arguments> saveUserSessionTest() {
        return Stream.of(
                Arguments.of(JbstJwtUser.fixed(JbstUserCreationOption.STANDARD), JbstAccountAccessMethod.USERNAME_PASSWORD),
                Arguments.of(JbstJwtUser.fixed(JbstUserCreationOption.MAGICLINK), JbstAccountAccessMethod.MAGICLINK)
        );
    }

    @ParameterizedTest
    @MethodSource("saveUserSessionTest")
    void saveUserSessionNotNullTest(JbstJwtUser user, JbstAccountAccessMethod accountAccessMethod) {
        // Arrange
        var ipAddr = randomIPv4();
        var httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("User-Agent")).thenReturn(randomString());
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(ipAddr);
        var username = user.username();
        var accessToken = JbstJwtAccessToken.random();
        var refreshToken = JbstJwtRefreshToken.random();
        var userSession = JbstUserSession.random(username, accessToken, refreshToken);
        when(this.usersSessionsRepository.isPresent(accessToken)).thenReturn(present(userSession));
        when(this.usersSessionsRepository.saveAs(any(JbstUserSession.class))).thenReturn(userSession);

        // Act
        this.componentUnderTest.save(user, accessToken, refreshToken, httpServletRequest);

        // Assert
        verify(this.usersSessionsRepository).isPresent(accessToken);
        var dbUserSessionAC = ArgumentCaptor.forClass(JbstUserSession.class);
        verify(this.usersSessionsRepository).saveAs(dbUserSessionAC.capture());
        var actualDbUserSession = dbUserSessionAC.getValue();
        assertThat(actualDbUserSession.username()).isEqualTo(username);
        assertThat(actualDbUserSession.refreshToken()).isEqualTo(refreshToken);
        var requestMetadata = actualDbUserSession.metadata();
        assertThat(requestMetadata.getStatus()).isEqualTo(JbstStatus.STARTED);
        assertThat(requestMetadata.getGeoLocation().getIpAddr()).isEqualTo(ipAddr);
        var whereTuple3 = requestMetadata.getWhereTuple3();
        assertThat(whereTuple3.a()).isEqualTo(ipAddr);
        assertThat(whereTuple3.b()).isEqualTo(JbstConstants.Flags.UNKNOWN);
        assertThat(whereTuple3.c()).isEqualTo("Processing. Please wait...");
        var whatTuple2 = requestMetadata.getWhatTuple2();
        assertThat(whatTuple2.a()).isEqualTo(JbstConstants.Strings.UNDEFINED);
        assertThat(whatTuple2.b()).isEqualTo("—");
        assertThat(actualDbUserSession.id()).isNotNull();
        var eventAC = ArgumentCaptor.forClass(JbstEventSessionUserRequestMetadataAdd.class);
        verify(this.eventsPublisher).publishSessionUserRequestMetadataAdd(eventAC.capture());
        var event = eventAC.getValue();
        assertThat(event.username()).isEqualTo(username);
        assertThat(event.session().id()).isEqualTo(actualDbUserSession.id());
        assertThat(event.session().metadata()).isNotEqualTo(actualDbUserSession.metadata());
        assertThat(event.accountAccessMethod()).isEqualTo(accountAccessMethod);
    }

    @Test
    void saveUserSessionNullTest() {
        // Arrange
        var ipAddr = randomIPv4();
        var httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("User-Agent")).thenReturn(randomString());
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(ipAddr);
        var user = JbstJwtUser.fixed();
        var username = user.username();
        var accessToken = JbstJwtAccessToken.random();
        var refreshToken = JbstJwtRefreshToken.random();
        when(this.usersSessionsRepository.isPresent(accessToken)).thenReturn(TuplePresence.absent());
        var userSession = JbstUserSession.random(username, accessToken, refreshToken);
        when(this.usersSessionsRepository.saveAs(any(JbstUserSession.class))).thenReturn(userSession);

        // Act
        this.componentUnderTest.save(user, accessToken, refreshToken, httpServletRequest);

        // Assert
        verify(this.usersSessionsRepository).isPresent(accessToken);
        var dbUserSessionAC = ArgumentCaptor.forClass(JbstUserSession.class);
        verify(this.usersSessionsRepository).saveAs(dbUserSessionAC.capture());
        var actualDbUserSession = dbUserSessionAC.getValue();
        assertThat(actualDbUserSession.username()).isEqualTo(username);
        assertThat(actualDbUserSession.refreshToken()).isEqualTo(refreshToken);
        var requestMetadata = actualDbUserSession.metadata();
        assertThat(requestMetadata.getStatus()).isEqualTo(JbstStatus.STARTED);
        assertThat(requestMetadata.getGeoLocation().getIpAddr()).isEqualTo(ipAddr);
        var whereTuple3 = requestMetadata.getWhereTuple3();
        assertThat(whereTuple3.a()).isEqualTo(ipAddr);
        assertThat(whereTuple3.b()).isEqualTo(JbstConstants.Flags.UNKNOWN);
        assertThat(whereTuple3.c()).isEqualTo("Processing. Please wait...");
        var whatTuple2 = requestMetadata.getWhatTuple2();
        assertThat(whatTuple2.a()).isEqualTo(JbstConstants.Strings.UNDEFINED);
        assertThat(whatTuple2.b()).isEqualTo("—");
        assertThat(actualDbUserSession.id()).isNotNull();
        var eventAC = ArgumentCaptor.forClass(JbstEventSessionUserRequestMetadataAdd.class);
        verify(this.eventsPublisher).publishSessionUserRequestMetadataAdd(eventAC.capture());
        var event = eventAC.getValue();
        assertThat(event.username()).isEqualTo(username);
        assertThat(event.session().id()).isNotEqualTo(actualDbUserSession.id());
        assertThat(event.session().metadata()).isNotEqualTo(actualDbUserSession.metadata());
        assertThat(event.accountAccessMethod()).isEqualTo(JbstAccountAccessMethod.USERNAME_PASSWORD);
    }

    @Test
    void refreshTest() {
        // Arrange
        var httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("User-Agent")).thenReturn(randomString());
        var user = entity(JbstJwtUser.class);
        var username = user.username();
        var newAccessToken = JbstJwtAccessToken.random();
        var newRefreshToken = JbstJwtRefreshToken.random();
        var oldSession = randomPersistedSession();
        when(this.usersSessionsRepository.saveAs(any(JbstUserSession.class))).thenReturn(randomPersistedSession());

        // Act
        this.componentUnderTest.refresh(user, oldSession, newAccessToken, newRefreshToken, httpServletRequest);

        // Assert
        var saveCaptor = ArgumentCaptor.forClass(JbstUserSession.class);
        verify(this.usersSessionsRepository).saveAs(saveCaptor.capture());
        var newUserSession = saveCaptor.getValue();
        assertThat(newUserSession.username()).isEqualTo(username);
        assertThat(newUserSession.refreshToken()).isEqualTo(newRefreshToken);
        assertThat(newUserSession.metadata()).isEqualTo(oldSession.metadata());
        verify(this.usersSessionsRepository).delete(oldSession.id());
        var eventAC = ArgumentCaptor.forClass(JbstEventSessionUserRequestMetadataAdd.class);
        verify(this.eventsPublisher).publishSessionUserRequestMetadataAdd(eventAC.capture());
        var event = eventAC.getValue();
        assertThat(event.username()).isEqualTo(username);
        assertThat(event.email()).isEqualTo(user.email());
        assertThat(event.session().id()).isNotEqualTo(newUserSession.id());
        assertThat(event.accountAccessMethod()).isEqualTo(JbstAccountAccessMethod.SESSION_TOKEN);
    }

    @Test
    void saveUserRequestMetadataEventSessionUserRequestMetadataAddTest() {
        var event = entity(JbstEventSessionUserRequestMetadataAdd.class);
        when(this.geoUtils.getUserRequestMetadataProcessed(event.clientIpAddr(), event.userAgentHeader())).thenReturn(JbstUserRequestMetadata.valid());
        when(this.usersSessionsRepository.saveAs(any(JbstUserSession.class))).thenReturn(event.session());

        // Act
        this.componentUnderTest.saveUserRequestMetadata(event);

        // Assert
        verify(this.geoUtils).getUserRequestMetadataProcessed(event.clientIpAddr(), event.userAgentHeader());
        var userSessionAC = ArgumentCaptor.forClass(JbstUserSession.class);
        verify(this.usersSessionsRepository).saveAs(userSessionAC.capture());
        assertThat(userSessionAC.getValue().metadata()).isEqualTo(JbstUserRequestMetadata.valid());
    }

    @Test
    void saveUserRequestMetadataEventSessionUserRequestMetadataRenewTest() {
        var event = new JbstEventSessionUserRequestMetadataRenew(
                Username.random(),
                entity(JbstUserSession.class),
                IPAddress.random(),
                entity(JbstUserAgentHeader.class),
                TupleToggle.disabled(),
                TupleToggle.disabled()
        );
        when(this.geoUtils.getUserRequestMetadataProcessed(event.clientIpAddr(), event.userAgentHeader())).thenReturn(JbstUserRequestMetadata.valid());
        when(this.usersSessionsRepository.saveAs(any(JbstUserSession.class))).thenReturn(event.session());

        // Act
        this.componentUnderTest.saveUserRequestMetadata(event);

        // Assert
        verify(this.geoUtils).getUserRequestMetadataProcessed(event.clientIpAddr(), event.userAgentHeader());
        var userSessionAC = ArgumentCaptor.forClass(JbstUserSession.class);
        verify(this.usersSessionsRepository).saveAs(userSessionAC.capture());
        assertThat(userSessionAC.getValue().metadata()).isEqualTo(JbstUserRequestMetadata.valid());
    }

    @ParameterizedTest
    @MethodSource("saveUserRequestMetadataTest")
    void saveUserRequestMetadataTest(
            TupleToggle<Boolean> metadataRenewCron,
            TupleToggle<Boolean> metadataRenewManually,
            boolean expectedMetadataRenewCron,
            boolean expectedMetadataRenewManually
    ) {
        // Arrange
        var username = Username.random();
        var session = JbstUserSession.ofPersisted(
                entity(JbstUserSessionId.class),
                getCurrentTimestamp(),
                getCurrentTimestamp(),
                username,
                JbstJwtAccessToken.random(),
                JbstJwtRefreshToken.random(),
                JbstUserRequestMetadata.random(),
                false,
                false
        );
        var saveFunction = new JbstFunctionSessionUserRequestMetadataSave(
                username,
                session,
                entity(IPAddress.class),
                entity(JbstUserAgentHeader.class),
                metadataRenewCron,
                metadataRenewManually
        );
        when(this.geoUtils.getUserRequestMetadataProcessed(saveFunction.clientIpAddr(), saveFunction.userAgentHeader())).thenReturn(JbstUserRequestMetadata.valid());
        when(this.usersSessionsRepository.saveAs(any(JbstUserSession.class))).thenReturn(saveFunction.session());

        // Act
        this.componentUnderTest.saveUserRequestMetadata(saveFunction);

        // Assert
        verify(this.geoUtils).getUserRequestMetadataProcessed(saveFunction.clientIpAddr(), saveFunction.userAgentHeader());
        var userSessionAC = ArgumentCaptor.forClass(JbstUserSession.class);
        verify(this.usersSessionsRepository).saveAs(userSessionAC.capture());
        var sessionProcessedMetadata = userSessionAC.getValue();
        assertThat(sessionProcessedMetadata.metadataRenewCron()).isEqualTo(expectedMetadataRenewCron);
        assertThat(sessionProcessedMetadata.metadataRenewManually()).isEqualTo(expectedMetadataRenewManually);
        assertThat(userSessionAC.getValue().metadata()).isEqualTo(JbstUserRequestMetadata.valid());
    }

    @Test
    void getExpiredSessionsTest() {
        // Arrange
        var usernames = new HashSet<>(Set.of(Username.fixed()));
        var sessionInvalidUserSession = JbstUserSession.random(
                Username.random(),
                JbstJwtAccessToken.random(),
                new JbstJwtRefreshToken("<invalid>")
        );
        var sessionExpiredUserSession = JbstUserSession.random(
                Username.random(),
                JbstJwtAccessToken.random(),
                new JbstJwtRefreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtdWx0aXVzZXI0MyIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJhZG1pbiJ9LHsiYXV0aG9yaXR5IjoidXNlciJ9XSwiaWF0IjoxNjQyNzc0NTk3LCJleHAiOjE2NDI3NzQ2Mjd9.KUkURlpCWsh0VJFC4xrCOxr_dXNusRRjdjFb88Wb4Rw")
        );
        var sessionAliveUserSession = JbstUserSession.random(
                Username.random(),
                JbstJwtAccessToken.random(),
                new JbstJwtRefreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtdWx0aXVzZXI0MyIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJhZG1pbiJ9LHsiYXV0aG9yaXR5IjoidXNlciJ9XSwiaWF0IjoxNjQyNzc0Nzc4LCJleHAiOjQ3OTg0NDgzNzh9.06Ep_ri727dkMEVA3zptDb8tmFn1VJ1FIhjjbE-mxpw")
        );
        var usersSessions = List.of(sessionInvalidUserSession, sessionExpiredUserSession, sessionAliveUserSession);
        when(this.usersSessionsRepository.findByUsernameInAsAny(usernames)).thenReturn(usersSessions);

        // Act
        var sessionsValidatedTuple2 = this.componentUnderTest.getExpiredRefreshTokensSessions(usernames);

        // Assert
        verify(this.usersSessionsRepository).findByUsernameInAsAny(usernames);
        assertThat(sessionsValidatedTuple2).isNotNull();
        assertThat(sessionsValidatedTuple2.expiredOrInvalidSessionIds()).isNotNull();
        assertThat(sessionsValidatedTuple2.expiredOrInvalidSessionIds()).hasSize(2);
        assertThat(sessionsValidatedTuple2.expiredOrInvalidSessionIds()).containsExactlyInAnyOrder(
                sessionInvalidUserSession.id(),
                sessionExpiredUserSession.id()
        );
        assertThat(sessionsValidatedTuple2.expiredSessions()).isNotNull();
        assertThat(sessionsValidatedTuple2.expiredSessions()).hasSize(1);
        assertThat(sessionsValidatedTuple2.expiredSessions().get(0).a().value()).isEqualTo("multiuser43");
    }

    @Test
    void enableUserRequestMetadataRenewCronTest() {
        // Act
        this.componentUnderTest.enableUserRequestMetadataRenewCron();

        // Assert
        verify(this.usersSessionsRepository).enableMetadataRenewCron();
    }

    @Test
    void enableUserRequestMetadataRenewManuallyTest() {
        // Arrange
        var sessionId = entity(JbstUserSessionId.class);

        // Act
        this.componentUnderTest.enableUserRequestMetadataRenewManually(sessionId);

        // Assert
        verify(this.usersSessionsRepository).enableMetadataRenewManually(sessionId);
    }

    @ParameterizedTest
    @MethodSource("renewUserRequestMetadataArgs")
    void renewUserRequestMetadataTest(
            boolean metadataRenewCron,
            boolean metadataRenewManually,
            TupleToggle<Boolean> expectedMetadataRenewCron,
            TupleToggle<Boolean> expectedMetadataRenewManually
    ) {
        // Arrange
        var httpServletRequest = mock(HttpServletRequest.class);
        var session = JbstUserSession.ofPersisted(
                entity(JbstUserSessionId.class),
                getCurrentTimestamp(),
                getCurrentTimestamp(),
                Username.random(),
                JbstJwtAccessToken.random(),
                JbstJwtRefreshToken.random(),
                JbstUserRequestMetadata.random(),
                metadataRenewCron,
                metadataRenewManually
        );

        // Act
        this.componentUnderTest.renewUserRequestMetadata(session, httpServletRequest);

        // Assert
        var eventAC = ArgumentCaptor.forClass(JbstEventSessionUserRequestMetadataRenew.class);
        if (session.isRenewRequired()) {
            verify(this.eventsPublisher).publishSessionUserRequestMetadataRenew(eventAC.capture());
            var event = eventAC.getValue();
            assertThat(event.username()).isEqualTo(session.username());
            assertThat(event.session()).isEqualTo(session);
            assertThat(event.clientIpAddr()).isEqualTo(getClientIpAddr(httpServletRequest));
            assertThat(event.userAgentHeader()).isEqualTo(new JbstUserAgentHeader(httpServletRequest));
            assertThat(event.metadataRenewCron()).isEqualTo(expectedMetadataRenewCron);
            assertThat(event.metadataRenewManually()).isEqualTo(expectedMetadataRenewManually);
        }
    }

    @Test
    void deleteByIdTest() {
        // Arrange
        var sessionId = entity(JbstUserSessionId.class);

        // Act
        this.componentUnderTest.deleteById(sessionId);

        // Assert
        verify(this.usersSessionsRepository).delete(sessionId);
    }

    @Test
    void deleteAllExceptCurrentTest() {
        // Arrange
        var username = entity(Username.class);
        var requestAccessToken = JbstRequestAccessToken.random();

        // Act
        this.componentUnderTest.deleteAllExceptCurrent(username, requestAccessToken);

        // Assert
        verify(this.usersSessionsRepository).deleteByUsernameExceptAccessToken(username, requestAccessToken);
    }

    @Test
    void deleteAllExceptCurrentAsSuperuserTest() {
        // Arrange
        var requestAccessToken = JbstRequestAccessToken.random();

        // Act
        this.componentUnderTest.deleteAllExceptCurrentAsSuperuser(requestAccessToken);

        // Assert
        verify(this.usersSessionsRepository).deleteExceptAccessToken(requestAccessToken);
    }
}
