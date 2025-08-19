package jbst.iam.domain.postgres.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.converters.postgres.PostgresEmailConverter;
import jbst.foundation.domain.time.TimeAmount;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestUserToken;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.domain.identifiers.TokenId;
import jbst.iam.domain.postgres.superclasses.PostgresDbAbstractPersistable0;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Transient;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.random.RandomUtility.randomStringLetterOrNumbersOnly;
import static jbst.foundation.utilities.time.TimestampUtility.getFutureRange;
import static jbst.foundation.utilities.time.TimestampUtility.getPastRange;

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

    @Convert(converter = PostgresEmailConverter.class)
    @Column(nullable = false, updatable = false)
    private Email email;

    @Column(nullable = false, updatable = false)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private UserTokenType type;

    @Column(name = "expiry_timestamp", nullable = false, updatable = false)
    private long expiryTimestamp;

    @Column(nullable = false)
    private boolean used;

    public PostgresDbUserToken(
            @NotNull Email email,
            @NotNull String value,
            @NotNull UserTokenType type,
            long expiryTimestamp,
            boolean used
    ) {
        this.email = email;
        this.value = value;
        this.type = type;
        this.expiryTimestamp = expiryTimestamp;
        this.used = used;
    }

    public PostgresDbUserToken(RequestUserToken request) {
        this(
                request.email(),
                randomStringLetterOrNumbersOnly(255),
                request.type(),
                request.type().getExpiryTimestamp(),
                false
        );
    }

    public PostgresDbUserToken(UserToken token) {
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
            UserTokenType type,
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
                UserTokenType.EMAIL_CONFIRMATION,
                getFutureRange(new TimeAmount(1, ChronoUnit.DAYS)).to(),
                false
        );
        var token2 = PostgresDbUserToken.random(
                Username.of("username2"),
                UserTokenType.PASSWORD_RESET,
                getFutureRange(new TimeAmount(1, ChronoUnit.DAYS)).to(),
                false
        );
        var token3 = PostgresDbUserToken.random(
                Username.of("username3"),
                UserTokenType.EMAIL_CONFIRMATION,
                getPastRange(new TimeAmount(1, ChronoUnit.DAYS)).from(),
                false
        );
        var token4 = PostgresDbUserToken.random(
                Username.of("username4"),
                UserTokenType.PASSWORD_RESET,
                getPastRange(new TimeAmount(1, ChronoUnit.DAYS)).from(),
                false
        );
        var token5 = PostgresDbUserToken.random(
                Username.of("username5"),
                UserTokenType.EMAIL_CONFIRMATION,
                getPastRange(new TimeAmount(1, ChronoUnit.DAYS)).from(),
                true
        );
        var token6 = PostgresDbUserToken.random(
                Username.of("username6"),
                UserTokenType.EMAIL_CONFIRMATION,
                getFutureRange(new TimeAmount(1, ChronoUnit.DAYS)).to(),
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
    public TokenId tokenId() {
        return new TokenId(this.id);
    }

    @JsonIgnore
    @Transient
    public UserToken asUserToken() {
        return new UserToken(
                new TokenId(this.id),
                this.email,
                this.value,
                this.type,
                this.expiryTimestamp,
                this.used
        );
    }
}
