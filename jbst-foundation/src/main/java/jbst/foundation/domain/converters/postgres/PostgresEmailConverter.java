package jbst.foundation.domain.converters.postgres;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jbst.foundation.domain.base.Email;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.nonNull;

@Converter
public class PostgresEmailConverter implements AttributeConverter<Email, String> {

    @Override
    public String convertToDatabaseColumn(@Nullable Email email) {
        return nonNull(email) ? email.value() : null;
    }

    @Override
    public Email convertToEntityAttribute(@Nullable String value) {
        return nonNull(value) ? Email.of(value) : null;
    }
}
