package jbst.foundation.domain.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.Version;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneId;
import java.util.Map;

import static java.util.Objects.nonNull;

@UtilityClass
public class PostgresConverters {
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
