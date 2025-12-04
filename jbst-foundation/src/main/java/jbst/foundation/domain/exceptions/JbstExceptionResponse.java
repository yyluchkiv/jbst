package jbst.foundation.domain.exceptions;

import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.strings.JbstStrings;
import jbst.foundation.domain.tuples.Tuple2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;
import static org.springframework.util.StringUtils.capitalize;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class JbstExceptionResponse {
    private static final String ATTRIBUTE_SHORT_MESSAGE = "shortMessage";
    private static final String ATTRIBUTE_FULL_MESSAGE = "fullMessage";
    // TODO [YYL] add new V2 attributes, avoid migration issues

    private final Type exceptionEntityType;
    private final Map<String, Object> attributes;
    private final long timestamp;

    public JbstExceptionResponse(@NotNull JbstExceptionResponse.Type type, Map<String, Object> attributes) {
        this.exceptionEntityType = type;
        this.attributes = new HashMap<>(attributes);
        this.timestamp = getCurrentTimestamp();
    }

    public JbstExceptionResponse(Type type, String shortMessage, String fullMessage) {
        this(
                type,
                Map.of(
                        ATTRIBUTE_SHORT_MESSAGE, shortMessage,
                        ATTRIBUTE_FULL_MESSAGE, fullMessage
                )
        );
    }

    public JbstExceptionResponse(MethodArgumentNotValidException exception) {
        this.exceptionEntityType = Type.ERROR;
        var message = exception.getBindingResult().getFieldErrors().stream()
                .map(item -> {
                    // E.G. "bollingerBands.numberOfPeriods" -> "Bollinger bands Number of periods"
                    var fieldName = Stream.of(item.getField().split("\\."))
                            .map(JbstStrings::convertCamelCaseToSplit)
                            .collect(Collectors.joining(" "));
                    // E.G: "Bollinger bands Number of periods" → "Bollinger bands number of periods"
                    fieldName = capitalize(fieldName.toLowerCase());
                    return new Tuple2<>(fieldName, item.getDefaultMessage());
                })
                .map(tuple2 -> tuple2.a() + " " + tuple2.b())
                .sorted()
                .collect(Collectors.joining(". "));
        this.attributes = Map.of(
                ATTRIBUTE_SHORT_MESSAGE, message,
                ATTRIBUTE_FULL_MESSAGE, message
        );
        this.timestamp = getCurrentTimestamp();
    }

    public JbstExceptionResponse(Exception exception) {
        this(
                Type.ERROR,
                exception.getMessage(),
                exception.getMessage()
        );
    }

    public void addAttribute(String attributeKey, Object value) {
        this.attributes.put(attributeKey, value);
    }

    // =================================================================================================================
    // CLASSES
    // =================================================================================================================
    public enum Type {
        PARTIALLY, WARNING, ERROR
    }
}
