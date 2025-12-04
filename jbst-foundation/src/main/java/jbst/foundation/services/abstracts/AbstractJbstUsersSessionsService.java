package jbst.foundation.services.abstracts;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.enums.JbstAccountAccessMethod;
import jbst.foundation.domain.events.JbstEventSessionUserRequestMetadataAdd;
import jbst.foundation.domain.events.JbstEventSessionUserRequestMetadataRenew;
import jbst.foundation.domain.functions.JbstFunctionSessionUserRequestMetadataSave;
import jbst.foundation.domain.http.requests.JbstUserAgentHeader;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.sessions.JbstSessionsExpiredTable;
import jbst.foundation.domain.tuples.Tuple3;
import jbst.foundation.domain.tuples.TupleToggle;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.utils.JbstGeoUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jbst.foundation.domain.databases.JbstUserSession.ofNotPersisted;
import static jbst.foundation.domain.databases.JbstUserSession.ofPersisted;
import static jbst.foundation.domain.http.JbstHttpServletRequests.getClientIpAddr;
import static jbst.foundation.domain.strings.JbstMessages.entityAccessDenied;
import static jbst.foundation.domain.time.JbstTime.isPast;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractJbstUsersSessionsService implements JbstUsersSessionsService {

    // Publishers
    protected final JbstEventsPublisher eventsPublisher;
    // Repositories
    protected final JbstUsersSessionsRepository usersSessionsRepository;
    // Utils
    protected final JbstGeoUtils geoUtils;
    // Utilities
    protected final JbstSecurityUtils securityUtils;

    @Override
    public void assertAccess(Username username, JbstUserSessionId sessionId) {
        var tuplePresence = this.usersSessionsRepository.isPresent(sessionId, username);
        if (!tuplePresence.present()) {
            throw new AccessDeniedException(entityAccessDenied("Session", sessionId.value()));
        }
    }

    @Override
    public void save(JbstJwtUser user, JbstJwtAccessToken accessToken, JbstJwtRefreshToken refreshToken, HttpServletRequest httpServletRequest) {
        var username = user.username();
        var userSessionTP = this.usersSessionsRepository.isPresent(accessToken);
        var clientIpAddr = getClientIpAddr(httpServletRequest);
        var metadata = JbstUserRequestMetadata.processing(clientIpAddr);
        var session = userSessionTP.value();
        if (userSessionTP.present()) {
            session = ofPersisted(
                    session.id(),
                    session.createdAt(),
                    session.updatedAt(),
                    session.username(),
                    session.accessToken(),
                    session.refreshToken(),
                    metadata,
                    session.metadataRenewCron(),
                    session.metadataRenewManually()
            );
        } else {
            session = ofNotPersisted(username, accessToken, refreshToken, metadata);
        }
        session = this.usersSessionsRepository.saveAs(session);
        this.eventsPublisher.publishSessionUserRequestMetadataAdd(
                new JbstEventSessionUserRequestMetadataAdd(
                        username,
                        user.email(),
                        session,
                        clientIpAddr,
                        new JbstUserAgentHeader(httpServletRequest),
                        JbstAccountAccessMethod.getMethod(user.creationOption())
                )
        );
    }

    @Override
    public void refresh(JbstJwtUser user, JbstUserSession oldSession, JbstJwtAccessToken newAccessToken, JbstJwtRefreshToken newRefreshToken, HttpServletRequest httpServletRequest) {
        var username = user.username();
        var newSession = this.usersSessionsRepository.saveAs(ofNotPersisted(username, newAccessToken, newRefreshToken, oldSession.metadata()));
        this.usersSessionsRepository.delete(oldSession.id());
        this.eventsPublisher.publishSessionUserRequestMetadataAdd(
                new JbstEventSessionUserRequestMetadataAdd(
                        username,
                        user.email(),
                        newSession,
                        getClientIpAddr(httpServletRequest),
                        new JbstUserAgentHeader(httpServletRequest),
                        JbstAccountAccessMethod.SESSION_TOKEN
                )
        );
    }

    @Override
    public JbstUserSession saveUserRequestMetadata(JbstEventSessionUserRequestMetadataAdd event) {
        return this.saveUserRequestMetadata(event.getSaveFunction());
    }

    @Override
    public void saveUserRequestMetadata(JbstEventSessionUserRequestMetadataRenew event) {
        this.saveUserRequestMetadata(event.getSaveFunction());
    }

    @Override
    public JbstUserSession saveUserRequestMetadata(JbstFunctionSessionUserRequestMetadataSave saveFunction) {
        var session = saveFunction.session();
        var sessionProcessedMetadata = ofPersisted(
                session.id(),
                session.createdAt(),
                getCurrentTimestamp(),
                session.username(),
                session.accessToken(),
                session.refreshToken(),
                this.geoUtils.getUserRequestMetadataProcessed(saveFunction.clientIpAddr(), saveFunction.userAgentHeader()),
                saveFunction.metadataRenewCron().enabled() ? saveFunction.metadataRenewCron().value() : session.metadataRenewCron(),
                saveFunction.metadataRenewManually().enabled() ? saveFunction.metadataRenewManually().value() : session.metadataRenewManually()
        );
        return this.usersSessionsRepository.saveAs(sessionProcessedMetadata);
    }

    @Override
    public JbstSessionsExpiredTable getExpiredRefreshTokensSessions(Set<Username> usernames) {
        var usersSessions = this.usersSessionsRepository.findByUsernameInAsAny(usernames);
        List<Tuple3<Username, JbstJwtRefreshToken, JbstUserRequestMetadata>> expiredSessions = new ArrayList<>();
        Set<JbstUserSessionId> expiredOrInvalidSessionIds = new HashSet<>();

        usersSessions.forEach(userSession -> {
            var sessionId = userSession.id();
            var validatedClaims = this.securityUtils.validate(userSession.refreshToken());
            var isValid = validatedClaims.valid();
            if (isValid) {
                var isExpired = isPast(validatedClaims.getExpirationTimestamp());
                if (isExpired) {
                    expiredOrInvalidSessionIds.add(sessionId);
                    expiredSessions.add(
                            new Tuple3<>(
                                    validatedClaims.username(),
                                    userSession.refreshToken(),
                                    userSession.metadata()
                            )
                    );
                }
            } else {
                expiredOrInvalidSessionIds.add(sessionId);
            }
        });

        return new JbstSessionsExpiredTable(
                expiredSessions,
                expiredOrInvalidSessionIds
        );
    }

    @Override
    public void enableUserRequestMetadataRenewCron() {
        this.usersSessionsRepository.enableMetadataRenewCron();
    }

    @Override
    public void enableUserRequestMetadataRenewManually(JbstUserSessionId sessionId) {
        this.usersSessionsRepository.enableMetadataRenewManually(sessionId);
    }

    @Override
    public void renewUserRequestMetadata(JbstUserSession session, HttpServletRequest httpServletRequest) {
        if (session.isRenewRequired()) {
            this.eventsPublisher.publishSessionUserRequestMetadataRenew(
                    new JbstEventSessionUserRequestMetadataRenew(
                            session.username(),
                            session,
                            getClientIpAddr(httpServletRequest),
                            new JbstUserAgentHeader(httpServletRequest),
                            session.metadataRenewCron() ? TupleToggle.enabled(true) : TupleToggle.disabled(),
                            session.metadataRenewManually() ? TupleToggle.enabled(true) : TupleToggle.disabled()
                    )
            );
        }
    }

    @Override
    public void deleteById(JbstUserSessionId sessionId) {
        this.usersSessionsRepository.delete(sessionId);
    }

    @Override
    public void deleteAllExceptCurrent(Username username, JbstRequestAccessToken requestAccessToken) {
        this.usersSessionsRepository.deleteByUsernameExceptAccessToken(username, requestAccessToken);
    }

    @Override
    public void deleteAllExceptCurrentAsSuperuser(JbstRequestAccessToken requestAccessToken) {
        this.usersSessionsRepository.deleteExceptAccessToken(requestAccessToken);
    }
}
