package jbst.iam.converters.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

public abstract class AbstractAttributeConverter<X, Y> implements AttributeConverter<X, Y> {
    protected final ObjectMapper objectMapper = new ObjectMapper();
}
