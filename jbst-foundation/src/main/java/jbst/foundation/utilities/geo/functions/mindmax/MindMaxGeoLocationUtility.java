package jbst.foundation.utilities.geo.functions.mindmax;

import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;

@Deprecated(forRemoval = true)
public interface MindMaxGeoLocationUtility {
    GeoLocation getGeoLocation(IPAddress ipAddress);
}
