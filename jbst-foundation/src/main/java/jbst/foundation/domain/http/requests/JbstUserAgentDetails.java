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
public class JbstUserAgentDetails {
    private static final List<String> TEST_DATA_BROWSERS = List.of("Chrome", "Mozilla", "Opera", "Edge");
    private static final List<String> TEST_DATA_PLATFORMS = List.of("macOS", "linux", "windows");

    private final String browser;
    private final String platform;
    private final String deviceType;
    private final String exceptionDetails;

    public JbstUserAgentDetails(
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

    public static JbstUserAgentDetails unknown(
            String exceptionDetails
    ) {
        return new JbstUserAgentDetails(
                JbstConstants.Strings.UNKNOWN,
                JbstConstants.Strings.UNKNOWN,
                JbstConstants.Strings.UNKNOWN,
                exceptionDetails
        );
    }

    public static JbstUserAgentDetails processing() {
        return new JbstUserAgentDetails(
                JbstConstants.Strings.UNDEFINED,
                JbstConstants.Strings.UNDEFINED,
                JbstConstants.Strings.UNDEFINED,
                ""
        );
    }

    public static JbstUserAgentDetails processed(
            String browser,
            String platform,
            String deviceType
    ) {
        return new JbstUserAgentDetails(
                browser,
                platform,
                deviceType,
                ""
        );
    }

    public static JbstUserAgentDetails valid() {
        return JbstUserAgentDetails.processed(
                "Chrome",
                "macOS",
                "Desktop"
        );
    }

    public static JbstUserAgentDetails invalid() {
        return JbstUserAgentDetails.unknown(
                "User agent details are unknown"
        );
    }

    public static JbstUserAgentDetails random() {
        return randomBoolean() ? valid() : invalid();
    }

    public static JbstUserAgentDetails testData() {
        return JbstUserAgentDetails.processed(
                randomElement(TEST_DATA_BROWSERS),
                randomElement(TEST_DATA_PLATFORMS),
                "Desktop"
        );
    }

    public String getWhat() {
        return this.browser + ", " + this.platform + " on " + this.deviceType;
    }
}
