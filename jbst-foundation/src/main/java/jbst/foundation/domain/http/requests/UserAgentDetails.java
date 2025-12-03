package jbst.foundation.domain.http.requests;

import jbst.foundation.domain.constants.JbstConstants;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.random.JbstRandom.randomBoolean;
import static jbst.foundation.domain.random.JbstRandom.randomElement;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class UserAgentDetails {
    private static final List<String> TEST_DATA_BROWSERS = List.of("Chrome", "Mozilla", "Opera", "Edge");
    private static final List<String> TEST_DATA_PLATFORMS = List.of("macOS", "linux", "windows");

    private final String browser;
    private final String platform;
    private final String deviceType;
    private final String exceptionDetails;

    public UserAgentDetails(
            String browser,
            String platform,
            String deviceType,
            String exceptionDetails
    ) {
        this.browser = nonNull(browser) ? browser: JbstConstants.Strings.UNKNOWN;
        this.platform = nonNull(platform) ? platform: JbstConstants.Strings.UNKNOWN;
        this.deviceType = nonNull(deviceType) ? deviceType: JbstConstants.Strings.UNKNOWN;
        this.exceptionDetails = exceptionDetails;
    }

    public static UserAgentDetails unknown(
            String exceptionDetails
    ) {
        return new UserAgentDetails(
                JbstConstants.Strings.UNKNOWN,
                JbstConstants.Strings.UNKNOWN,
                JbstConstants.Strings.UNKNOWN,
                exceptionDetails
        );
    }

    public static UserAgentDetails processing() {
        return new UserAgentDetails(
                JbstConstants.Strings.UNDEFINED,
                JbstConstants.Strings.UNDEFINED,
                JbstConstants.Strings.UNDEFINED,
                ""
        );
    }

    public static UserAgentDetails processed(
            String browser,
            String platform,
            String deviceType
    ) {
        return new UserAgentDetails(
                browser,
                platform,
                deviceType,
                ""
        );
    }

    public static UserAgentDetails valid() {
        return UserAgentDetails.processed(
                "Chrome",
                "macOS",
                "Desktop"
        );
    }

    public static UserAgentDetails invalid() {
        return UserAgentDetails.unknown(
                "User agent details are unknown"
        );
    }

    public static UserAgentDetails random() {
        return randomBoolean() ? valid() : invalid();
    }

    public static UserAgentDetails testData() {
        return UserAgentDetails.processed(
                randomElement(TEST_DATA_BROWSERS),
                randomElement(TEST_DATA_PLATFORMS),
                "Desktop"
        );
    }

    public String getWhat() {
        return this.browser + ", " + this.platform + " on " + this.deviceType;
    }
}
