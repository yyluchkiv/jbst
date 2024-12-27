package jbst.foundation.utilities.geo.functions.ipapi.utility;

import jbst.foundation.domain.exceptions.geo.GeoLocationNotFoundException;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;

@Deprecated(forRemoval = true)
public interface IPAPIGeoLocationUtility {
    GeoLocation getGeoLocation(IPAddress ipAddress) throws GeoLocationNotFoundException;
}
