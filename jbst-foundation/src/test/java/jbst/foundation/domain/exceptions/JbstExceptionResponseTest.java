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
        var arrangedExceptionEntity = new JbstExceptionResponse(new NullPointerException(exceptionMessage));
        arrangedExceptionEntity.addAttribute("externalAttribute", randomString());

        // Act
        var json = this.writeValueAsString(arrangedExceptionEntity);
        HashMap<String, Object> exceptionEntity = OBJECT_MAPPER.readValue(json, typeRef);

        // Assert
        assertThat(exceptionEntity)
                .hasSize(3)
                .containsKeys("exceptionEntityType", "attributes", "timestamp")
                .containsEntry("exceptionEntityType", ERROR.toString());
        assertThat(exceptionEntity.get("timestamp")).isNotNull();
        var attributes = (Map<String, Object>) exceptionEntity.get("attributes");
        assertThat(attributes)
                .hasSize(3)
                .containsKey("externalAttribute")
                .containsEntry("shortMessage", exceptionMessage)
                .containsEntry("fullMessage", exceptionMessage);
    }
}
