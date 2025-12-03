package jbst.foundation.domain.databases.mongo;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static jbst.foundation.domain.random.JbstRandom.randomBoolean;
import static jbst.foundation.domain.random.JbstRandom.randomZoneId;
import static jbst.foundation.domain.reflection.JbstReflections.setPrivateField;
import static org.assertj.core.api.Assertions.assertThat;

class MongoDbUserTest {

    @Test
    void getNotNullAttributesTest() {
        // Arrange
        var user = new MongoDbUser(
                JbstUserCreationOption.random(),
                Username.random(),
                Password.random(),
                true,
                randomZoneId(),
                Set.of(
                        new SimpleGrantedAuthority("admin123")
                ),
                Email.random(),
                randomBoolean(),
                JbstUserEmailDetails.random()
        );

        // Act
        var actual = user.getNotNullAttributes();

        // Assert
        assertThat(actual).isEmpty();
    }

    @Test
    void getNotNullAttributesLegacyMigrationNullPointerExceptionTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        var user = new MongoDbUser(
                JbstUserCreationOption.random(),
                Username.random(),
                Password.random(),
                true,
                randomZoneId(),
                Set.of(
                        new SimpleGrantedAuthority("admin123")
                ),
                Email.random(),
                randomBoolean(),
                JbstUserEmailDetails.random()
        );
        setPrivateField(user, "attributes", null);

        // Act
        var actual = user.getNotNullAttributes();

        // Assert
        assertThat(actual).isEmpty();
    }
}
