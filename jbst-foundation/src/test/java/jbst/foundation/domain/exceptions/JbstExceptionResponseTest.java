package jbst.foundation.domain.exceptions;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.JbstUnitTests.Runners.Base;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static jbst.foundation.domain.exceptions.JbstExceptionResponse.Type.ERROR;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.assertj.core.api.Assertions.assertThat;

class JbstExceptionResponseTest extends Base {

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void serializeDeserializeTest() {
        // Arrange
        var exceptionMessage = randomString();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
        var exceptionResponse = JbstExceptionResponse.of(JbstExceptionResponse.Type.ERROR, new NullPointerException(exceptionMessage));

        // Act
        var json = this.writeValueAsString(exceptionResponse);
        HashMap<String, Object> exceptionEntity = OBJECT_MAPPER.readValue(json, typeRef);

        // Assert
        assertThat(exceptionEntity)
                .hasSize(4)
                .containsKeys("jbsTimestamp", "jbstType", "jbstMessageOnClient", "jbstAttributes")
                .containsEntry("jbstType", ERROR.toString());
        assertThat(exceptionEntity.get("jbsTimestamp")).isNotNull();
        var attributes = (Map<String, Object>) exceptionEntity.get("jbstAttributes");
        assertThat(attributes)
                .hasSize(1)
                .containsEntry("jbstTrace", exceptionMessage);
    }
}
