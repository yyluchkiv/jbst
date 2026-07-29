package jbst.foundation.domain.converters;

import jbst.foundation.domain.jsons.JbstObjectMappers;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

@SuppressWarnings("ConverterNotAnnotatedInspection")
public abstract class JbstAbstractAttributeConverter<X, Y> implements AttributeConverter<X, Y> {
    protected final ObjectMapper MAPPER = JbstObjectMappers.jackson2Compatible();
}
