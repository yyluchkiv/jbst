package jbst.foundation.domain.http.requests;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jbst.foundation.domain.base.IPAddress;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.JbstStatus;
import jbst.foundation.domain.geo.JbstGeoLocation;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.domain.tuples.Tuple3;
import jbst.foundation.domain.tuples.TupleExceptionDetails;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static jbst.foundation.domain.random.JbstRandom.randomBoolean;
import static jbst.foundation.domain.strings.JbstMessages.pleaseWait;
import static jbst.foundation.domain.strings.JbstStrings.hasLength;

// JSON
@JsonPropertyOrder({
        "status",
        "geoLocation",
        "userAgentDetails",
        "whereTuple3",
        "whatTuple2",
        "exception"
})
// Lombok
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class JbstUserRequestMetadata {
    private final JbstStatus status;
    private final JbstGeoLocation geoLocation;
    private final JbstUserAgentDetails userAgentDetails;

    public static JbstUserRequestMetadata processing(
            IPAddress ipAddress
    ) {
        return new JbstUserRequestMetadata(
                JbstStatus.STARTED,
                JbstGeoLocation.processing(ipAddress),
                JbstUserAgentDetails.processing()
        );
    }

    public static JbstUserRequestMetadata processed(
            JbstGeoLocation geoLocation,
            JbstUserAgentDetails userAgentDetails
    ) {
        return new JbstUserRequestMetadata(
                JbstStatus.COMPLETED,
                geoLocation,
                userAgentDetails
        );
    }

    public static JbstUserRequestMetadata valid() {
        return JbstUserRequestMetadata.processed(
                JbstGeoLocation.valid(),
                JbstUserAgentDetails.valid()
        );
    }

    public static JbstUserRequestMetadata invalid() {
        return JbstUserRequestMetadata.processed(
                JbstGeoLocation.invalid(),
                JbstUserAgentDetails.invalid()
        );
    }

    public static JbstUserRequestMetadata random() {
        return randomBoolean() ? valid() : invalid();
    }

    public static JbstUserRequestMetadata testData() {
        return JbstUserRequestMetadata.processed(
                JbstGeoLocation.testData(),
                JbstUserAgentDetails.testData()
        );
    }

    public Tuple3<String, String, String> getWhereTuple3() {
        if (this.status.isCompleted()) {
            return new Tuple3<>(this.geoLocation.getIpAddr(), this.geoLocation.getCountryFlag(), this.geoLocation.getWhere());
        } else {
            return new Tuple3<>(this.geoLocation.getIpAddr(), JbstConstants.Flags.UNKNOWN, pleaseWait("Processing"));
        }
    }

    public Tuple2<String, String> getWhatTuple2() {
        if (this.status.isCompleted()) {
            return new Tuple2<>(this.userAgentDetails.getBrowser(), this.userAgentDetails.getWhat());
        } else {
            return new Tuple2<>(this.userAgentDetails.getBrowser(), "—");
        }
    }

    public TupleExceptionDetails getException() {
        var geoExceptionDetails = this.geoLocation.getExceptionDetails();
        var userAgentExceptionDetails = this.userAgentDetails.getExceptionDetails();
        if (hasLength(geoExceptionDetails) && hasLength(userAgentExceptionDetails)) {
            return TupleExceptionDetails.exception(geoExceptionDetails + ". " + userAgentExceptionDetails);
        }
        if (hasLength(geoExceptionDetails)) {
            return TupleExceptionDetails.exception(geoExceptionDetails);
        }
        if (hasLength(userAgentExceptionDetails)) {
            return TupleExceptionDetails.exception(userAgentExceptionDetails);
        }
        return TupleExceptionDetails.ok();
    }
}
