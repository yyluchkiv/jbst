package jbst.iam.postgres.repositories;

import jbst.iam.configurations.JbstConfigurationPostgresRepositories;
import jbst.iam.domain.db.Invitation;
import jbst.iam.domain.dto.requests.RequestNewInvitationParams;
import jbst.foundation.domain.ids.InvitationId;
import jbst.iam.domain.postgres.db.PostgresDbInvitation;
import jbst.iam.postgres.configs.PostgresBeforeAllCallback;
import jbst.iam.postgres.configs.TestsJbstConfigurationPostgresRepositoriesRunner;
import jbst.iam.repositories.postgres.PostgresInvitationsRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.tuples.TuplePresence;

import static jbst.iam.domain.db.Invitation.INVITATION_CODES_UNUSED;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static jbst.foundation.utilities.random.EntityUtility.entity;
import static jbst.foundation.utilities.random.RandomUtility.randomElement;
import static jbst.foundation.utilities.random.RandomUtility.randomStringLetterOrNumbersOnly;

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
class PostgresInvitationsRepositoryIT extends TestsJbstConfigurationPostgresRepositoriesRunner {

    private final PostgresInvitationsRepository invitationsRepository;

    @Override
    public JpaRepository<PostgresDbInvitation, String> getJpaRepository() {
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
        var saved = this.invitationsRepository.saveAll(PostgresDbInvitation.dummies1());

        var notExistentInvitationId = entity(InvitationId.class);
        var notExistentInvitation = randomStringLetterOrNumbersOnly(Invitation.DEFAULT_INVITATION_CODE_LENGTH);

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
        this.invitationsRepository.saveAll(PostgresDbInvitation.dummies2());

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
        var saved = this.invitationsRepository.saveAll(PostgresDbInvitation.dummies1());
        var notExistentInvitationId = entity(InvitationId.class);
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
        var saved = this.invitationsRepository.saveAll(PostgresDbInvitation.dummies1());
        var request = RequestNewInvitationParams.random();

        // Act-Assert-0
        assertThat(this.invitationsRepository.count()).isEqualTo(6);

        // Act-Assert-1
        this.invitationsRepository.saveAs(randomElement(saved).invitation());
        assertThat(this.invitationsRepository.count()).isEqualTo(6);

        // Act-Assert-2
        var existentInvitationId = this.invitationsRepository.saveAs(Invitation.randomNotPersisted());
        assertThat(this.invitationsRepository.count()).isEqualTo(7);
        var notExistentInvitationId = entity(InvitationId.class);
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
