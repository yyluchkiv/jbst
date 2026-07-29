package jbst.foundation.integration.postgres.repos;

import jbst.foundation.configurations.JbstConfigurationPostgresRepositories;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.databases.postgres.entities.JbstPostgresUserToken;
import jbst.foundation.domain.dto.requests.JbstRequestUserToken;
import jbst.foundation.domain.ids.JbstTokenId;
import jbst.foundation.integration.postgres.configs.PostgresBeforeAllCallback;
import jbst.foundation.integration.postgres.configs.TestsJbstConfigurationPostgresRepositoriesRunner;
import jbst.foundation.repositories.postgres.JbstPostgresUsersTokensRepository;
import jbst.foundation.domain.random.JbstRandom;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;

import static jbst.foundation.domain.enums.JbstUserTokenType.EMAIL_CONFIRMATION;
import static jbst.foundation.domain.enums.JbstUserTokenType.PASSWORD_RESET;
import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static jbst.foundation.domain.random.JbstRandom.randomElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

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
class JbstPostgresUsersTokensRepositoryIT extends TestsJbstConfigurationPostgresRepositoriesRunner {

    private final JbstPostgresUsersTokensRepository usersTokensRepository;

    @Override
    public JpaRepository<?, String> getJpaRepository() {
        return this.usersTokensRepository;
    }

    @Test
    void readIntegrationTests() {
        // Arrange
        var saved = this.usersTokensRepository.saveAll(JbstPostgresUserToken.dummies1());

        var notExistentTokenId = entity(JbstTokenId.class);
        var notExistentToken = JbstRandom.randomString();

        var savedToken = saved.get(0);
        var existentTokenId = savedToken.tokenId();
        var existentToken = savedToken.getValue();
        var savedExpiredToken = saved.get(3);
        var expiredTokenId = savedExpiredToken.tokenId();
        var expiredToken = savedExpiredToken.getValue();
        var savedUsedToken = saved.get(5);
        var usedTokenId = savedUsedToken.tokenId();
        var usedToken = savedUsedToken.getValue();

        // Act
        var count = this.usersTokensRepository.count();

        // Assert
        assertThat(count).isEqualTo(6);
        assertThat(this.usersTokensRepository.findById(existentTokenId.value())).isNotEmpty();
        assertThat(this.usersTokensRepository.findById(notExistentTokenId.value())).isEmpty();
        assertThat(this.usersTokensRepository.findByValueAsAnyOrNull(existentToken)).isNotNull();
        assertThat(this.usersTokensRepository.findByValueAsAnyOrNull(notExistentToken)).isNull();
        assertThat(this.usersTokensRepository.findById(expiredTokenId.value())).isNotEmpty();
        assertThat(this.usersTokensRepository.findByValueAsAnyOrNull(expiredToken)).isNotNull();
        assertThat(this.usersTokensRepository.findById(usedTokenId.value())).isNotEmpty();
        assertThat(this.usersTokensRepository.findByValueAsAnyOrNull(usedToken)).isNotNull();
        assertThat(this.usersTokensRepository.findByUserTokenValidOrNull(new JbstRequestUserToken(Email.random(), EMAIL_CONFIRMATION))).isNull();
        assertThat(this.usersTokensRepository.findByUserTokenValidOrNull(new JbstRequestUserToken(Email.random(), PASSWORD_RESET))).isNull();
        assertThat(this.usersTokensRepository.findByUserTokenValidOrNull(new JbstRequestUserToken(Email.of("username1@gmail.com"), EMAIL_CONFIRMATION))).isNotNull();
        assertThat(this.usersTokensRepository.findByUserTokenValidOrNull(new JbstRequestUserToken(Email.of("username1@gmail.com"), PASSWORD_RESET))).isNull();
        assertThat(this.usersTokensRepository.findByUserTokenValidOrNull(new JbstRequestUserToken(Email.of("username2@gmail.com"), EMAIL_CONFIRMATION))).isNull();
        assertThat(this.usersTokensRepository.findByUserTokenValidOrNull(new JbstRequestUserToken(Email.of("username2@gmail.com"), PASSWORD_RESET))).isNotNull();
        assertThat(this.usersTokensRepository.findByUserTokenValidOrNull(new JbstRequestUserToken(Email.of("username3@gmail.com"), EMAIL_CONFIRMATION))).isNull();
        assertThat(this.usersTokensRepository.findByUserTokenValidOrNull(new JbstRequestUserToken(Email.of("username5@gmail.com"), EMAIL_CONFIRMATION))).isNull();
        assertThat(this.usersTokensRepository.findByUserTokenValidOrNull(new JbstRequestUserToken(Email.of("username6@gmail.com"),EMAIL_CONFIRMATION))).isNull();
    }

