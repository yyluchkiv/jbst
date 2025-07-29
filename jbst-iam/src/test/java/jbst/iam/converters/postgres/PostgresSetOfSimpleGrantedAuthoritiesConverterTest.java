package jbst.iam.converters.postgres;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Set;
import java.util.stream.Stream;

import static jbst.foundation.domain.base.AbstractAuthority.INVITATIONS_READ;
import static jbst.foundation.domain.base.AbstractAuthority.INVITATIONS_WRITE;
import static jbst.foundation.domain.constants.JbstConstants.SpringAuthorities.SUPERADMIN;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PostgresSetOfSimpleGrantedAuthoritiesConverterTest {

    private static Stream<Arguments> convertToDatabaseColumnArgs() {
        return Stream.of(
                Arguments.of(null, ""),
                Arguments.of(Set.of(), ""),
                Arguments.of(Set.of(SUPERADMIN), "superadmin"),
                Arguments.of(Set.of(new SimpleGrantedAuthority(INVITATIONS_READ), new SimpleGrantedAuthority(INVITATIONS_WRITE)), "invitations:read;invitations:write")
        );
    }

    private static Stream<Arguments> convertToEntityAttributeArgs() {
        return Stream.of(
                Arguments.of(null, Set.of()),
                Arguments.of("", Set.of()),
                Arguments.of("superadmin", Set.of(SUPERADMIN)),
                Arguments.of("invitations:read;invitations:write", Set.of(new SimpleGrantedAuthority(INVITATIONS_READ), new SimpleGrantedAuthority(INVITATIONS_WRITE)))
        );
    }

    @Configuration
    static class ContextConfiguration {
        @Bean
        PostgresSetOfSimpleGrantedAuthoritiesConverter postgresSetOfSimpleGrantedAuthoritiesConverter() {
            return new PostgresSetOfSimpleGrantedAuthoritiesConverter();
        }
    }

    private final PostgresSetOfSimpleGrantedAuthoritiesConverter componentUnderTest;

    @ParameterizedTest
    @MethodSource("convertToDatabaseColumnArgs")
    void convertToDatabaseColumn(Set<SimpleGrantedAuthority> authorities, String expected) {
        // Act
        var actual = this.componentUnderTest.convertToDatabaseColumn(authorities);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("convertToEntityAttributeArgs")
    void convertToEntityAttribute(String authorities, Set<SimpleGrantedAuthority> expected) {
        // Act
        var actual = this.componentUnderTest.convertToEntityAttribute(authorities);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
