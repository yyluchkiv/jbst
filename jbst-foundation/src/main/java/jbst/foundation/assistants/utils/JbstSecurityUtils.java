package jbst.foundation.assistants.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.jwt.*;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.TimeAmount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.jwt.JwtTokenValidatedClaims.getIssuedAt;
import static jbst.foundation.utilities.time.DateUtility.convertLocalDateTime;

@Slf4j
@Component
public class JbstSecurityUtils {

    // Properties
    private final JbstProperties jbstProperties;
    // Values
    private final SecretKey secretKey;

    @Autowired
    public JbstSecurityUtils(JbstProperties jbstProperties) {
        this.jbstProperties = jbstProperties;
        var jwtTokensConfigs = this.jbstProperties.getSecurityJwtConfigs().getJwtTokensConfigs();
        jwtTokensConfigs.assertProperties(new PropertyId("securityJwtConfigs.jwtTokensConfigs"));
        // WARNING: consider using Base64 encoded key in properties, and decode it here
        // https://www.baeldung.com/spring-security-sign-jwt-token#1-using-key-instance
        this.secretKey = Keys.hmacShaKeyFor(jwtTokensConfigs.getSecretKey().getBytes());
    }

    public final JwtUser getAuthenticatedJwtUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (nonNull(authentication)) {
            try {
                return (JwtUser) authentication.getPrincipal();
            } catch (ClassCastException ex) {
                var message = "Illegal request. Authentication principal is not a JwtUser. EX: " + ex.getMessage();
                throw new IllegalArgumentException(message);
            }
        } else {
            var message = "Illegal request. Authentication is null";
            throw new IllegalArgumentException(message);
        }
    }

    public final String getAuthenticatedUsername() {
        return this.getAuthenticatedJwtUser().getUsername();
    }

    public final String getAuthenticatedUsernameOrUnexpected() {
        try {
            return this.getAuthenticatedJwtUser().getUsername();
        } catch (RuntimeException ex) {
            return "[unexpected]";
        }
    }

    public final JwtAccessToken createJwtAccessToken(JwtTokenCreationParams creationParams) {
        var accessTokenConfiguration = this.jbstProperties.getSecurityJwtConfigs().getJwtTokensConfigs().getAccessToken();
        var jwtToken = this.createJwtToken(creationParams, accessTokenConfiguration.getExpiration());
        return new JwtAccessToken(jwtToken);
    }

    public final JwtRefreshToken createJwtRefreshToken(JwtTokenCreationParams creationParams) {
        var refreshTokenConfiguration = this.jbstProperties.getSecurityJwtConfigs().getJwtTokensConfigs().getRefreshToken();
        var jwtToken = this.createJwtToken(creationParams, refreshTokenConfiguration.getExpiration());
        return new JwtRefreshToken(jwtToken);
    }

    public final String createJwtToken(JwtTokenCreationParams creationParams, TimeAmount timeAmount) {
        var claims = Jwts.claims().subject(creationParams.username().value());
        claims.add("authorities", creationParams.authorities());
        var zoneId = creationParams.zoneId();
        var expiration = LocalDateTime.now(zoneId).plus(timeAmount.getAmount(), timeAmount.getUnit());
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claims(claims.build())
                .issuedAt(getIssuedAt())
                .expiration(convertLocalDateTime(expiration, zoneId))
                .signWith(this.secretKey)
                .compact();
    }

    public final JwtTokenValidatedClaims validate(JwtAccessToken jwtAccessToken) {
        return this.validate(jwtAccessToken.value(), true, false);
    }

    public final JwtTokenValidatedClaims validate(JwtRefreshToken jwtRefreshToken) {
        return this.validate(jwtRefreshToken.value(), false, true);
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private JwtTokenValidatedClaims validate(String jwtToken, boolean isAccess, boolean isRefresh) {
        try {
            var claims = Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(jwtToken).getPayload();
            return JwtTokenValidatedClaims.valid(isAccess, isRefresh, jwtToken, claims);
        } catch (ExpiredJwtException ex1) {
            LOGGER.info("JWT token expired", ex1);
            return JwtTokenValidatedClaims.valid(isAccess, isRefresh, jwtToken, ex1.getClaims());
        } catch (JwtException | IllegalArgumentException ex2) {
            LOGGER.info("JWT token exception", ex2);
            return JwtTokenValidatedClaims.invalid(isAccess, isRefresh, jwtToken);
        }
    }
}
