package jbst.foundation.tests.mains;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PasswordManagerTest {

    @Configuration
    static class ContextConfiguration {

        @Bean
        BCryptPasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder(11);
        }

    }

    private final BCryptPasswordEncoder componentUnderTest;

    @Test
    @Disabled
    void generatePassword() {
        // Arrange
        var password = "User12";

        // Act
        var encodedPassword = this.componentUnderTest.encode(password);

        // Assert
        assertThat(encodedPassword).isNotNull();

        // Print
        // Uncomment only in development purposes to avoid printing encoded password during "mvn clean test"
        LOGGER.debug(encodedPassword);
    }

}
