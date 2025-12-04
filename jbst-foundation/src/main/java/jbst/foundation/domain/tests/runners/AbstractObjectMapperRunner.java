package jbst.foundation.domain.tests.runners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jbst.foundation.domain.annotations.JbstDeletionScheduled;
import lombok.SneakyThrows;

@JbstDeletionScheduled(version = "v1.48")
@Deprecated
public abstract class AbstractObjectMapperRunner {
    protected static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .build();

    protected static final ObjectMapper PLAIN_OBJECT_MAPPER = JsonMapper.builder()
            .build();

    @SneakyThrows
    protected final String writeValueAsString(Object object) {
        return OBJECT_MAPPER
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(object);
    }

    @SneakyThrows
    protected final String writeValueAsPlainString(Object object) {
        return PLAIN_OBJECT_MAPPER
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(object);
    }
}
