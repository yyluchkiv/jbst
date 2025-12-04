package jbst.foundation.domain.geo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.base.IPAddress;
import jbst.foundation.domain.random.JbstRandom;
import jbst.foundation.domain.tuples.Tuple5;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.random.JbstRandom.randomBoolean;
import static jbst.foundation.domain.strings.JbstStrings.hasLength;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class JbstGeoLocation {
    private static final List<Tuple5<String, String, String, String, String>> TEST_DATA = List.of(
            new Tuple5<>("103.194.114.1", "Ukraine", "UA", JbstConstants.Flags.UKRAINE, "Lviv"),
            new Tuple5<>("103.194.114.2", "Ukraine", "UA", JbstConstants.Flags.UKRAINE, "Kyiv"),
            new Tuple5<>("1.186.0.1", "United Kingdom", "UK", JbstConstants.Flags.UK, "London"),
            new Tuple5<>("1.186.0.2", "United Kingdom", "UK", JbstConstants.Flags.UK, "Manchester"),
            new Tuple5<>("55.255.1.1", "USA", "US", JbstConstants.Flags.USA, "New York"),
            new Tuple5<>("55.255.1.2", "USA", "US", JbstConstants.Flags.USA, "Washington"),
            new Tuple5<>("55.255.1.3", "USA", "US", JbstConstants.Flags.USA, "Las Vegas"),
            new Tuple5<>("55.255.1.4", "USA", "US", JbstConstants.Flags.USA, "Los Angeles"),
            new Tuple5<>("149.90.0.1", "Portugal", "PT", JbstConstants.Flags.PORTUGAL, "Porto"),
            new Tuple5<>("149.90.0.2", "Portugal", "PT", JbstConstants.Flags.PORTUGAL, "Lisbon")
    );

    private final String ipAddr;
    private final String country;
    private final String countryCode;
    private final String countryFlag;
    @JsonIgnore
    private final String city;
    @JsonIgnore
    private final String exceptionDetails;

    public JbstGeoLocation(
            String ipAddr,
            String country,
            String countryCode,
            String countryFlag,
            String city,
            String exceptionDetails
    ) {
        this.ipAddr = ipAddr;
        if (nonNull(countryCode)) {
            this.countryCode = countryCode;
        } else {
            this.countryCode = JbstConstants.Strings.UNKNOWN;
        }
        if (nonNull(countryFlag)) {
            this.countryFlag = countryFlag;
        } else {
            this.countryFlag = JbstConstants.Flags.UNKNOWN;
        }
        if (nonNull(country)) {
            this.country = country.trim();
            this.city = nonNull(city) ? city.trim() : null;
        } else {
            this.country = JbstConstants.Strings.UNKNOWN;
            this.city = null;
        }
        this.exceptionDetails = exceptionDetails;
    }

    public static JbstGeoLocation unknown(
            IPAddress ipAddress,
            String exceptionDetails
    ) {
        return new JbstGeoLocation(
                getIpAddrOrUnknown(ipAddress),
                JbstConstants.Strings.UNKNOWN,
                JbstConstants.Strings.UNKNOWN,
                JbstConstants.Flags.UNKNOWN,
                JbstConstants.Strings.UNKNOWN,
                exceptionDetails
        );
    }

    public static JbstGeoLocation processing(
            IPAddress ipAddress
    ) {
        return new JbstGeoLocation(
                getIpAddrOrUnknown(ipAddress),
                JbstConstants.Strings.UNDEFINED,
                JbstConstants.Strings.UNDEFINED,
                JbstConstants.Flags.UNKNOWN,
                JbstConstants.Strings.UNDEFINED,
                ""
        );
    }

    public static JbstGeoLocation processed(
            IPAddress ipAddress,
            String country,
            String countryCode,
            String countryFlag,
            String city
    ) {
        return new JbstGeoLocation(
                getIpAddrOrUnknown(ipAddress),
                country,
                countryCode,
                countryFlag,
                city,
                ""
        );
    }

    public static JbstGeoLocation valid() {
        return JbstGeoLocation.processed(
                IPAddress.localhost(),
                "Ukraine",
                "UA",
                "🇺🇦",
                "Lviv"
        );
    }

    public static JbstGeoLocation invalid() {
        return JbstGeoLocation.unknown(
                IPAddress.localhost(),
                "Location is unknown"
        );
    }

    public static JbstGeoLocation random() {
        return randomBoolean() ? valid() : invalid();
    }

    public static JbstGeoLocation testData() {
        var tuple5 = JbstRandom.randomElement(TEST_DATA);
        return JbstGeoLocation.processed(
                new IPAddress(tuple5.a()),
                tuple5.b(),
                tuple5.c(),
                tuple5.d(),
                tuple5.e()
        );
    }

    public String getWhere() {
        var countryPresent = hasLength(this.country);
        var cityPresent = hasLength(this.city);
        var countryFlagPrefix = hasLength(this.countryFlag) ? this.countryFlag + " " : "";
        if (countryPresent && !cityPresent) {
            return countryFlagPrefix + this.country;
        }
        if (countryPresent) {
            return countryFlagPrefix + this.country + ", " + this.city;
        }
        return JbstConstants.Flags.UNKNOWN + " " + JbstConstants.Strings.UNKNOWN;
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    public static String getIpAddrOrUnknown(IPAddress ipAddress) {
        return nonNull(ipAddress) ? ipAddress.value() : JbstConstants.Strings.UNKNOWN;
    }
}
