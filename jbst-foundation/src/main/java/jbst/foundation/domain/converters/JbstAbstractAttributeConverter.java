package jbst.foundation.domain.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

@SuppressWarnings("ConverterNotAnnotatedInspection")
public abstract class JbstAbstractAttributeConverter<X, Y> implements AttributeConverter<X, Y> {
    protected final ObjectMapper MAPPER = new ObjectMapper();
}
