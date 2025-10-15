package jbst.foundation.domain.exceptions.geo;

public class JbstGeoLocationNotFoundException extends Exception {

    public JbstGeoLocationNotFoundException(String message) {
        super("Geo location not found: " + message);
    }
}
