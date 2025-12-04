package jbst.foundation.domain.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.UserAgentDetails;
import jbst.foundation.domain.http.requests.UserRequestMetadata;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasLength;

@UtilityClass
public class JbstPostgresConverters {
    @Converter
    public class EmailConverter implements AttributeConverter<Email, String> {
        @Override
        public String convertToDatabaseColumn(@Nullable Email email) {
            return nonNull(email) ? email.value() : null;
        }

        @Override
        public Email convertToEntityAttribute(@Nullable String value) {
            return nonNull(value) ? Email.of(value) : null;
        }
    }

    @Converter
    public class JwtAccessTokenConverter implements AttributeConverter<JwtAccessToken, String> {

        @Override
        public String convertToDatabaseColumn(JwtAccessToken accessToken) {
            return nonNull(accessToken) ? accessToken.value() : null;
        }

        @Override
        public JwtAccessToken convertToEntityAttribute(String value) {
            return nonNull(value) ? JwtAccessToken.of(value) : null;
        }
    }

    @Converter
    public class JwtRefreshTokenConverter implements AttributeConverter<JwtRefreshToken, String> {

        @Override
        public String convertToDatabaseColumn(JwtRefreshToken accessToken) {
            return nonNull(accessToken) ? accessToken.value() : null;
        }

        @Override
        public JwtRefreshToken convertToEntityAttribute(String value) {
            return nonNull(value) ? JwtRefreshToken.of(value) : null;
        }
    }

    @Converter
    public class MapStringsObjectsConverter implements AttributeConverter<Map<String, Object>, String> {
        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        @SneakyThrows
        @Override
        public String convertToDatabaseColumn(Map<String, Object> authorities) {
            return OBJECT_MAPPER.writeValueAsString(authorities);
        }

        @SneakyThrows
        @Override
        public Map<String, Object> convertToEntityAttribute(String value) {
            var typeReference = new TypeReference<Map<String, Object>>() {};
            return OBJECT_MAPPER.readValue(value, typeReference);
        }
    }

    @Converter
    public class PasswordConverter implements AttributeConverter<Password, String> {
        @Override
        public String convertToDatabaseColumn(Password password) {
            return password.value();
        }

        @Override
        public Password convertToEntityAttribute(String value) {
            return Password.of(value);
        }
    }

    @Converter
    public class SimpleGrantedAuthoritiesSetConverter implements AttributeConverter<Set<SimpleGrantedAuthority>, String> {

        @Override
        public String convertToDatabaseColumn(Set<SimpleGrantedAuthority> authorities) {
            return !isEmpty(authorities) ? authorities.stream().map(SimpleGrantedAuthority::getAuthority).sorted().collect(joining(JbstConstants.Symbols.SEMICOLON)) : "";
        }

        @Override
        public Set<SimpleGrantedAuthority> convertToEntityAttribute(String value) {
            return hasLength(value) ? getSimpleGrantedAuthorities(Stream.of(value.split(JbstConstants.Symbols.SEMICOLON))) : new HashSet<>();
        }
    }

    @Converter
    public class UsernameConverter implements AttributeConverter<Username, String> {
        @Override
        public String convertToDatabaseColumn(@Nullable Username username) {
            return nonNull(username) ? username.value() : null;
        }

        @Override
        public Username convertToEntityAttribute(@Nullable String value) {
            return nonNull(value) ? Username.of(value) : null;
        }
    }

    @Converter
    public class UserCreationOptionConverter implements AttributeConverter<JbstUserCreationOption, String> {

        @Override
        public String convertToDatabaseColumn(JbstUserCreationOption obj) {
            return nonNull(obj) ? obj.getValue() : null;
        }

        @Override
        public JbstUserCreationOption convertToEntityAttribute(String column) {
            return nonNull(column) ? JbstUserCreationOption.find(column) : null;
        }
    }

    @Converter
    public class UserRequestMetadataConverter extends JbstAbstractAttributeConverter<UserRequestMetadata, String> {

        @SneakyThrows
        @Override
        public String convertToDatabaseColumn(UserRequestMetadata metadata) {
            Map<String, String> json = new HashMap<>();
            var geoLocation = metadata.getGeoLocation();
            var userAgentDetails = metadata.getUserAgentDetails();
            json.put("status", metadata.getStatus().name());
            json.put("ipAddr", geoLocation.getIpAddr());
            json.put("country", geoLocation.getCountry());
            json.put("countryCode", geoLocation.getCountryCode());
            json.put("countryFlag", geoLocation.getCountryFlag());
            json.put("city", geoLocation.getCity());
            json.put("geoLocationExceptionDetails", geoLocation.getExceptionDetails());
            json.put("browser", userAgentDetails.getBrowser());
            json.put("platform", userAgentDetails.getPlatform());
            json.put("deviceType", userAgentDetails.getDeviceType());
            json.put("userAgentDetailsExceptionDetails", userAgentDetails.getExceptionDetails());
            return MAPPER.writeValueAsString(json);
        }

        @SneakyThrows
        @Override
        public UserRequestMetadata convertToEntityAttribute(String value) {
            var typeReference = new TypeReference<Map<String, String>>() {};
            var json = MAPPER.readValue(value, typeReference);
            return new UserRequestMetadata(
                    Status.valueOf(json.get("status")),
                    new GeoLocation(
                            json.get("ipAddr"),
                            json.get("country"),
                            json.get("countryCode"),
                            json.get("countryFlag"),
                            json.get("city"),
                            json.get("geoLocationExceptionDetails")
                    ),
                    new UserAgentDetails(
                            json.get("browser"),
                            json.get("platform"),
                            json.get("deviceType"),
                            json.get("userAgentDetailsExceptionDetails")
                    )
            );
        }
    }

    @SuppressWarnings("unused")
    @Converter
    public class VersionConverter implements AttributeConverter<Version, String> {

        @Override
        public String convertToDatabaseColumn(Version version) {
            return version.value();
        }

        @Override
        public Version convertToEntityAttribute(String value) {
            return new Version(value);
        }
    }

    @Converter
    public class ZoneIdConverter implements AttributeConverter<ZoneId, String> {

        @Override
        public String convertToDatabaseColumn(@Nullable ZoneId zoneId) {
            return nonNull(zoneId) ? zoneId.getId() : null;
        }

        @Override
        public ZoneId convertToEntityAttribute(@Nullable String value) {
            return nonNull(value) ? ZoneId.of(value) : null;
        }
    }

}
