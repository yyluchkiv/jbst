package jbst.foundation.domain.cryptography;

import jbst.foundation.domain.tuples.Tuple2;
import lombok.experimental.UtilityClass;

import static java.util.Base64.getEncoder;

@UtilityClass
public class JbstEncoding {

    public static Tuple2<String, String> getBasicAuthenticationHeader(String username, String password) {
        return new Tuple2<>(
                "Authorization",
                getBasicAuthenticationHeaderValue(username, password)
        );
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private static String getBasicAuthenticationHeaderValue(String username, String password) {
        return "Basic " + getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
