package jbst.foundation.utils;

import jbst.foundation.domain.exceptions.geo.JbstGeoLocationNotFoundException;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;
import lombok.RequiredArgsConstructor;

// TODO [YYL] support vs. orchestrators
@RequiredArgsConstructor
public final class GeoLocationUtils {

    private final JbstGeoUtils geoUtils;
    private final GeoLocationMindMaxUtils geoLocationMindMaxUtils;

    public GeoLocation getGeoLocation(IPAddress ipAddress) {
        try {
            return this.geoUtils.getGeoLocationIPAPI(ipAddress);
        } catch (JbstGeoLocationNotFoundException ex1) {
            try {
                return this.geoLocationMindMaxUtils.getGeoLocation(ipAddress);
            } catch (JbstGeoLocationNotFoundException ex2) {
                return GeoLocation.unknown(ipAddress, ex2.getMessage());
            }
        }
    }
}
