package jbst.foundation.utilities.browsers;

import jbst.foundation.domain.http.requests.UserAgentDetails;
import jbst.foundation.domain.http.requests.UserAgentHeader;

// TODO [YYL] utilities -> utils
@Deprecated(forRemoval = true)
public interface UserAgentDetailsUtility {
    UserAgentDetails getUserAgentDetails(UserAgentHeader userAgentHeader);
}
