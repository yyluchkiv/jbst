package jbst.foundation.utilities.geo.functions.ipapi.feign;

import feign.Param;
import feign.RequestLine;
import jbst.foundation.utilities.geo.functions.ipapi.domain.IPAPIResponse;

@Deprecated(forRemoval = true)
public interface IPAPIFeign {
    @RequestLine("GET /json/{ipAddress}")
    IPAPIResponse getIPAPIResponse(@Param("ipAddress") String ipAddress);
}
