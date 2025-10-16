package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.properties.configs.SecurityJwtConfigs;
import org.junit.jupiter.api.Test;

import static jbst.foundation.utilities.random.EntityUtility.entity;
import static org.assertj.core.api.Assertions.assertThat;

class UserOnInitTest {

    @Test
    void getEmailNullTest() {
        // Arrange
        var users = SecurityJwtConfigs.hardcoded().getEssenceConfigs().getUsersOnInit();

        // Act
        var email = users.getUsers().get(0).getEmailOrNull();

        // Assert
        assertThat(email).isNull();
    }

    @Test
    void getEmailTest() {
        // Arrange
        var users = entity(UserOnInit.class);

        // Act
        var email = users.getEmailOrNull();

        // Assert
        assertThat(email).isNotNull();
    }
}
