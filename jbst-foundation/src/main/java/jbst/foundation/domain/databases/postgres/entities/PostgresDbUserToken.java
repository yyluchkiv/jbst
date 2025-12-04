package jbst.foundation.domain.databases.postgres.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.converters.JbstPostgresConverters;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.databases.postgres.superclasses.PostgresDbAbstractPersistable0;
import jbst.foundation.domain.dto.requests.JbstRequestUserToken;
import jbst.foundation.domain.enums.JbstUserTokenType;
import jbst.foundation.domain.ids.JbstTokenId;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Transient;

import java.time.Duration;
import java.util.List;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.random.JbstRandom.randomStringLetterOrNumbersOnly;
import static jbst.foundation.domain.time.JbstTime.getFutureTimestamp;
import static jbst.foundation.domain.time.JbstTime.getPastTimestamp;

@SuppressWarnings("JpaDataSourceORMInspection")
// Lombok
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
// JPA
@Entity
@Table(name = PostgresDbUserToken.PG_TABLE_NAME)
public class PostgresDbUserToken extends PostgresDbAbstractPersistable0 {
    public static final String PG_TABLE_NAME = "jbst_users_tokens";

    @Convert(converter = JbstPostgresConverters.EmailConverter.class)
    @Column(nullable = false, updatable = false)
    private Email email;

    @Column(nullable = false, updatable = false)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private JbstUserTokenType type;

    @Column(name = "expiry_timestamp", nullable = false, updatable = false)
    private long expiryTimestamp;

    @Column(nullable = false)
    private boolean used;

    public PostgresDbUserToken(
            @NotNull Email email,
            @NotNull String value,
            @NotNull JbstUserTokenType type,
            long expiryTimestamp,
            boolean used
    ) {
        this.email = email;
        this.value = value;
        this.type = type;
        this.expiryTimestamp = expiryTimestamp;
        this.used = used;
    }

    public PostgresDbUserToken(JbstRequestUserToken request) {
        this(
                request.email(),
                randomStringLetterOrNumbersOnly(255),
                request.type(),
                request.type().getExpiryTimestamp(),
                false
        );
    }

    public PostgresDbUserToken(JbstUserToken token) {
        this(
                token.email(),
                token.value(),
                token.type(),
                token.expiryTimestamp(),
                token.used()
        );
        this.id = nonNull(token.id()) ? token.id().value() : null;
    }

    public static PostgresDbUserToken random(
            Username username,
            JbstUserTokenType type,
            long expiryTimestamp,
            boolean used
    ) {
        return new PostgresDbUserToken(
                new Email(username.value() + "@gmail.com"),
                randomStringLetterOrNumbersOnly(36),
                type,
                expiryTimestamp,
                used
        );
    }

    public static List<PostgresDbUserToken> dummies1() {
        var token1 = PostgresDbUserToken.random(
                Username.of("username1"),
                JbstUserTokenType.EMAIL_CONFIRMATION,
                getFutureTimestamp(Duration.ofDays(1L)).value(),
                false
        );
        var token2 = PostgresDbUserToken.random(
                Username.of("username2"),
                JbstUserTokenType.PASSWORD_RESET,
                getFutureTimestamp(Duration.ofDays(1L)).value(),
                false
        );
        var token3 = PostgresDbUserToken.random(
                Username.of("username3"),
                JbstUserTokenType.EMAIL_CONFIRMATION,
                getPastTimestamp(Duration.ofDays(1L)).value(),
                false
        );
        var token4 = PostgresDbUserToken.random(
                Username.of("username4"),
                JbstUserTokenType.PASSWORD_RESET,
                getPastTimestamp(Duration.ofDays(1L)).value(),
                false
        );
        var token5 = PostgresDbUserToken.random(
                Username.of("username5"),
                JbstUserTokenType.EMAIL_CONFIRMATION,
                getPastTimestamp(Duration.ofDays(1L)).value(),
                true
        );
        var token6 = PostgresDbUserToken.random(
                Username.of("username6"),
                JbstUserTokenType.EMAIL_CONFIRMATION,
                getFutureTimestamp(Duration.ofDays(1L)).value(),
                true
        );
        return List.of(
                token1,
                token2,
                token3,
                token4,
                token5,
                token6
        );
    }

    @JsonIgnore
    @Transient
    public JbstTokenId tokenId() {
        return new JbstTokenId(this.id);
    }

    @JsonIgnore
    @Transient
    public JbstUserToken asUserToken() {
        return new JbstUserToken(
                new JbstTokenId(this.id),
                this.email,
                this.value,
                this.type,
                this.expiryTimestamp,
                this.used
        );
    }
}
