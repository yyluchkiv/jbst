package jbst.foundation.utilities.geo.functions.ipapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Deprecated(forRemoval = true)
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
