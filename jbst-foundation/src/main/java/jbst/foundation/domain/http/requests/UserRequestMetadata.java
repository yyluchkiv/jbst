package jbst.foundation.domain.http.requests;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.tests.constants.TestsFlagsConstants;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.domain.tuples.Tuple3;
import jbst.foundation.domain.tuples.TupleExceptionDetails;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.pleaseWait;
import static jbst.foundation.domain.random.JbstRandom.randomBoolean;
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
public class UserRequestMetadata {
    private final Status status;
    private final GeoLocation geoLocation;
    private final UserAgentDetails userAgentDetails;

    public static UserRequestMetadata processing(
            IPAddress ipAddress
    ) {
        return new UserRequestMetadata(
                Status.STARTED,
                GeoLocation.processing(ipAddress),
                UserAgentDetails.processing()
        );
    }

    public static UserRequestMetadata processed(
            GeoLocation geoLocation,
            UserAgentDetails userAgentDetails
    ) {
        return new UserRequestMetadata(
                Status.COMPLETED,
                geoLocation,
                userAgentDetails
        );
    }

    public static UserRequestMetadata valid() {
        return UserRequestMetadata.processed(
                GeoLocation.valid(),
                UserAgentDetails.valid()
        );
    }

    public static UserRequestMetadata invalid() {
        return UserRequestMetadata.processed(
                GeoLocation.invalid(),
                UserAgentDetails.invalid()
        );
    }

    public static UserRequestMetadata random() {
        return randomBoolean() ? valid() : invalid();
    }

    public static UserRequestMetadata testData() {
        return UserRequestMetadata.processed(
                GeoLocation.testData(),
                UserAgentDetails.testData()
        );
    }

    public Tuple3<String, String, String> getWhereTuple3() {
        if (this.status.isCompleted()) {
            return new Tuple3<>(this.geoLocation.getIpAddr(), this.geoLocation.getCountryFlag(), this.geoLocation.getWhere());
        } else {
            return new Tuple3<>(this.geoLocation.getIpAddr(), TestsFlagsConstants.UNKNOWN, pleaseWait("Processing"));
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
