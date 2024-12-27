package jbst.foundation.utilities.geo.facades.impl;

import jbst.foundation.domain.exceptions.geo.GeoLocationNotFoundException;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.utilities.geo.facades.GeoLocationFacadeUtility;
import jbst.foundation.utilities.geo.functions.ipapi.utility.IPAPIGeoLocationUtility;
import jbst.foundation.utilities.geo.functions.mindmax.MindMaxGeoLocationUtility;
import lombok.RequiredArgsConstructor;

@Deprecated(forRemoval = true)
@RequiredArgsConstructor
public class GeoLocationFacadeUtilityImpl implements GeoLocationFacadeUtility {

    private final IPAPIGeoLocationUtility ipapiGeoLocationUtility;
    private final MindMaxGeoLocationUtility mindMaxGeoLocationUtility;

    @Override
    public GeoLocation getGeoLocation(IPAddress ipAddress) {
        try {
            return this.ipapiGeoLocationUtility.getGeoLocation(ipAddress);
        } catch (GeoLocationNotFoundException ex) {
            return this.mindMaxGeoLocationUtility.getGeoLocation(ipAddress);
        }
    }
}
