package jbst.foundation.domain.databases.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseUserSession2;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.domain.ids.UserSessionId;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.databases.JbstUserSession.ofNotPersisted;
import static jbst.foundation.domain.databases.JbstUserSession.ofPersisted;
import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
// Mongodb
@Document(collection = MongoDbUserSession.MONGO_TABLE_NAME)
public class MongoDbUserSession {
    public static final String MONGO_TABLE_NAME = "jbst_users_sessions";

    @Id
    private String id;
    private long createdAt;
    private long updatedAt;
    private Username username;
    private JwtAccessToken accessToken;
    private JwtRefreshToken refreshToken;
    private UserRequestMetadata metadata;
    private boolean metadataRenewCron;
    private boolean metadataRenewManually;

    public MongoDbUserSession(JbstUserSession session) {
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

    public static MongoDbUserSession random(String owner) {
        return new MongoDbUserSession(JbstUserSession.random(owner));
    }

    public static MongoDbUserSession random(Username owner, String accessToken) {
        return new MongoDbUserSession(JbstUserSession.random(owner, accessToken));
    }

    public static MongoDbUserSession random(String owner, String accessToken, String refreshToken) {
        return new MongoDbUserSession(JbstUserSession.random(owner, accessToken, refreshToken));
    }

    public static List<MongoDbUserSession> dummies1() {
        var session1 = MongoDbUserSession.random(Username.hardcoded().value(), "awt1", "rwt1");
        var session2 = MongoDbUserSession.random(Username.hardcoded().value(), "awt2", "rwt2");
        var session3 = MongoDbUserSession.random(Username.hardcoded().value(), "awt3", "rwt3");
        var session4 = MongoDbUserSession.random(Username.hardcoded().value(), "awt4", "rwt4");
        var session5 = MongoDbUserSession.random("user1", "atoken11", "rtoken11");
        var session6 = MongoDbUserSession.random("user1", "atoken12", "rtoken12");
        var session7 = MongoDbUserSession.random("sa", "atoken", "rtoken");
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

    public static List<MongoDbUserSession> dummies2() {
        var session1 = MongoDbUserSession.random(Username.hardcoded(), "token1");
        var session2 = MongoDbUserSession.random(Username.hardcoded(), "token2");
        var session3 = MongoDbUserSession.random(Username.hardcoded(), "token3");
        var session4 = MongoDbUserSession.random(Username.of("admin"), "token4");
        return List.of(session1, session2, session3, session4);
    }

    @JsonIgnore
    @Transient
    public UserSessionId userSessionId() {
        return new UserSessionId(this.id);
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
    public ResponseUserSession2 responseUserSession2(RequestAccessToken requestAccessToken) {
        return ResponseUserSession2.of(
                this.userSessionId(),
                this.updatedAt,
                this.username,
                requestAccessToken,
                this.accessToken,
                this.metadata
        );
    }
}
