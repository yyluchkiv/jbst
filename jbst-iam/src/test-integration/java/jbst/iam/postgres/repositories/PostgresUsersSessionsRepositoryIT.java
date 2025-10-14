package jbst.iam.postgres.repositories;

import jbst.foundation.configurations.JbstConfigurationPostgresRepositories;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.ids.UserSessionId;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.databases.postgres.entities.PostgresDbUserSession;
import jbst.iam.postgres.configs.PostgresBeforeAllCallback;
import jbst.iam.postgres.configs.TestsJbstConfigurationPostgresRepositoriesRunner;
import jbst.foundation.repositories.postgres.PostgresUsersSessionsRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.tuples.TuplePresence;

import java.util.List;
import java.util.Set;

import static jbst.foundation.domain.jwt.JwtAccessToken.accessTokens;
import static jbst.iam.tests.converters.postgres.PostgresUserConverter.toAccessTokensAsStrings2;
import static jbst.iam.tests.converters.postgres.PostgresUserConverter.toUsernamesAsStrings2;
import static jbst.iam.tests.converters.postgres.PostgresUserSessionConverter.toMetadataRenewCron;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static jbst.foundation.utilities.random.EntityUtility.entity;
import static jbst.foundation.utilities.random.RandomUtility.randomElement;

@ExtendWith({
        PostgresBeforeAllCallback.class
})
@SpringBootTest(
        webEnvironment = NONE,
        classes = {
                JbstConfigurationPostgresRepositories.class
        }
)
@AutoConfigureDataJpa
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PostgresUsersSessionsRepositoryIT extends TestsJbstConfigurationPostgresRepositoriesRunner {

    private final PostgresUsersSessionsRepository usersSessionsRepository;

    @Override
    public JpaRepository<PostgresDbUserSession, String> getJpaRepository() {
        return this.usersSessionsRepository;
    }

    @Test
    void readIntegrationTests() {
        // Arrange
        var saved = this.usersSessionsRepository.saveAll(PostgresDbUserSession.dummies1());

        var notExistentSessionId = entity(UserSessionId.class);

        var savedSession = saved.get(0);
        var existentSessionId = savedSession.userSessionId();

        // Act
        var count = this.usersSessionsRepository.count();

        // Assert
        assertThat(count).isEqualTo(7);
        assertThat(this.usersSessionsRepository.isPresent(existentSessionId, Username.random())).isEqualTo(TuplePresence.absent());
        assertThat(this.usersSessionsRepository.isPresent(existentSessionId, savedSession.getUsername())).isEqualTo(TuplePresence.present(savedSession.userSession()));
        assertThat(this.usersSessionsRepository.isPresent(notExistentSessionId, Username.random())).isEqualTo(TuplePresence.absent());
        assertThat(this.usersSessionsRepository.isPresent(existentSessionId)).isEqualTo(TuplePresence.present(savedSession.userSession()));
        assertThat(this.usersSessionsRepository.isPresent(notExistentSessionId)).isEqualTo(TuplePresence.absent());
        assertThat(this.usersSessionsRepository.isPresent(JwtAccessToken.of("awt1")).present()).isTrue();
        assertThat(this.usersSessionsRepository.isPresent(JwtAccessToken.of("awt2")).present()).isTrue();
        assertThat(this.usersSessionsRepository.isPresent(JwtAccessToken.of("awt777")).present()).isFalse();
        assertThat(this.usersSessionsRepository.isPresent(JwtRefreshToken.of("rwt1")).present()).isTrue();
        assertThat(this.usersSessionsRepository.isPresent(JwtRefreshToken.of("rwt2")).present()).isTrue();
        assertThat(this.usersSessionsRepository.isPresent(JwtRefreshToken.of("rwt777")).present()).isFalse();
        assertThat(this.usersSessionsRepository.getUsersSessionsTable(Username.of("user777"), new RequestAccessToken("awt2"))).isEmpty();
        var usersSessions = this.usersSessionsRepository.getUsersSessionsTable(Username.hardcoded(), new RequestAccessToken("awt2"));
        assertThat(usersSessions).hasSize(4);
        assertThat(usersSessions.get(0).current()).isTrue();
        assertThat(usersSessions.get(0).activity()).isEqualTo("Current session");
        usersSessions.stream().skip(1).forEach(userSession -> {
            assertThat(userSession.current()).isFalse();
            assertThat(userSession.activity()).isEqualTo("—");
        });
        var sessionsTable = this.usersSessionsRepository.getSessionsTable(accessTokens("awt3", "atoken11", "atoken"), new RequestAccessToken("atoken"));
        assertThat(sessionsTable.activeSessions()).hasSize(3);
        assertThat(sessionsTable.activeSessions().get(0).current()).isTrue();
        assertThat(sessionsTable.activeSessions().get(0).who().value()).isEqualTo("sa");
        assertThat(sessionsTable.activeSessions().get(1).current()).isFalse();
        assertThat(sessionsTable.activeSessions().get(1).who().value()).isEqualTo("jbst");
        assertThat(sessionsTable.activeSessions().get(2).current()).isFalse();
        assertThat(sessionsTable.activeSessions().get(2).who().value()).isEqualTo("user1");
        assertThat(sessionsTable.inactiveSessions()).hasSize(4);
        sessionsTable.inactiveSessions().forEach(inactiveSession -> assertThat(inactiveSession.current()).isFalse());
        assertThat(sessionsTable.inactiveSessions().get(0).who().value()).isEqualTo("jbst");
        assertThat(sessionsTable.inactiveSessions().get(1).who().value()).isEqualTo("jbst");
        assertThat(sessionsTable.inactiveSessions().get(2).who().value()).isEqualTo("jbst");
        assertThat(sessionsTable.inactiveSessions().get(3).who().value()).isEqualTo("user1");
        assertThat(this.usersSessionsRepository.findByUsernameInAsAny(Set.of(Username.hardcoded(), Username.of("sa")))).hasSize(5);
        assertThat(this.usersSessionsRepository.findByUsernameInAsAny(Set.of(Username.hardcoded(), Username.of("user1")))).hasSize(6);
        assertThat(this.usersSessionsRepository.findByUsernameInAsAny(Set.of(Username.of("user1"), Username.of("sa")))).hasSize(3);
        assertThat(this.usersSessionsRepository.findByUsernameInAsAny(Set.of(Username.of("user777"), Username.of("sa777")))).isEmpty();
    }

    @Test
    void enableMetadataRenewCronTest() {
        // Arrange
        var saved1 = this.usersSessionsRepository.saveAll(PostgresDbUserSession.dummies1());

        // Assert-0
        assertThat(toMetadataRenewCron(saved1))
                .hasSize(1)
                .contains(false);

        // Act
        this.usersSessionsRepository.enableMetadataRenewCron();

        // Assert-1
        assertThat(toMetadataRenewCron(this.usersSessionsRepository.findAll()))
                .hasSize(1)
                .contains(true);
    }

    @Test
    void enableMetadataRenewManuallyTest() {
        // Arrange
        var saved1 = this.usersSessionsRepository.saveAll(PostgresDbUserSession.dummies1());

        // Assert-0
        assertThat(toMetadataRenewCron(saved1))
                .hasSize(1)
                .contains(false);
        var sessionId1 = UserSessionId.of(saved1.get(2).getId());
        var sessionId2 = UserSessionId.of(saved1.get(5).getId());

        // Act
        var session1 = this.usersSessionsRepository.enableMetadataRenewManually(sessionId1);
        var session2 = this.usersSessionsRepository.enableMetadataRenewManually(sessionId2);

        // Assert-1
        assertThat(session1).isNotNull();
        assertThat(session2).isNotNull();
        var sessions = this.usersSessionsRepository.findAll();
        sessions.forEach(session -> {
            var sessionId = session.getId();
            if (sessionId1.value().equals(sessionId) || sessionId2.value().equals(sessionId)) {
                assertThat(session.isMetadataRenewManually()).isTrue();
            } else {
                assertThat(session.isMetadataRenewManually()).isFalse();
            }
        });
    }

    @Test
    void deletionIntegrationTests() {
        // Arrange
        var saved = this.usersSessionsRepository.saveAll(PostgresDbUserSession.dummies1());

        var existentSessionId = saved.get(0).userSessionId();
        var existentSessionsIds = Set.of(saved.get(1).userSessionId(), saved.get(5).userSessionId());

        // Act-Assert-0
        assertThat(this.usersSessionsRepository.count()).isEqualTo(7);

        // Act-Assert-1
        this.usersSessionsRepository.delete(existentSessionId);
        assertThat(this.usersSessionsRepository.count()).isEqualTo(6);

        // Act-Assert-2
        this.usersSessionsRepository.delete(existentSessionsIds);
        assertThat(this.usersSessionsRepository.count()).isEqualTo(4);

        // Act-Assert-1
        this.usersSessionsRepository.deleteByUsernames(Set.of(Username.hardcoded(), Username.of("sa")));
        assertThat(this.usersSessionsRepository.count()).isEqualTo(1);
        assertThat(this.usersSessionsRepository.findAll().get(0).getUsername().value()).isEqualTo("user1");
    }

    @Test
    void deleteByUsernameExceptAccessTokenTest() {
        // Arrange
        this.usersSessionsRepository.saveAll(PostgresDbUserSession.dummies2());

        // Act
        var count1 = this.usersSessionsRepository.count();
        this.usersSessionsRepository.deleteByUsernameExceptAccessToken(Username.hardcoded(), new RequestAccessToken("token2"));
        var count2 = this.usersSessionsRepository.count();
        var sessions = this.usersSessionsRepository.findAll();
        assertThat(count1).isEqualTo(4);
        assertThat(count2).isEqualTo(2);
        assertThat(toUsernamesAsStrings2(sessions)).isEqualTo(List.of(Username.hardcoded().value(), "admin"));
        assertThat(toAccessTokensAsStrings2(sessions)).isEqualTo(List.of("token2", "token4"));
    }

    @Test
    void deleteExceptAccessTokenTest() {
        // Arrange
        this.usersSessionsRepository.saveAll(PostgresDbUserSession.dummies2());

        // Act
        var count1 = this.usersSessionsRepository.count();
        this.usersSessionsRepository.deleteExceptAccessToken(new RequestAccessToken("token2"));
        var count2 = this.usersSessionsRepository.count();
        var sessions = this.usersSessionsRepository.findAll();

        assertThat(count1).isEqualTo(4);
        assertThat(count2).isEqualTo(1);
        var session = sessions.get(0);
        assertThat(session.getUsername()).isEqualTo(Username.hardcoded());
        assertThat(session.getAccessToken().value()).isEqualTo("token2");
    }

    @Test
    void saveIntegrationTests() {
        // Arrange
        var saved = this.usersSessionsRepository.saveAll(PostgresDbUserSession.dummies1());

        // Act-Assert-0
        assertThat(this.usersSessionsRepository.count()).isEqualTo(7);

        // Act-Assert-1
        this.usersSessionsRepository.saveAs(randomElement(saved).userSession());
        assertThat(this.usersSessionsRepository.count()).isEqualTo(7);

        // Act-Assert-2
        var existentSessionId = this.usersSessionsRepository.saveAs(JbstUserSession.randomNotPersistedSession()).id();
        assertThat(this.usersSessionsRepository.count()).isEqualTo(8);
        var notExistentSessionId = entity(UserSessionId.class);
        assertThat(this.usersSessionsRepository.isPresent(existentSessionId).present()).isTrue();
        assertThat(this.usersSessionsRepository.isPresent(notExistentSessionId).present()).isFalse();
    }
}
