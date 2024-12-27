package jbst.foundation.utilities.geo.facades;

import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;

@Deprecated(forRemoval = true)
public interface GeoLocationFacadeUtility {
    GeoLocation getGeoLocation(IPAddress ipAddress);
}
