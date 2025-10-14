package jbst.iam.mongo.repositories;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.iam.configurations.JbstConfigurationMongoRepositories;
import jbst.iam.domain.db.Invitation;
import jbst.foundation.domain.databases.UserEmailDetails;
import jbst.iam.domain.dto.requests.RequestUserRegistration0;
import jbst.iam.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.domain.ids.UserId;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.iam.domain.mongodb.MongoDbUser;
import jbst.iam.mongo.configs.MongoBeforeAllCallback;
import jbst.iam.mongo.configs.TestsJbstConfigurationMongoRepositoriesRunner;
import jbst.iam.repositories.mongodb.MongoUsersRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Set;

import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.entityNotFound;
import static jbst.foundation.utilities.random.EntityUtility.entity;
import static jbst.foundation.utilities.random.RandomUtility.randomElement;
import static jbst.iam.tests.converters.mongodb.MongoUserConverter.toUsernamesAsStrings1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
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
class MongoUsersRepositoryIT extends TestsJbstConfigurationMongoRepositoriesRunner {

    private final MongoUsersRepository usersRepository;

    @Override
    public MongoRepository<MongoDbUser, String> getMongoRepository() {
        return this.usersRepository;
    }

    @Test
    void readIntegrationTests() {
        // Arrange
        var saved = this.usersRepository.saveAll(MongoDbUser.dummies1());

        var notExistentUserId = entity(UserId.class);

        var savedUser = saved.get(0);
        var existentUserId = savedUser.userId();

        // Act
        var count = this.usersRepository.count();

        // Assert
        assertThat(count).isEqualTo(6);
        assertThat(this.usersRepository.isPresent(existentUserId)).isEqualTo(TuplePresence.present(savedUser.asJwtUser()));
        assertThat(this.usersRepository.isPresent(notExistentUserId)).isEqualTo(TuplePresence.absent());
        assertThat(this.usersRepository.loadUserByUsername(Username.of("sa1"))).isNotNull();
        assertThat(catchThrowable(() -> this.usersRepository.loadUserByUsername(Username.of("sa777"))))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageStartingWith(entityNotFound("Username", "sa777"));
        assertThat(this.usersRepository.existsByUsername(Username.of("sa1"))).isTrue();
        assertThat(this.usersRepository.existsByUsername(Username.of("sa777"))).isFalse();
        assertThat(this.usersRepository.findByUsernameAsJwtUserOrNull(Username.of("sa2"))).isNotNull();
        assertThat(this.usersRepository.findByUsernameAsJwtUserOrNull(Username.of("sa888"))).isNull();
        assertThat(this.usersRepository.findByEmailAsJwtUserOrNull(Email.of("sa3@" + JbstConstants.Domains.HARDCODED))).isNotNull();
        assertThat(this.usersRepository.findByEmailAsJwtUserOrNull(Email.of("sa999@" + JbstConstants.Domains.HARDCODED))).isNull();
        assertThat(this.usersRepository.findByEmail(Email.of("sa1@" + JbstConstants.Domains.HARDCODED))).isNotNull();
        assertThat(this.usersRepository.findByEmail(Email.of("sa2@" + JbstConstants.Domains.HARDCODED))).isNotNull();
        assertThat(this.usersRepository.findByEmail(Email.of("sa4@" + JbstConstants.Domains.HARDCODED))).isNull();
        assertThat(this.usersRepository.existsByEmail(Email.of("sa1@" + JbstConstants.Domains.HARDCODED))).isTrue();
        assertThat(this.usersRepository.existsByEmail(Email.of("sa4@" + JbstConstants.Domains.HARDCODED))).isFalse();
        assertThat(this.usersRepository.findByUsername(Username.of("sa1"))).isNotNull();
        assertThat(this.usersRepository.findByUsername(Username.of("sa2"))).isNotNull();
        assertThat(this.usersRepository.findByUsername(Username.of("sa4"))).isNull();
        assertThat(this.usersRepository.findByUsernameIn(
                Set.of(
                        Username.of("sa1"),
                        Username.of("admin1"),
                        Username.of("not_real1")
                )
        )).hasSize(2);
        assertThat(this.usersRepository.findByUsernameIn(
                List.of(
                        Username.of("sa3"),
                        Username.of("user1"),
                        Username.of("not_real2")
                )
        )).hasSize(2);

        assertThat(toUsernamesAsStrings1(this.usersRepository.findByAuthoritySuperadmin()))
                .hasSize(3)
                .containsExactlyInAnyOrder("sa1", "sa2", "sa3");

        assertThat(toUsernamesAsStrings1(this.usersRepository.findByAuthorityNotSuperadmin()))
                .hasSize(3)
                .containsExactlyInAnyOrder("admin1", "user1", "user2");

        assertThat(Username.asStrings(this.usersRepository.findSuperadminsUsernames()))
                .hasSize(3)
                .containsExactlyInAnyOrder("sa1", "sa2", "sa3");

        assertThat(Username.asStrings(this.usersRepository.findNotSuperadminsUsernames()))
                .hasSize(3)
                .containsExactlyInAnyOrder("admin1", "user1", "user2");

        var jwtUser = this.usersRepository.loadUserByUsername(Username.of("sa1"));
        assertThat(jwtUser).isNotNull();
        assertThat(jwtUser.username()).isEqualTo(Username.of("sa1"));
        assertThat(jwtUser.password()).isNotNull();
        assertThat(jwtUser.authorities()).isNotNull();
        assertThat(jwtUser.isAccountNonExpired()).isTrue();
        assertThat(jwtUser.isAccountNonLocked()).isTrue();
        assertThat(jwtUser.isCredentialsNonExpired()).isTrue();
        assertThat(jwtUser.isEnabled()).isTrue();

        var username = Username.random();
        var throwable = catchThrowable(() -> this.usersRepository.loadUserByUsername(username));
        assertThat(throwable)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageStartingWith(entityNotFound("Username", username.value()));
    }

