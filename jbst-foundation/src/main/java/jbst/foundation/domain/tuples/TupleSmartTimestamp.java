package jbst.foundation.domain.tuples;

import jbst.foundation.domain.asserts.JbstAsserts;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.ZoneId;

import static java.time.format.DateTimeFormatter.ofPattern;
import static jbst.foundation.domain.strings.JbstMessages.invalidAttribute;
import static jbst.foundation.domain.time.JbstTime.convert1;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class TupleSmartTimestamp {
    private final long timestamp;
    private final String formatted;

    public TupleSmartTimestamp(
            long timestamp,
            ZoneId zoneId,
            String dateTimePattern
    ) {
        JbstAsserts.assertNonNullOrThrow(zoneId, invalidAttribute("TupleSmartTimestamp.zoneId"));
        JbstAsserts.assertNonNullOrThrow(dateTimePattern, invalidAttribute("TupleSmartTimestamp.dateTimePattern"));
        this.timestamp = timestamp;
        this.formatted = convert1(timestamp, zoneId).format(ofPattern(dateTimePattern));
    }

    public static TupleSmartTimestamp of(
            long timestamp,
            ZoneId zoneId,
            String dateTimePattern
    ) {
        return new TupleSmartTimestamp(
                timestamp,
                zoneId,
                dateTimePattern
        );
    }
}
