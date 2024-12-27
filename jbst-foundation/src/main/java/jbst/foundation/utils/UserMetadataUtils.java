package jbst.foundation.utils;

import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserMetadataUtils {
    // Utils
    private final GeoLocationUtils geoLocationUtils;
    private final UserAgentDetailsUtils userAgentDetailsUtils;

    public final UserRequestMetadata getUserRequestMetadataProcessed(IPAddress ipAddress, UserAgentHeader userAgentHeader) {
        return UserRequestMetadata.processed(
                this.geoLocationUtils.getGeoLocation(ipAddress),
                this.userAgentDetailsUtils.getUserAgentDetails(userAgentHeader)
        );
    }
}
