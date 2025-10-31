package jbst.foundation.domain.databases.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestUserToken;
import jbst.foundation.domain.enums.JbstUserTokenType;
import jbst.foundation.domain.ids.TokenId;
import jbst.foundation.domain.time.TimeAmount;
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
    private String value;
    private JbstUserTokenType type;
    private long expiryTimestamp;
    private boolean used;

    public MongoDbUserToken(
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

    public MongoDbUserToken(RequestUserToken request) {
        this(
                request.email(),
                randomStringLetterOrNumbersOnly(255),
                request.type(),
                request.type().getExpiryTimestamp(),
                false
        );
    }

    public MongoDbUserToken(JbstUserToken token) {
        this(
                token.email(),
                token.value(),
                token.type(),
                token.expiryTimestamp(),
                token.used()
        );
        this.id = token.id().value();
    }

    public static MongoDbUserToken random(
            Username username,
            JbstUserTokenType type,
            long expiryTimestamp,
            boolean used
    ) {
        return new MongoDbUserToken(
                new Email(username.value() + "@gmail.com"),
                randomStringLetterOrNumbersOnly(36),
                type,
                expiryTimestamp,
                used
        );
    }

    public static List<MongoDbUserToken> dummies1() {
        var token1 = MongoDbUserToken.random(
                Username.of("username1"),
                JbstUserTokenType.EMAIL_CONFIRMATION,
                getFutureRange(new TimeAmount(1, ChronoUnit.DAYS)).to(),
                false
        );
        var token2 = MongoDbUserToken.random(
                Username.of("username2"),
                JbstUserTokenType.PASSWORD_RESET,
                getFutureRange(new TimeAmount(1, ChronoUnit.DAYS)).to(),
                false
        );
        var token3 = MongoDbUserToken.random(
                Username.of("username3"),
                JbstUserTokenType.EMAIL_CONFIRMATION,
                getPastRange(new TimeAmount(1, ChronoUnit.DAYS)).from(),
                false
        );
        var token4 = MongoDbUserToken.random(
                Username.of("username4"),
                JbstUserTokenType.PASSWORD_RESET,
                getPastRange(new TimeAmount(1, ChronoUnit.DAYS)).from(),
                false
        );
        var token5 = MongoDbUserToken.random(
                Username.of("username5"),
                JbstUserTokenType.EMAIL_CONFIRMATION,
                getPastRange(new TimeAmount(1, ChronoUnit.DAYS)).from(),
                true
        );
        var token6 = MongoDbUserToken.random(
                Username.of("username6"),
                JbstUserTokenType.EMAIL_CONFIRMATION,
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
    public JbstUserToken asUserToken() {
        return new JbstUserToken(
                new TokenId(this.id),
                this.email,
                this.value,
                this.type,
                this.expiryTimestamp,
                this.used
        );
    }
}
