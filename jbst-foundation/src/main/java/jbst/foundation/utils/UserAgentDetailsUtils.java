package jbst.foundation.utils;

import com.blueconic.browscap.BrowsCapField;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.http.requests.UserAgentDetails;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.enums.Status.FAILURE;
import static jbst.foundation.domain.enums.Status.SUCCESS;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.contactDevelopmentTeam;

@Slf4j
public final class UserAgentDetailsUtils {
    private static final String CONFIGURATION_LOG = PREFIX + " User agent — {}";

    private final UserAgentParser userAgentParser;
    private final boolean configured;
    private final String exceptionMessage;

    // Properties
    private final JbstProperties jbstProperties;

    public UserAgentDetailsUtils(JbstProperties jbstProperties) {
        this.jbstProperties = jbstProperties;
        UserAgentParser userAgentParserOrNull;
        boolean configuredFlag;
        String exceptionMessageOrNull;
        var enabled = this.jbstProperties.getUtilsConfigs().getUserAgentConfigs().isEnabled();
        LOGGER.info(CONFIGURATION_LOG, Status.of(enabled).formatAnsi());
        if (enabled) {
            try {
                userAgentParserOrNull = new UserAgentService().loadParser(
                        List.of(
                                BrowsCapField.BROWSER,
                                BrowsCapField.PLATFORM,
                                BrowsCapField.DEVICE_TYPE
                        )
                );
                configuredFlag = true;
                exceptionMessageOrNull = null;
                LOGGER.info(CONFIGURATION_LOG, SUCCESS);
            } catch (ParseException | IOException ex) {
                LOGGER.error(CONFIGURATION_LOG, FAILURE);
                throw new IllegalArgumentException(ex);
            }
        } else {
            userAgentParserOrNull = null;
            configuredFlag = false;
            exceptionMessageOrNull = contactDevelopmentTeam("User agent configuration failure");
        }
        this.userAgentParser = userAgentParserOrNull;
        this.configured = configuredFlag;
        this.exceptionMessage = exceptionMessageOrNull;
    }

    public UserAgentDetails getUserAgentDetails(UserAgentHeader userAgentHeader) {
        if (!this.jbstProperties.getUtilsConfigs().getUserAgentConfigs().isEnabled() || !this.configured) {
            return UserAgentDetails.unknown(this.exceptionMessage);
        }
        var capabilities = this.userAgentParser.parse(userAgentHeader.getValue());
        return UserAgentDetails.processed(
                capabilities.getBrowser(),
                capabilities.getPlatform(),
                capabilities.getDeviceType()
        );
    }
}
