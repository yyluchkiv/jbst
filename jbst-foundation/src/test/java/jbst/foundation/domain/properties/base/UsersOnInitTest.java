package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.time.ZoneId.systemDefault;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

class UsersOnInitTest {

    private static Stream<Arguments> getAuthoritiesTest() {
        return Stream.of(
                Arguments.of(null, emptySet()),
                Arguments.of(
                        List.of(
                                new UserOnInit(Username.of("user1"), Password.of("pass1"), systemDefault(), null, false, null)
                        ),
                        emptySet()
                ),
                Arguments.of(
                        List.of(
                                new UserOnInit(Username.of("user1"), Password.of("pass1"), systemDefault(), null, false, Set.of("user")),
                                new UserOnInit(Username.of("user2"), Password.of("pass2"), systemDefault(), null, false, Set.of("admin", "user"))
                        ),
                        Set.of("user", "admin")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getAuthoritiesTest")
    void getAuthoritiesTest(List<UserOnInit> users, Set<String> expected) {
        // Act
       var actual = new UsersOnInit(true, users).getAuthorities();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
