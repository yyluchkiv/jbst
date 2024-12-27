package jbst.iam.utils.impl;

import io.jsonwebtoken.Jwts;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.TimeAmount;
import jbst.iam.domain.jwt.JwtAccessToken;
import jbst.iam.domain.jwt.JwtRefreshToken;
import jbst.iam.domain.jwt.JwtTokenCreationParams;
import jbst.iam.tests.domain.enums.TestAuthority;
import jbst.iam.utils.SecurityJwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.*;
import static java.util.Objects.nonNull;
import static java.util.TimeZone.getTimeZone;
import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.tests.constants.TestsDTFsConstants.DEFAULT_DATE_FORMAT_PATTERN;
import static jbst.foundation.utilities.random.RandomUtility.randomZoneId;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;
import static jbst.foundation.utilities.time.DateUtility.convertLocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class SecurityJwtTokenUtilsImplTest {

    private static Stream<Arguments> createJwtTokenTest() {
        return Stream.of(
                Arguments.of(new TimeAmount(1L, HOURS)),
                Arguments.of(new TimeAmount(10L, HOURS)),
                Arguments.of(new TimeAmount(100L, HOURS)),
                Arguments.of(new TimeAmount(1L, MINUTES)),
                Arguments.of(new TimeAmount(10L, MINUTES)),
                Arguments.of(new TimeAmount(100L, MINUTES)),
                Arguments.of(new TimeAmount(1L, SECONDS)),
                Arguments.of(new TimeAmount(10L, SECONDS)),
                Arguments.of(new TimeAmount(100L, SECONDS))
        );
    }

    private static Stream<Arguments> validateTest() {
        return Stream.of(
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbjIzIiwicm9sZXMiOlsiQURNSU4iLCJVU0VSIl0sImlhdCI6MTY0MTkyNDM4OSwiZXhwIjo3OTUzMjcxNTg5fQ.8vsV_RChHNOepRu452JlUTfS9qAo4K9qTROiop4WzUI", true),
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTIiLCJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXSwiaWF0IjoxNjQxOTI0NDA5LCJleHAiOjc5NTMyNzE2MDl9.QKi36h-8SuFDT-vr6PUN7_avwOsNK0d5uwSOP96G-VQ", true),
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtdWx0aXVzZXI0MyIsInJvbGVzIjpbIkFETUlOIiwiVVNFUiJdLCJpYXQiOjE2NDE5MzE4MjYsImV4cCI6MTY0MTkzMTgzNn0.xO49IBj2DKLvkUySWms1sK1B7IDzKH579nJgpbl2Mz0", true), // expired jwt token
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTIiLCJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXSwiaWF0IjoxNjQxOTI0NDA5LCJleHAiOjc5NTMyNzE2MDl9.p9gNAWxL-vi81zy7ECfqqDnjQ7GfvhyDMJyKJ4iBQyq1", false),
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTIiLCJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXSwiaWF0IjoxNjQxOTI0NDA5LCJleHAiOjc5NTMyNzE2MDl9.p9gNAWxL-vi81zy7ECfqqDnjQ7GfvhyDMJyKJ4iBQyq2", false)
        );
    }

    private static Stream<Arguments> isExpiredTest() {
        return Stream.of(
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbjIzIiwicm9sZXMiOlsiQURNSU4iLCJVU0VSIl0sImlhdCI6MTY0MTkyNDM4OSwiZXhwIjo3OTUzMjcxNTg5fQ.RUiEg-2yMDjl5b-TOMwpuXfOGq5zTQiosO9IoteMDAo1", true),
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbjIzIiwicm9sZXMiOlsiQURNSU4iLCJVU0VSIl0sImlhdCI6MTY0MTkyNDM4OSwiZXhwIjo3OTUzMjcxNTg5fQ.8vsV_RChHNOepRu452JlUTfS9qAo4K9qTROiop4WzUI", false),
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTIiLCJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXSwiaWF0IjoxNjQxOTI0NDA5LCJleHAiOjc5NTMyNzE2MDl9.QKi36h-8SuFDT-vr6PUN7_avwOsNK0d5uwSOP96G-VQ", false),
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtdWx0aXVzZXI0MyIsInJvbGVzIjpbIkFETUlOIiwiVVNFUiJdLCJpYXQiOjE2NDE5MzE4MjYsImV4cCI6MTY0MTkzMTgzNn0.EhKtfHCgOGrJEGR0G8czSHf1T6MywDYIKYpxlxV8GxI", true)
        );
    }

    private static Stream<Arguments> getUsernameByClaimsTest() {
        return Stream.of(
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbjIzIiwicm9sZXMiOlsiQURNSU4iLCJVU0VSIl0sImlhdCI6MTY0MTkyNDM4OSwiZXhwIjo3OTUzMjcxNTg5fQ.8vsV_RChHNOepRu452JlUTfS9qAo4K9qTROiop4WzUI", Username.of("admin23")),
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTIiLCJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXSwiaWF0IjoxNjQxOTI0NDA5LCJleHAiOjc5NTMyNzE2MDl9.QKi36h-8SuFDT-vr6PUN7_avwOsNK0d5uwSOP96G-VQ", Username.of("user12")),
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtdWx0aXVzZXI0MyIsInJvbGVzIjpbIkFETUlOIiwiVVNFUiJdLCJpYXQiOjE2NDE5MzE4MjYsImV4cCI6MTY0MTkzMTgzNn0.xO49IBj2DKLvkUySWms1sK1B7IDzKH579nJgpbl2Mz0", Username.of("multiuser43")) // expired jwt token
        );
    }

    private static Stream<Arguments> versionsTests() {
        return Stream.of(
                Arguments.of(
                        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtdWx0aXVzZXI0MyIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJ1c2VyIn1dLCJpYXQiOjE2NDIyMDUyNzQsImV4cCI6NDc5Nzg3ODg3NH0.jQa_1ox0jj621g0256jlv4Ch9Ifrynjm1a76SFHvox8",
                        "15.01.2022 02:07:54",
                        "15.01.2122 02:07:54",
                        List.of(
                                new SimpleGrantedAuthority(TestAuthority.USER.getValue())
                        )
                ),
                Arguments.of(
                        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtdWx0aXVzZXI0MyIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJhZG1pbiJ9LHsiYXV0aG9yaXR5IjoidXNlciJ9XSwiaWF0IjoxNjQyMjA1MTg4LCJleHAiOjQ3OTc4Nzg3ODh9.pCkKoWBOT2u6bsr4iyESHmCEJaLBYZXU-unv1kqOtOc",
                        "15.01.2022 02:06:28",
                        "15.01.2122 02:06:28",
                        List.of(
                                new SimpleGrantedAuthority(TestAuthority.ADMIN.getValue()),
                                new SimpleGrantedAuthority(TestAuthority.USER.getValue())
                        )
                )
        );
    }

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        // Properties
        private final JbstProperties jbstProperties;

        @Bean
        SecurityJwtTokenUtils securityJwtTokenUtility() {
            return new SecurityJwtTokenUtilsImpl(
                    this.jbstProperties
            );
        }
    }

    private final JbstProperties jbstProperties;

    private final SecurityJwtTokenUtils componentUnderTest;

    @Disabled("Only for debugging purposes")
    @Test
    void generateJwtSecretKey() {
        // Act
        var encodedSecretKey = Base64.getEncoder().encodeToString(Jwts.SIG.HS256.key().build().getEncoded());

        // Assert
        LOGGER.info("key: {}", encodedSecretKey);
    }

    @Test
    void createJwtAccessTokenTest() {
        // Arrange
        var username = Username.of("multiuser43");
        var authorities = getSimpleGrantedAuthorities("admin", "user");
        var creationParams = new JwtTokenCreationParams(username, authorities, randomZoneId());

        // Act
        var accessToken = this.componentUnderTest.createJwtAccessToken(creationParams);

        // Assert
        var validatedClaims = this.componentUnderTest.validate(accessToken);
        assertThat(validatedClaims.username()).isEqualTo(username);
        var zoneId = creationParams.zoneId();
        var timeAmount = this.jbstProperties.getSecurityJwtConfigs().getJwtTokensConfigs().getAccessToken().getExpiration();
        var expiration = convertLocalDateTime(
                LocalDateTime.now(zoneId).plus(timeAmount.getAmount(), timeAmount.getUnit()),
                zoneId
        );
        assertThat(validatedClaims.expirationDate()).isBeforeOrEqualTo(expiration);
    }

    @Test
    void createJwtRefreshTokenTest() {
        // Arrange
        var expectedUsername = Username.of("multiuser43");
        var authorities = getSimpleGrantedAuthorities("admin", "user");
        var creationParams = new JwtTokenCreationParams(expectedUsername, authorities, randomZoneId());

        // Act
        var refreshToken = this.componentUnderTest.createJwtRefreshToken(creationParams);

        // Assert
        var validatedClaims = this.componentUnderTest.validate(refreshToken);
        assertThat(validatedClaims.username()).isEqualTo(expectedUsername);
        var zoneId = creationParams.zoneId();
        var timeAmount = this.jbstProperties.getSecurityJwtConfigs().getJwtTokensConfigs().getRefreshToken().getExpiration();
        var expiration = convertLocalDateTime(
                LocalDateTime.now(zoneId).plus(timeAmount.getAmount(), timeAmount.getUnit()),
                zoneId
        );
        assertThat(validatedClaims.expirationDate()).isBeforeOrEqualTo(expiration);
    }

    @ParameterizedTest
    @MethodSource("createJwtTokenTest")
    void createJwtTokenTest(TimeAmount timeAmount) {
        // Arrange
        var creationParams = new JwtTokenCreationParams(Username.hardcoded(), getSimpleGrantedAuthorities("user"), randomZoneId());

        // Act
        var jwtToken = this.componentUnderTest.createJwtToken(creationParams, timeAmount);

        // Assert
        var validatedClaims = this.componentUnderTest.validate(new JwtAccessToken(jwtToken));
        assertThat(validatedClaims.username()).isEqualTo(Username.hardcoded());
        var zoneId = nonNull(creationParams.zoneId()) ? creationParams.zoneId() : ZoneId.systemDefault();
        var expiration = convertLocalDateTime(
                LocalDateTime.now(zoneId).plus(timeAmount.getAmount(), timeAmount.getUnit()),
                zoneId
        );
        assertThat(validatedClaims.expirationDate()).isBeforeOrEqualTo(expiration);
    }

    @ParameterizedTest
    @MethodSource("validateTest")
    void validateTest(String jwtToken, boolean expected) {
        // Act
        var accessValidatedClaims = this.componentUnderTest.validate(new JwtAccessToken(jwtToken));
        var refreshValidatedClaims = this.componentUnderTest.validate(new JwtRefreshToken(jwtToken));

        // Assert
        assertThat(accessValidatedClaims.valid()).isEqualTo(expected);
        assertThat(accessValidatedClaims.jwtToken()).isEqualTo(jwtToken);
        assertThat(accessValidatedClaims.isAccess()).isTrue();
        assertThat(accessValidatedClaims.isRefresh()).isFalse();
        assertThat(accessValidatedClaims.authorities()).isEmpty();

        assertThat(refreshValidatedClaims.valid()).isEqualTo(expected);
        assertThat(refreshValidatedClaims.jwtToken()).isEqualTo(jwtToken);
        assertThat(refreshValidatedClaims.isAccess()).isFalse();
        assertThat(refreshValidatedClaims.isRefresh()).isTrue();
        assertThat(refreshValidatedClaims.authorities()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("isExpiredTest")
    void isExpiredTest(String jwtToken, boolean expected) {
        // Act
        var validatedClaims = this.componentUnderTest.validate(new JwtAccessToken(jwtToken));

        // Assert
        assertThat(validatedClaims.isExpired()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getUsernameByClaimsTest")
    void getUsernameByClaimsTest(String jwtToken, Username expectedUsername) {
        // Act
        var validatedClaims = this.componentUnderTest.validate(new JwtAccessToken(jwtToken));

        // Assert
        assertThat(validatedClaims.username()).isEqualTo(expectedUsername);
        assertThat(validatedClaims.authorities()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("versionsTests")
    void getClaimsTest(String jwtToken, String expectedIssuedAt, String expectedExpiration, List<SimpleGrantedAuthority> authorities) throws ParseException {
        // Arrange
        var sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT_PATTERN);
        sdf.setTimeZone(getTimeZone(UKRAINE));

        // Act
        var validatedClaims = this.componentUnderTest.validate(new JwtAccessToken(jwtToken));

        // Assert
        assertThat(validatedClaims.issuedAt()).isEqualTo(sdf.parse(expectedIssuedAt));
        assertThat(validatedClaims.expirationDate()).isEqualTo(sdf.parse(expectedExpiration));
        assertThat(validatedClaims.authorities()).isEqualTo(authorities);
    }

}
