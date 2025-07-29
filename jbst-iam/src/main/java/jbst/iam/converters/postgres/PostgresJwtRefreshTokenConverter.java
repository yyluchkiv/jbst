package jbst.iam.converters.postgres;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jbst.iam.domain.jwt.JwtRefreshToken;

import static java.util.Objects.nonNull;

@Converter
public class PostgresJwtRefreshTokenConverter implements AttributeConverter<JwtRefreshToken, String> {

    @Override
    public String convertToDatabaseColumn(JwtRefreshToken accessToken) {
        return nonNull(accessToken) ? accessToken.value() : null;
    }

    @Override
    public JwtRefreshToken convertToEntityAttribute(String value) {
        return nonNull(value) ? JwtRefreshToken.of(value) : null;
    }
}
