package jbst.foundation.domain.strings;

import org.junit.jupiter.api.Test;

import static jbst.foundation.domain.strings.JbstMessages.*;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.assertj.core.api.Assertions.assertThat;

class JbstMessagesTest {

    @Test
    void pleaseWaitTest() {
        // Arrange
        var prefix = randomString();

        // Act
        var actual = pleaseWait(prefix);

        // Assert
        assertThat(actual).isEqualTo(prefix + ". Please wait...");
    }

    @Test
    void contactDevelopmentTeamTest() {
        // Arrange
        var prefix = randomString();

        // Act
        var actual = contactDevelopmentTeam(prefix);

        // Assert
        assertThat(actual).isEqualTo(prefix + ". Please contact development team");
    }

    @Test
    void unexpectedErrorOccurredTest() {
        // Act
        var actual = unexpectedErrorOccurred();

        // Assert
        assertThat(actual).isEqualTo("An unexpected error occurred. Please contact development team");
    }

    @Test
    void notImplementedYetTest() {
        // Act
        var actual = notImplementedYet();

        // Assert
        assertThat(actual).isEqualTo("Not implemented yet. Please contact development team");
    }

    @Test
    void attributeNameTest() {
        // Arrange
        var attributeName = randomString();

        // Act
        var actual = invalidAttribute(attributeName);

        // Assert
        assertThat(actual).isEqualTo("Attribute `" + attributeName + "` is invalid");
    }

    @Test
    void entityNotFoundTest() {
        // Arrange
        var entity = randomString();

        // Act
        var actual = entityNotFound("userNAME", entity);

        // Assert
        assertThat(actual).isEqualTo("Username " + entity + " is not found");
    }

    @Test
    void entityAlreadyUsedTest() {
        // Arrange
        var entity = randomString();

        // Act
        var actual = entityAlreadyUsed("userNAME", entity);

        // Assert
        assertThat(actual).isEqualTo("Username " + entity + " is already used");
    }

    @Test
    void entityAccessDeniedTest() {
        // Arrange
        var entity = randomString();

        // Act
        var actual = entityAccessDenied("userNAME", entity);

        // Assert
        assertThat(actual).isEqualTo("Username " + entity + " access denied");
    }
}
