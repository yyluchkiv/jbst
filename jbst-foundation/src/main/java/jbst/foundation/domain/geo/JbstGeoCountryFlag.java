package jbst.foundation.domain.geo;

public record JbstGeoCountryFlag(
        String name,
        String code,
        String emoji,
        String unicode
) {
    public static JbstGeoCountryFlag unknown() {
        return new JbstGeoCountryFlag("Unknown", "Unknown", "🏴‍", "—");
    }
}
