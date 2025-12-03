package jbst.foundation.domain.spring;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static jbst.foundation.domain.base.AbstractAuthority.*;
import static jbst.foundation.domain.tests.constants.TestsJunitConstants.TWICE;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getResponseInvitationsAuthoritiesAsField;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;
import static org.assertj.core.api.Assertions.assertThat;

class JbstSpringAuthoritiesTest {

    private static Stream<Arguments> getResponseInvitationsAuthoritiesAsFieldArgs() {
        return Stream.of(
                Arguments.of(null, "—"),
                Arguments.of(Set.of(), "—"),
                Arguments.of(Set.of(
                        new SimpleGrantedAuthority(INVITATIONS_READ),
                        new SimpleGrantedAuthority(SUPERADMIN),
                        new SimpleGrantedAuthority(INVITATIONS_WRITE)),
                        "invitations:read, invitations:write, superadmin"
                )
        );
    }

    @RepeatedTest(TWICE)
    void getSimpleGrantedAuthoritiesAsListTest() {
        // Act
        var actual = getSimpleGrantedAuthorities(
                List.of(
                        SUPERADMIN,
                        INVITATIONS_WRITE,
                        INVITATIONS_READ
                )
        );

        // Assert
        assertThat(actual).hasSize(3);
        actual.forEach(authority -> assertThat(authority).isInstanceOf(SimpleGrantedAuthority.class));
    }

    @RepeatedTest(TWICE)
    void getSimpleGrantedAuthoritiesAsSetTest() {
        // Act
        var actual = getSimpleGrantedAuthorities(
                Set.of(
                        SUPERADMIN,
                        INVITATIONS_WRITE,
                        INVITATIONS_READ
                )
        );

        // Assert
        assertThat(actual).hasSize(3);
        actual.forEach(authority -> assertThat(authority).isInstanceOf(SimpleGrantedAuthority.class));
    }

    @ParameterizedTest
    @MethodSource("getResponseInvitationsAuthoritiesAsFieldArgs")
    void getResponseInvitationsAuthoritiesAsFieldTest(Set<SimpleGrantedAuthority> authorities, String expected) {
        // Act
        var actual = getResponseInvitationsAuthoritiesAsField(authorities);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