    @Test
    void deletionIntegrationTests() {
        // Arrange
        var saved = this.usersTokensRepository.saveAll(JbstPostgresUserToken.dummies1());
        var savedExpiredToken = saved.get(3);
        var expiredTokenId = savedExpiredToken.tokenId();
        var expiredToken = savedExpiredToken.getValue();
        var savedUsedToken = saved.get(5);
        var usedTokenId = savedUsedToken.tokenId();
        var usedToken = savedUsedToken.getValue();

        // Act-Assert-0
        assertThat(this.usersTokensRepository.count()).isEqualTo(6);

        // Act-Assert-1
        this.usersTokensRepository.cleanupExpired();
        assertThat(this.usersTokensRepository.count()).isEqualTo(3);
        assertThat(this.usersTokensRepository.findById(expiredTokenId.value())).isEmpty();
        assertThat(this.usersTokensRepository.findByValueAsAnyOrNull(expiredToken)).isNull();

        // Act-Assert-2
        this.usersTokensRepository.cleanupUsed();
        assertThat(this.usersTokensRepository.count()).isEqualTo(2);
        assertThat(this.usersTokensRepository.findById(usedTokenId.value())).isEmpty();
        assertThat(this.usersTokensRepository.findByValueAsAnyOrNull(usedToken)).isNull();
    }

    @Test
    void saveIntegrationTests() {
        // Arrange
        var saved = this.usersTokensRepository.saveAll(JbstPostgresUserToken.dummies1());

        // Act-Assert-0
        assertThat(this.usersTokensRepository.count()).isEqualTo(6);

        // Act-Assert-1
        this.usersTokensRepository.saveAs(randomElement(saved).asUserToken());
        assertThat(this.usersTokensRepository.count()).isEqualTo(6);

        // Act-Assert-2
        var existentTokenId = this.usersTokensRepository.saveAs(JbstUserToken.randomNotPersisted());
        assertThat(this.usersTokensRepository.count()).isEqualTo(7);
        var notExistentTokenId = entity(JbstTokenId.class);
        assertThat(this.usersTokensRepository.findById(existentTokenId.value())).isNotEmpty();
        assertThat(this.usersTokensRepository.findById(notExistentTokenId.value())).isEmpty();

        // Act-Assert-3
        var requestUserEmailToken = JbstRequestUserToken.fixed();
        var userEmailToken = this.usersTokensRepository.saveAs(requestUserEmailToken);
        assertThat(this.usersTokensRepository.count()).isEqualTo(8);
        assertThat(this.usersTokensRepository.findById(userEmailToken.id().value())).isNotEmpty();
        assertThat(this.usersTokensRepository.findByValueAsAnyOrNull(userEmailToken.value())).isNotNull();

        // Act-Assert-4
        var savedToken = saved.get(0);
        assertThat(savedToken.isUsed()).isFalse();
        savedToken.setUsed(true);
        var tokenId = this.usersTokensRepository.saveAs(savedToken.asUserToken());
        var updatedToken = this.usersTokensRepository.findById(tokenId.value());
        assertThat(updatedToken).isNotEmpty();
        assertThat(updatedToken.get().isUsed()).isTrue();
        var updatedAnyToken = this.usersTokensRepository.findByValueAsAnyOrNull(savedToken.getValue());
        assertThat(updatedAnyToken).isNotNull();
        assertThat(updatedAnyToken.used()).isTrue();
    }
}
