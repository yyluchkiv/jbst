package jbst.foundation.domain.dto.responses;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.geo.JbstGeoLocation;
import jbst.foundation.domain.base.IPAddress;
import jbst.foundation.domain.http.requests.JbstUserAgentDetails;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JwtAccessToken;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static jbst.foundation.domain.constants.JbstConstants.Flags.UK;
import static jbst.foundation.domain.constants.JbstConstants.Flags.USA;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static jbst.foundation.domain.time.TimestampUtility.getCurrentTimestamp;
import static org.assertj.core.api.Assertions.assertThat;

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
                JbstUserSessionId.random(),
                getCurrentTimestamp(),
                username,
                new RequestAccessToken(randomString()),
                new JwtAccessToken("token1"),
                JbstUserRequestMetadata.processed(
                        JbstGeoLocation.processed(new IPAddress("2.2.2.2"), "UK", "UK", UK, "London"),
                        JbstUserAgentDetails.random()
                )
        );
        var responseUserSession22 = ResponseUserSession2.of(
                JbstUserSessionId.random(),
                getCurrentTimestamp(),
                username,
                new RequestAccessToken("token2"),
                new JwtAccessToken("token2"),
                JbstUserRequestMetadata.processed(
                        JbstGeoLocation.processed(new IPAddress("3.3.3.3"), "USA", "US", USA, "New York"),
                        JbstUserAgentDetails.valid()
                )
        );
        var responseUserSession23 = ResponseUserSession2.of(
                JbstUserSessionId.random(),
                getCurrentTimestamp(),
                username,
                new RequestAccessToken(randomString()),
                new JwtAccessToken("token3"),
                JbstUserRequestMetadata.processed(
                        JbstGeoLocation.processed(new IPAddress("3.3.3.3"), "UK", "UK", UK, "Liverpool"),
                        JbstUserAgentDetails.invalid()
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
