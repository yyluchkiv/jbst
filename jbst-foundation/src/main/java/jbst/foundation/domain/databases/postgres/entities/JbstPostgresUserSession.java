package jbst.foundation.domain.databases.postgres.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.converters.JbstPostgresConverters;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.databases.postgres.superclasses.JbstPostgresAbstractPersistable1;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.dto.responses.JbstResponseUserSession2;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
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
@Table(name = JbstPostgresUserSession.PG_TABLE_NAME)
public class JbstPostgresUserSession extends JbstPostgresAbstractPersistable1 {
    public static final String PG_TABLE_NAME = "jbst_users_sessions";

    @Convert(converter = JbstPostgresConverters.UsernameConverter.class)
    @Column(nullable = false)
    private Username username;

    @Convert(converter = JbstPostgresConverters.JwtAccessTokenConverter.class)
    @Column(name = "access_token", length = 4096, nullable = false)
    private JbstJwtAccessToken accessToken;

    @Convert(converter = JbstPostgresConverters.JwtRefreshTokenConverter.class)
    @Column(name = "refresh_token", length = 4096, nullable = false)
    private JbstJwtRefreshToken refreshToken;

    @Convert(converter = JbstPostgresConverters.UserRequestMetadataConverter.class)
    @Column(length = 65535, nullable = false)
    private JbstUserRequestMetadata metadata;

    @Column(name = "metadata_renew_cron", nullable = false)
    private boolean metadataRenewCron;

    @Column(name = "metadata_renew_manually", nullable = false)
    private boolean metadataRenewManually;

    public JbstPostgresUserSession(JbstUserSession session) {
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

    public static JbstPostgresUserSession random(String owner) {
        return new JbstPostgresUserSession(JbstUserSession.random(owner));
    }

    public static JbstPostgresUserSession random(Username owner, String accessToken) {
        return new JbstPostgresUserSession(JbstUserSession.random(owner, accessToken));
    }

    public static JbstPostgresUserSession random(String owner, String accessToken, String refreshToken) {
        return new JbstPostgresUserSession(JbstUserSession.random(owner, accessToken, refreshToken));
    }

    public static List<JbstPostgresUserSession> dummies1() {
        UnaryOperator<JbstPostgresUserSession> removeId = session -> {
            session.id = null;
            return session;
        };
        var session1 = JbstPostgresUserSession.random(Username.hardcoded().value(), "awt1", "rwt1");
        var session2 = JbstPostgresUserSession.random(Username.hardcoded().value(), "awt2", "rwt2");
        var session3 = JbstPostgresUserSession.random(Username.hardcoded().value(), "awt3", "rwt3");
        var session4 = JbstPostgresUserSession.random(Username.hardcoded().value(), "awt4", "rwt4");
        var session5 = JbstPostgresUserSession.random("user1", "atoken11", "rtoken11");
        var session6 = JbstPostgresUserSession.random("user1", "atoken12", "rtoken12");
        var session7 = JbstPostgresUserSession.random("sa", "atoken", "rtoken");
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

    public static List<JbstPostgresUserSession> dummies2() {
        UnaryOperator<JbstPostgresUserSession> removeId = session -> {
            session.id = null;
            return session;
        };
        var session1 = JbstPostgresUserSession.random(Username.hardcoded(), "token1");
        var session2 = JbstPostgresUserSession.random(Username.hardcoded(), "token2");
        var session3 = JbstPostgresUserSession.random(Username.hardcoded(), "token3");
        var session4 = JbstPostgresUserSession.random(Username.of("admin"), "token4");
        return List.of(
                removeId.apply(session1),
                removeId.apply(session2),
                removeId.apply(session3),
                removeId.apply(session4)
        );
    }

    @JsonIgnore
    @Transient
    public JbstUserSessionId userSessionId() {
        return new JbstUserSessionId(this.id);
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
