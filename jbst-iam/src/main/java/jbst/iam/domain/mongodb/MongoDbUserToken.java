package jbst.iam.domain.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.time.TimeAmount;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestUserToken;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.domain.identifiers.TokenId;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static jbst.foundation.utilities.random.RandomUtility.randomStringLetterOrNumbersOnly;
import static jbst.foundation.utilities.time.TimestampUtility.getFutureRange;
import static jbst.foundation.utilities.time.TimestampUtility.getPastRange;

// Lombok
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@EqualsAndHashCode
@ToString
// Mongodb
@Document(collection = MongoDbUserToken.MONGO_TABLE_NAME)
public class MongoDbUserToken {
    public static final String MONGO_TABLE_NAME = "jbst_users_tokens";

    @Id
    private String id;
    private Email email;
    private Username username;
    private String value;
    private UserTokenType type;
    private long expiryTimestamp;
    private boolean used;

    public MongoDbUserToken(
            @NotNull Email email,
            @NotNull Username username,
            @NotNull String value,
            @NotNull UserTokenType type,
            long expiryTimestamp,
            boolean used
    ) {
        this.email = email;
        this.username = username;
        this.value = value;
        this.type = type;
        this.expiryTimestamp = expiryTimestamp;
        this.used = used;
    }

    public MongoDbUserToken(RequestUserToken request) {
        this(
                request.email(),
                request.username(),
                randomStringLetterOrNumbersOnly(255),
                request.type(),
                getFutureRange(new TimeAmount(24, ChronoUnit.HOURS)).to(),
                false
        );
    }

    public MongoDbUserToken(UserToken token) {
        this(
                token.email(),
                token.username(),
                token.value(),
                token.type(),
                token.expiryTimestamp(),
                token.used()
        );
        this.id = token.id().value();
    }

    public static MongoDbUserToken random(
            Username username,
            UserTokenType type,
            long expiryTimestamp,
            boolean used
    ) {
        return new MongoDbUserToken(
                new Email(username.value() + "@gmail.com"),
                username,
                randomStringLetterOrNumbersOnly(36),
                type,
                expiryTimestamp,
                used
        );
    }

    public static List<MongoDbUserToken> dummies1() {
        var token1 = MongoDbUserToken.random(
                Username.of("username1"),
                UserTokenType.EMAIL_CONFIRMATION,
                getFutureRange(new TimeAmount(1, ChronoUnit.DAYS)).to(),
                false
        );
        var token2 = MongoDbUserToken.random(
                Username.of("username2"),
                UserTokenType.PASSWORD_RESET,
                getFutureRange(new TimeAmount(1, ChronoUnit.DAYS)).to(),
                false
        );
        var token3 = MongoDbUserToken.random(
                Username.of("username3"),
                UserTokenType.EMAIL_CONFIRMATION,
                getPastRange(new TimeAmount(1, ChronoUnit.DAYS)).from(),
                false
        );
        var token4 = MongoDbUserToken.random(
                Username.of("username4"),
                UserTokenType.PASSWORD_RESET,
                getPastRange(new TimeAmount(1, ChronoUnit.DAYS)).from(),
                false
        );
        var token5 = MongoDbUserToken.random(
                Username.of("username5"),
                UserTokenType.EMAIL_CONFIRMATION,
                getPastRange(new TimeAmount(1, ChronoUnit.DAYS)).from(),
                true
        );
        var token6 = MongoDbUserToken.random(
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
                this.username,
                this.value,
                this.type,
                this.expiryTimestamp,
                this.used
        );
    }
}
