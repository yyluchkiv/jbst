package jbst.foundation.utilities.geo.functions.ipapi.utility.impl;

import jbst.foundation.domain.exceptions.geo.GeoLocationNotFoundException;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.utilities.geo.facades.GeoCountryFlagUtility;
import jbst.foundation.utilities.geo.functions.ipapi.feign.IPAPIFeign;
import jbst.foundation.utilities.geo.functions.ipapi.utility.IPAPIGeoLocationUtility;
import lombok.RequiredArgsConstructor;

@Deprecated(forRemoval = true)
@RequiredArgsConstructor
public class IPAPIGeoLocationUtilityImpl implements IPAPIGeoLocationUtility {

    // Feign
    private final IPAPIFeign ipapiFeign;
    // Utilities
    private final GeoCountryFlagUtility geoCountryFlagUtility;

    @Override
    public GeoLocation getGeoLocation(IPAddress ipAddress) throws GeoLocationNotFoundException {
        try {
            var queryResponse = this.ipapiFeign.getIPAPIResponse(ipAddress.value());
            if (queryResponse.isSuccess()) {
                var countryCode = queryResponse.countryCode();
                var countryFlag = this.geoCountryFlagUtility.getFlagEmojiByCountryCode(countryCode);
                return GeoLocation.processed(
                        ipAddress,
                        queryResponse.country(),
                        countryCode,
                        countryFlag,
                        queryResponse.city()
                );
            } else {
                throw new GeoLocationNotFoundException(queryResponse.message());
            }
        } catch (RuntimeException throwable) {
            throw new GeoLocationNotFoundException(throwable.getMessage());
        }
    }
}
