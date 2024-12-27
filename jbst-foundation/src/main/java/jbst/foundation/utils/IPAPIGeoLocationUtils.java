package jbst.foundation.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import feign.Param;
import feign.RequestLine;
import jbst.foundation.domain.exceptions.geo.GeoLocationNotFoundException;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.utilities.geo.functions.ipapi.utility.IPAPIGeoLocationUtility;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IPAPIGeoLocationUtils implements IPAPIGeoLocationUtility {

    // Classes: Definitions
    public interface IPAPIDefinition {
        @RequestLine("GET /json/{ipAddress}")
        IPAPIResponse getIPAPIResponse(@Param("ipAddress") String ipAddress);
    }

    // Classes: Responses
    // JSON
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record IPAPIResponse(
            String status,
            String country,
            String countryCode,
            String city,
            String message
    ) {
        @JsonIgnore
        public boolean isSuccess() {
            return "success".equals(this.status);
        }
    }

    // Definitions
    private final IPAPIDefinition definition;
    // Utils
    private final GeoCountryFlagUtils geoCountryFlagUtils;

    @Override
    public GeoLocation getGeoLocation(IPAddress ipAddress) throws GeoLocationNotFoundException {
        try {
            var queryResponse = this.definition.getIPAPIResponse(ipAddress.value());
            if (queryResponse.isSuccess()) {
                var countryCode = queryResponse.countryCode();
                var countryFlag = this.geoCountryFlagUtils.getFlagEmojiByCountryCode(countryCode);
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
