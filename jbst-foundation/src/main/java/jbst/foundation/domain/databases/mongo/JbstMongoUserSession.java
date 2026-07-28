package jbst.foundation.domain.databases.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.dto.responses.JbstResponseUserSession2;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.databases.JbstUserSession.ofNotPersisted;
import static jbst.foundation.domain.databases.JbstUserSession.ofPersisted;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;

// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
// Mongodb
@Document(collection = JbstMongoUserSession.MONGO_TABLE_NAME)
public class JbstMongoUserSession {
    public static final String MONGO_TABLE_NAME = "jbst_users_sessions";

    @Id
    private String id;
    private long createdAt;
    private long updatedAt;
    private Username username;
    private JbstJwtAccessToken accessToken;
    private JbstJwtRefreshToken refreshToken;
    private JbstUserRequestMetadata metadata;
    private boolean metadataRenewCron;
    private boolean metadataRenewManually;

    public JbstMongoUserSession(JbstUserSession session) {
        if (session.persisted()) {
            this.id = session.id().value();
        }
        var currentTimestamp = getCurrentTimestamp();
        this.createdAt = currentTimestamp;
        this.updatedAt = currentTimestamp;
        this.username = session.username();
        this.accessToken = session.accessToken();
        this.refreshToken = session.refreshToken();
        this.metadata = session.metadata();
        this.metadataRenewCron = false;
        this.metadataRenewManually = false;
    }

    public static JbstMongoUserSession random(String owner) {
        return new JbstMongoUserSession(JbstUserSession.random(owner));
    }

    public static JbstMongoUserSession random(Username owner, String accessToken) {
        return new JbstMongoUserSession(JbstUserSession.random(owner, accessToken));
    }

    public static JbstMongoUserSession random(String owner, String accessToken, String refreshToken) {
        return new JbstMongoUserSession(JbstUserSession.random(owner, accessToken, refreshToken));
    }

    public static List<JbstMongoUserSession> dummies1() {
        var session1 = JbstMongoUserSession.random(Username.fixed().value(), "awt1", "rwt1");
        var session2 = JbstMongoUserSession.random(Username.fixed().value(), "awt2", "rwt2");
        var session3 = JbstMongoUserSession.random(Username.fixed().value(), "awt3", "rwt3");
        var session4 = JbstMongoUserSession.random(Username.fixed().value(), "awt4", "rwt4");
        var session5 = JbstMongoUserSession.random("user1", "atoken11", "rtoken11");
        var session6 = JbstMongoUserSession.random("user1", "atoken12", "rtoken12");
        var session7 = JbstMongoUserSession.random("sa", "atoken", "rtoken");
        return List.of(
                session1,
                session2,
                session3,
                session4,
                session5,
                session6,
                session7
        );
    }

    public static List<JbstMongoUserSession> dummies2() {
        var session1 = JbstMongoUserSession.random(Username.fixed(), "token1");
        var session2 = JbstMongoUserSession.random(Username.fixed(), "token2");
        var session3 = JbstMongoUserSession.random(Username.fixed(), "token3");
        var session4 = JbstMongoUserSession.random(Username.of("admin"), "token4");
        return List.of(session1, session2, session3, session4);
    }

    @JsonIgnore
    @Transient
    public JbstUserSessionId userSessionId() {
        return new JbstUserSessionId(this.id);
    }

    @JsonIgnore
    @Transient
    public JbstUserSession userSession() {
        if (isNull(this.id)) {
            return ofNotPersisted(this.username, this.accessToken, this.refreshToken, this.metadata);
        } else {
            return ofPersisted(
                    this.userSessionId(),
                    this.createdAt,
                    this.updatedAt,
                    this.username,
                    this.accessToken,
                    this.refreshToken,
                    this.metadata,
                    this.metadataRenewCron,
                    this.metadataRenewManually
            );
        }
    }

    @JsonIgnore
    @Transient
    public JbstResponseUserSession2 responseUserSession2(JbstRequestAccessToken requestAccessToken) {
        return JbstResponseUserSession2.of(
                this.userSessionId(),
                this.updatedAt,
                this.username,
                requestAccessToken,
                this.accessToken,
                this.metadata
        );
    }
}
