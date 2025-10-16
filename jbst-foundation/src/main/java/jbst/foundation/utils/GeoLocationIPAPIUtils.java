package jbst.foundation.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import feign.Param;
import feign.RequestLine;
import jbst.foundation.domain.exceptions.geo.JbstGeoLocationNotFoundException;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GeoLocationIPAPIUtils {

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
    private final JbstGeoUtils geoUtils;

    public GeoLocation getGeoLocation(IPAddress ipAddress) throws JbstGeoLocationNotFoundException {
        try {
            var queryResponse = this.definition.getIPAPIResponse(ipAddress.value());
            if (queryResponse.isSuccess()) {
                var countryCode = queryResponse.countryCode();
                var countryFlag = this.geoUtils.getFlagEmojiByCountryCode(countryCode);
                return GeoLocation.processed(
                        ipAddress,
                        queryResponse.country(),
                        countryCode,
                        countryFlag,
                        queryResponse.city()
                );
            } else {
                throw new JbstGeoLocationNotFoundException(queryResponse.message());
            }
        } catch (RuntimeException throwable) {
            throw new JbstGeoLocationNotFoundException(throwable.getMessage());
        }
    }
}
