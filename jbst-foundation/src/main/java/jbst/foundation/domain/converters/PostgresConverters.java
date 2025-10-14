package jbst.foundation.domain.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jbst.foundation.domain.base.Username;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.nonNull;

@UtilityClass
public class PostgresConverters {

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
}
