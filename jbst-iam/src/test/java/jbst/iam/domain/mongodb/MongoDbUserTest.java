package jbst.iam.domain.mongodb;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.UserEmailDetails;
import jbst.foundation.domain.enums.UserCreationOption;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;
import static jbst.foundation.utilities.random.RandomUtility.randomZoneId;
import static jbst.foundation.utilities.reflections.ReflectionUtility.setPrivateField;
import static org.assertj.core.api.Assertions.assertThat;

class MongoDbUserTest {

    @Test
    void getNotNullAttributesTest() {
        // Arrange
        var user = new MongoDbUser(
                UserCreationOption.random(),
                Username.random(),
                Password.random(),
                randomZoneId(),
                Set.of(
                        new SimpleGrantedAuthority("admin123")
                ),
                Email.random(),
                randomBoolean(),
                UserEmailDetails.random()
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
                UserCreationOption.random(),
                Username.random(),
                Password.random(),
                randomZoneId(),
                Set.of(
                        new SimpleGrantedAuthority("admin123")
                ),
                Email.random(),
                randomBoolean(),
                UserEmailDetails.random()
        );
        setPrivateField(user, "attributes", null);

        // Act
        var actual = user.getNotNullAttributes();

        // Assert
        assertThat(actual).isEmpty();
    }
}
