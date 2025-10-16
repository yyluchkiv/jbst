package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.properties.configs.SecurityJwtConfigs;
import org.junit.jupiter.api.Test;

import static jbst.foundation.utilities.random.EntityUtility.entity;
import static org.assertj.core.api.Assertions.assertThat;

class UserOnInitTest {

    @Test
    void getEmailNullTest() {
        // Arrange
        var defaultUsers = SecurityJwtConfigs.hardcoded().getEssenceConfigs().getUsersOnInit();

        // Act
        var email = defaultUsers.getUsers().get(0).getEmailOrNull();

        // Assert
        assertThat(email).isNull();
    }

    @Test
    void getEmailTest() {
        // Arrange
        var defaultUsers = entity(UserOnInit.class);

        // Act
        var email = defaultUsers.getEmailOrNull();

        // Assert
        assertThat(email).isNotNull();
    }
}
