package jbst.foundation.domain.databases.postgres.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.converters.PostgresConverters;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.databases.postgres.superclasses.PostgresDbAbstractPersistable1;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseUserSession2;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.domain.ids.UserSessionId;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import lombok.*;

import java.util.List;
import java.util.function.UnaryOperator;

import static jbst.foundation.domain.databases.JbstUserSession.ofNotPersisted;
import static jbst.foundation.domain.databases.JbstUserSession.ofPersisted;

@SuppressWarnings("JpaDataSourceORMInspection")
// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
// JPA
@Entity
@Table(name = PostgresDbUserSession.PG_TABLE_NAME)
public class PostgresDbUserSession extends PostgresDbAbstractPersistable1 {
    public static final String PG_TABLE_NAME = "jbst_users_sessions";

    @Convert(converter = PostgresConverters.UsernameConverter.class)
    @Column(nullable = false)
    private Username username;

    @Convert(converter = PostgresConverters.JwtAccessTokenConverter.class)
    @Column(name = "access_token", length = 4096, nullable = false)
    private JwtAccessToken accessToken;

    @Convert(converter = PostgresConverters.JwtRefreshTokenConverter.class)
    @Column(name = "refresh_token", length = 4096, nullable = false)
    private JwtRefreshToken refreshToken;

    @Convert(converter = PostgresConverters.UserRequestMetadataConverter.class)
    @Column(length = 65535, nullable = false)
    private UserRequestMetadata metadata;

    @Column(name = "metadata_renew_cron", nullable = false)
    private boolean metadataRenewCron;

    @Column(name = "metadata_renew_manually", nullable = false)
    private boolean metadataRenewManually;

    public PostgresDbUserSession(JbstUserSession session) {
        if (session.persisted()) {
            this.id = session.id().value();
        }
        this.username = session.username();
        this.accessToken = session.accessToken();
        this.refreshToken = session.refreshToken();
        this.metadata = session.metadata();
        this.metadataRenewCron = false;
        this.metadataRenewManually = false;
    }

    public static PostgresDbUserSession random(String owner) {
        return new PostgresDbUserSession(JbstUserSession.random(owner));
    }

    public static PostgresDbUserSession random(Username owner, String accessToken) {
        return new PostgresDbUserSession(JbstUserSession.random(owner, accessToken));
    }

    public static PostgresDbUserSession random(String owner, String accessToken, String refreshToken) {
        return new PostgresDbUserSession(JbstUserSession.random(owner, accessToken, refreshToken));
    }

    public static List<PostgresDbUserSession> dummies1() {
        UnaryOperator<PostgresDbUserSession> removeId = session -> {
            session.id = null;
            return session;
        };
        var session1 = PostgresDbUserSession.random(Username.hardcoded().value(), "awt1", "rwt1");
        var session2 = PostgresDbUserSession.random(Username.hardcoded().value(), "awt2", "rwt2");
        var session3 = PostgresDbUserSession.random(Username.hardcoded().value(), "awt3", "rwt3");
        var session4 = PostgresDbUserSession.random(Username.hardcoded().value(), "awt4", "rwt4");
        var session5 = PostgresDbUserSession.random("user1", "atoken11", "rtoken11");
        var session6 = PostgresDbUserSession.random("user1", "atoken12", "rtoken12");
        var session7 = PostgresDbUserSession.random("sa", "atoken", "rtoken");
        return List.of(
                removeId.apply(session1),
                removeId.apply(session2),
                removeId.apply(session3),
                removeId.apply(session4),
                removeId.apply(session5),
                removeId.apply(session6),
                removeId.apply(session7)
        );
    }

    public static List<PostgresDbUserSession> dummies2() {
        UnaryOperator<PostgresDbUserSession> removeId = session -> {
            session.id = null;
            return session;
        };
        var session1 = PostgresDbUserSession.random(Username.hardcoded(), "token1");
        var session2 = PostgresDbUserSession.random(Username.hardcoded(), "token2");
        var session3 = PostgresDbUserSession.random(Username.hardcoded(), "token3");
        var session4 = PostgresDbUserSession.random(Username.of("admin"), "token4");
        return List.of(
                removeId.apply(session1),
                removeId.apply(session2),
                removeId.apply(session3),
                removeId.apply(session4)
        );
    }

    @JsonIgnore
    @Transient
    public UserSessionId userSessionId() {
        return new UserSessionId(this.id);
    }

    @JsonIgnore
    @Transient
    public JbstUserSession userSession() {
        if (this.isNew()) {
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