    @Test
    void deletionIntegrationTests() {
        // Arrange
        this.usersRepository.saveAll(MongoDbUser.dummies1());

        // Act-Assert-0
        assertThat(this.usersRepository.count()).isEqualTo(6);

        // Act-Assert-1
        this.usersRepository.deleteByAuthorityNotSuperadmin();
        assertThat(toUsernamesAsStrings1(this.usersRepository.findAll()))
                .hasSize(3)
                .containsExactlyInAnyOrder("sa1", "sa2", "sa3");

        // Act-Assert-2
        this.usersRepository.deleteByAuthoritySuperadmin();
        assertThat(this.usersRepository.count()).isZero();
    }

    @Test
    void saveIntegrationTests() {
        // Arrange
        var saved = this.usersRepository.saveAll(MongoDbUser.dummies1());

        // Act-Assert-0
        assertThat(this.usersRepository.count()).isEqualTo(6);

        // Act-Assert-1
        this.usersRepository.saveAs(randomElement(saved).asJwtUser());
        assertThat(this.usersRepository.count()).isEqualTo(6);

        // Act-Assert-2
        var userId1 = this.usersRepository.saveAs(JwtUser.randomSuperadmin());
        assertThat(this.usersRepository.count()).isEqualTo(7);
        assertThat(userId1).isNotNull();
        assertThat(this.usersRepository.isPresent(userId1).present()).isTrue();
        assertThat(this.usersRepository.isPresent(entity(UserId.class)).present()).isFalse();

        // Act-Assert-3
        var userId2 = this.usersRepository.saveAs(RequestUserRegistration1.hardcoded(), Password.random(), Invitation.random());
        assertThat(this.usersRepository.count()).isEqualTo(8);
        assertThat(this.usersRepository.findByUsernameAsJwtUserOrNull(Username.of("registration11")).id()).isEqualTo(userId2);

        // Act-Assert-4
        var userId3 = this.usersRepository.saveAs(RequestUserRegistration0.hardcoded(), Password.random());
        assertThat(this.usersRepository.count()).isEqualTo(9);
        var user3 = this.usersRepository.findById(userId3.value()).orElse(null);
        assertThat(user3).isNotNull();
        assertThat(user3.getEmailDetails()).isEqualTo(UserEmailDetails.required());
        this.usersRepository.confirmEmail(user3.getEmail());
        user3 = this.usersRepository.findById(userId3.value()).orElse(null);
        assertThat(user3).isNotNull();
        assertThat(user3.getEmailDetails()).isEqualTo(UserEmailDetails.confirmed());
        assertThatNoException().isThrownBy(() -> this.usersRepository.confirmEmail(Email.random()));

        // Act-Assert-5
        var savedPassword = Password.random();
        var userId4 = this.usersRepository.saveAs(RequestUserRegistration0.random(), savedPassword);
        assertThat(this.usersRepository.count()).isEqualTo(10);
        var user4 = this.usersRepository.findById(userId4.value()).orElse(null);
        assertThat(user4).isNotNull();
        assertThat(user4.getPassword()).isEqualTo(savedPassword);
        var newPassword = Password.random();
        this.usersRepository.resetPassword(user4.getEmail(), newPassword);
        user4 = this.usersRepository.findById(userId4.value()).orElse(null);
        assertThat(user4).isNotNull();
        assertThat(user4.getPassword()).isEqualTo(newPassword);
        assertThatNoException().isThrownBy(() -> this.usersRepository.resetPassword(Email.random(), Password.random()));
    }
}
