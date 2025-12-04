package jbst.foundation.domain.jwt;

import io.jsonwebtoken.Claims;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.strings.JbstStrings;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public record JbstJwtTokenValidatedClaims(
        boolean valid,
        boolean isAccess,
        boolean isRefresh,
        String jwtToken,
        Username username,
        Date issuedAt,
        Date expirationDate,
        List<SimpleGrantedAuthority> authorities
) {
    private static final Username INVALID = Username.of("invalid");

    public static Date getIssuedAt() {
        return new Date();
    }

    public static JbstJwtTokenValidatedClaims invalid(boolean isAccess, boolean isRefresh, String jwtToken) {
        return new JbstJwtTokenValidatedClaims(false, isAccess, isRefresh, jwtToken, INVALID, new Date(0), new Date(0), new ArrayList<>());
    }

    public static JbstJwtTokenValidatedClaims invalid(JbstJwtAccessToken accessToken) {
        return invalid(true, false, accessToken.value());
    }

    public static JbstJwtTokenValidatedClaims invalid(JbstJwtRefreshToken refreshToken) {
        return invalid(false, true, refreshToken.value());
    }

    public static JbstJwtTokenValidatedClaims valid(boolean isAccess, boolean isRefresh, String jwtToken, Claims claims) {
        var username = Username.of(claims.getSubject());
        var issuedAt = claims.getIssuedAt();
        var expirationDate = claims.getExpiration();
        List<SimpleGrantedAuthority> authorities;
        var claimsAuthoritiesAttribute = claims.get("authorities");
        if (nonNull(claimsAuthoritiesAttribute)) {
            authorities = Arrays.stream(
                            claimsAuthoritiesAttribute.toString()
                                    .replace("[", "")
                                    .replace("]", "")
                                    .replace("{", "")
                                    .replace("}", "")
                                    .replace("authority=", "")
                                    .split(",")
                    )
                    .filter(JbstStrings::hasLength)
                    .map(rawUserRole -> new SimpleGrantedAuthority(rawUserRole.trim()))
                    .toList();
        } else {
            authorities = new ArrayList<>();
        }
        return new JbstJwtTokenValidatedClaims(true, isAccess, isRefresh, jwtToken, username, issuedAt, expirationDate, authorities);
    }

    public static JbstJwtTokenValidatedClaims valid(JbstJwtAccessToken accessToken, Claims claims) {
        return valid(true, false, accessToken.value(), claims);
    }

    public static JbstJwtTokenValidatedClaims valid(JbstJwtRefreshToken refreshToken, Claims claims) {
        return valid(false, true, refreshToken.value(), claims);
    }

    public boolean isInvalid() {
        return !this.valid;
    }

    public boolean isExpired() {
        return this.isInvalid() || getIssuedAt().after(this.expirationDate);
    }

    public long getExpirationTimestamp() {
        return this.expirationDate.getTime();
    }

    public Set<String> authoritiesAsStrings() {
        return this.authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toSet());
    }
}
