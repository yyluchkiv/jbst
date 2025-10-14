package jbst.iam.domain.dto.responses;

import jbst.iam.domain.identifiers.UserSessionId;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.iam.domain.jwt.RequestAccessToken;
import org.junit.jupiter.api.Test;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentDetails;
import jbst.foundation.domain.http.requests.UserRequestMetadata;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static jbst.foundation.domain.tests.constants.TestsFlagsConstants.UK;
import static jbst.foundation.domain.tests.constants.TestsFlagsConstants.USA;
import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

class ResponseUserSessionsTableTest {

    @Test
    void noSessionsConstructorTest() {
        // Act
        var actual = ResponseUserSessionsTable.of(new ArrayList<>());

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.sessions()).isEmpty();
        assertThat(actual.anyPresent()).isFalse();
        assertThat(actual.anyProblem()).isFalse();
    }

    @Test
    void constructorTest() {
        // Arrange
        var username = Username.random();
        var responseUserSession21 = ResponseUserSession2.of(
                UserSessionId.random(),
                getCurrentTimestamp(),
                username,
                new RequestAccessToken(randomString()),
                new JwtAccessToken("token1"),
                UserRequestMetadata.processed(
                        GeoLocation.processed(new IPAddress("2.2.2.2"), "UK", "UK", UK, "London"),
                        UserAgentDetails.random()
                )
        );
        var responseUserSession22 = ResponseUserSession2.of(
                UserSessionId.random(),
                getCurrentTimestamp(),
                username,
                new RequestAccessToken("token2"),
                new JwtAccessToken("token2"),
                UserRequestMetadata.processed(
                        GeoLocation.processed(new IPAddress("3.3.3.3"), "USA", "US", USA, "New York"),
                        UserAgentDetails.valid()
                )
        );
        var responseUserSession23 = ResponseUserSession2.of(
                UserSessionId.random(),
                getCurrentTimestamp(),
                username,
                new RequestAccessToken(randomString()),
                new JwtAccessToken("token3"),
                UserRequestMetadata.processed(
                        GeoLocation.processed(new IPAddress("3.3.3.3"), "UK", "UK", UK, "Liverpool"),
                        UserAgentDetails.invalid()
                )
        );

        // Act
        var actual = ResponseUserSessionsTable.of(new ArrayList<>(List.of(responseUserSession21, responseUserSession22, responseUserSession23)));

        // Assert
        assertThat(actual.sessions()).hasSize(3);
        assertThat(actual.sessions().get(0).current()).isTrue();
        assertThat(actual.sessions().get(0).where()).isEqualTo("🇺🇸 USA, New York");
        assertThat(actual.sessions().get(1).current()).isFalse();
        assertThat(actual.sessions().get(1).where()).isEqualTo("🇬🇧 UK, Liverpool");
        assertThat(actual.sessions().get(2).current()).isFalse();
        assertThat(actual.sessions().get(2).where()).isEqualTo("🇬🇧 UK, London");
        assertThat(actual.anyPresent()).isTrue();
        assertThat(actual.anyProblem()).isTrue();
    }
}
