package jbst.foundation.integration.mongo.repos;

import jbst.foundation.configurations.JbstConfigurationMongoRepositories;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.mongo.JbstMongoInvitation;
import jbst.foundation.domain.dto.requests.JbstRequestNewInvitationParams;
import jbst.foundation.domain.ids.JbstInvitationId;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.integration.mongo.configs.MongoBeforeAllCallback;
import jbst.foundation.integration.mongo.configs.TestsJbstConfigurationMongoRepositoriesRunner;
import jbst.foundation.repositories.mongo.JbstMongoInvitationsRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import static jbst.foundation.domain.databases.JbstInvitation.INVITATION_CODES_UNUSED;
import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static jbst.foundation.domain.random.JbstRandom.randomElement;
import static jbst.foundation.domain.random.JbstRandom.randomStringLetterOrNumbersOnly;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@ExtendWith({
        MongoBeforeAllCallback.class
})
@SpringBootTest(
        webEnvironment = NONE,
        classes = {
                JbstConfigurationMongoRepositories.class
        }
)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstMongoInvitationsRepositoryIT extends TestsJbstConfigurationMongoRepositoriesRunner {

    private final JbstMongoInvitationsRepository invitationsRepository;

    @Override
    public MongoRepository<JbstMongoInvitation, String> getMongoRepository() {
        return this.invitationsRepository;
    }

    @Test
    void findUnusedSortTest() {
        // Assert
        assertThat(INVITATION_CODES_UNUSED).hasToString("owner: ASC,code: ASC");
    }

    @Test
    void readIntegrationTests() {
        // Arrange
        var saved = this.invitationsRepository.saveAll(JbstMongoInvitation.dummies1());

        var notExistentInvitationId = entity(JbstInvitationId.class);
        var notExistentInvitation = randomStringLetterOrNumbersOnly(JbstInvitation.DEFAULT_INVITATION_CODE_LENGTH);

        var savedInvitation = saved.get(0);
        var existentInvitationId = savedInvitation.invitationId();
        var existentInvitation = savedInvitation.getCode();

        // Act
        var count = this.invitationsRepository.count();

        // Assert
        assertThat(count).isEqualTo(6);
        assertThat(this.invitationsRepository.isPresent(existentInvitationId)).isEqualTo(TuplePresence.present(savedInvitation.invitation()));
        assertThat(this.invitationsRepository.isPresent(notExistentInvitationId)).isEqualTo(TuplePresence.absent());
        assertThat(this.invitationsRepository.findResponseCodesByOwner(Username.of("user1"))).hasSize(2);
        assertThat(this.invitationsRepository.findResponseCodesByOwner(Username.of("user2"))).hasSize(3);
        assertThat(this.invitationsRepository.findResponseCodesByOwner(Username.of("user3"))).hasSize(1);
        assertThat(this.invitationsRepository.findResponseCodesByOwner(Username.of("user5"))).isEmpty();
        assertThat(this.invitationsRepository.findByCodeAsAny(notExistentInvitation)).isNull();
        assertThat(this.invitationsRepository.findByCodeAsAny(existentInvitation)).isNotNull();
        assertThat(this.invitationsRepository.countByOwner(Username.of("user1"))).isEqualTo(2);
        assertThat(this.invitationsRepository.countByOwner(Username.of("user2"))).isEqualTo(3);
        assertThat(this.invitationsRepository.countByOwner(Username.of("user3"))).isEqualTo(1);
        assertThat(this.invitationsRepository.countByOwner(Username.of("user5"))).isZero();
        assertThat(this.invitationsRepository.findByInvitedIsNull()).hasSize(5);
        assertThat(this.invitationsRepository.findByInvitedIsNotNull()).hasSize(1);
    }

    @Test
    void findUnusedAndSortingTests() {
        // Arrange
        this.invitationsRepository.saveAll(JbstMongoInvitation.dummies2());

        // Act
        var count = this.invitationsRepository.count();

        // Assert
        assertThat(count).isEqualTo(7);
        var unused = this.invitationsRepository.findUnused();
        assertThat(unused).hasSize(5);
        assertThat(unused.get(0).value()).isEqualTo("value111");
        assertThat(unused.get(1).value()).isEqualTo("value222");
        assertThat(unused.get(2).value()).isEqualTo("abc");
        assertThat(unused.get(3).value()).isEqualTo("value22");
        assertThat(unused.get(4).value()).isEqualTo("value44");
        var codes = this.invitationsRepository.findByInvitedIsNotNull(Sort.by("code").descending());
        assertThat(codes).hasSize(2);
        assertThat(codes.get(0).getCode()).isEqualTo("value234");
        assertThat(codes.get(1).getCode()).isEqualTo("value123");
    }

    @Test
    void deletionIntegrationTests() {
        // Arrange
        var saved = this.invitationsRepository.saveAll(JbstMongoInvitation.dummies1());
        var notExistentInvitationId = entity(JbstInvitationId.class);
        var existentInvitationId = saved.get(0).invitationId();

        // Act-Assert-0
        assertThat(this.invitationsRepository.count()).isEqualTo(6);

        // Act-Assert-1
        this.invitationsRepository.delete(notExistentInvitationId);
        assertThat(this.invitationsRepository.count()).isEqualTo(6);

        // Act-Assert-2
        this.invitationsRepository.delete(existentInvitationId);
        assertThat(this.invitationsRepository.count()).isEqualTo(5);

        // Act-Assert-1
        this.invitationsRepository.deleteByInvitedIsNotNull();
        assertThat(this.invitationsRepository.count()).isEqualTo(4);

        // Act-Assert-2
        this.invitationsRepository.deleteByInvitedIsNull();
        assertThat(this.invitationsRepository.count()).isZero();
    }

    @Test
    void saveIntegrationTests() {
        // Arrange
        var saved = this.invitationsRepository.saveAll(JbstMongoInvitation.dummies1());
        var request = JbstRequestNewInvitationParams.random();

        // Act-Assert-0
        assertThat(this.invitationsRepository.count()).isEqualTo(6);

        // Act-Assert-1
        this.invitationsRepository.saveAs(randomElement(saved).invitation());
        assertThat(this.invitationsRepository.count()).isEqualTo(6);

        // Act-Assert-2
        var existentInvitationId = this.invitationsRepository.saveAs(JbstInvitation.random());
        assertThat(this.invitationsRepository.count()).isEqualTo(7);
        var notExistentInvitationId = entity(JbstInvitationId.class);
        assertThat(this.invitationsRepository.isPresent(existentInvitationId).present()).isTrue();
        assertThat(this.invitationsRepository.isPresent(notExistentInvitationId).present()).isFalse();

        // Act-Assert-3
        this.invitationsRepository.saveAs(Username.hardcoded(), request);
        assertThat(this.invitationsRepository.count()).isEqualTo(8);
        var ownedInvitations = this.invitationsRepository.findByOwner(Username.hardcoded());
        assertThat(ownedInvitations).hasSize(1);
        var ownedInvitation = ownedInvitations.get(0);
        assertThat(ownedInvitation.getOwner()).isEqualTo(Username.hardcoded());
        assertThat(ownedInvitation.getAuthorities()).isEqualTo(getSimpleGrantedAuthorities(request.authorities()));
        assertThat(ownedInvitation.getCode()).hasSize(40);
    }
}
