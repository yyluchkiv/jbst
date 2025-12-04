package jbst.foundation.domain.dto.responses;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.time.JbstTimeAmount;
import jbst.foundation.domain.time.JbstTimeago;
import jbst.foundation.domain.tuples.TupleExceptionDetails;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;

public record ResponseUserSession2(
        JbstUserSessionId id,
        Username who,
        boolean current,
        String activity,
        JbstTimeago when,
        TupleExceptionDetails exception,
        String country,
        String ipAddr,
        String countryFlag,
        String where,
        String browser,
        String what
) {

    public static final Comparator<ResponseUserSession2> USERS_SESSIONS = comparing(ResponseUserSession2::current).reversed()
            .thenComparing(ResponseUserSession2::where);

    public static final Comparator<ResponseUserSession2> ACTIVE_SESSIONS_AS_SUPERADMIN = comparing(ResponseUserSession2::current).reversed()
            .thenComparing((ResponseUserSession2 session) -> session.who().value())
            .thenComparing(ResponseUserSession2::where);

    public static final Comparator<ResponseUserSession2> INACTIVE_SESSIONS_AS_SUPERADMIN = comparing((ResponseUserSession2 session) -> session.who().value())
            .thenComparing(ResponseUserSession2::where);

    public static ResponseUserSession2 of(
            JbstUserSessionId id,
            long updatedAt,
            Username username,
            RequestAccessToken requestAccessToken,
            JbstJwtAccessToken accessToken,
            JbstUserRequestMetadata metadata
    ) {
        var current = requestAccessToken.value().equals(accessToken.value());
        var activity = current ? "Current session" : "—";

        var whereTuple3 = metadata.getWhereTuple3();
        var whatTuple2 = metadata.getWhatTuple2();
        var country = metadata.getGeoLocation().getCountry();

        return new ResponseUserSession2(
                id,
                username,
                current,
                activity,
                new JbstTimeago(updatedAt),
                metadata.getException(),
                country,
                whereTuple3.a(),
                whereTuple3.b(),
                whereTuple3.c(),
                whatTuple2.a(),
                whatTuple2.b()
        );
    }

    public static ResponseUserSession2 hardcodedCurrent() {
        var token = "PFRL63OtcEKKy0hb7UjE";
        return of(
                JbstUserSessionId.hardcoded(),
                getCurrentTimestamp(),
                Username.hardcoded(),
                new RequestAccessToken(token),
                new JbstJwtAccessToken(token),
                JbstUserRequestMetadata.valid()
        );
    }

    public static ResponseUserSession2 random() {
        return of(
                JbstUserSessionId.random(),
                getCurrentTimestamp() - JbstTimeAmount.random().toMillis(),
                Username.hardcoded(),
                RequestAccessToken.random(),
                JbstJwtAccessToken.random(),
                JbstUserRequestMetadata.testData()
        );
    }
}
