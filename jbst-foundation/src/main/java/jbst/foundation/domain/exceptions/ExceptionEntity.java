package jbst.foundation.domain.exceptions;

import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.domain.strings.JbstStrings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;
import static org.springframework.util.StringUtils.capitalize;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class ExceptionEntity {
    private static final String ATTRIBUTE_SHORT_MESSAGE = "shortMessage";
    private static final String ATTRIBUTE_FULL_MESSAGE = "fullMessage";

    private final ExceptionEntityType exceptionEntityType;
    private final Map<String, Object> attributes;
    private final long timestamp;

    public ExceptionEntity(@NotNull ExceptionEntityType exceptionEntityType, Map<String, Object> attributes) {
        this.exceptionEntityType = exceptionEntityType;
        this.attributes = new HashMap<>(attributes);
        this.timestamp = getCurrentTimestamp();
    }

    public ExceptionEntity(ExceptionEntityType exceptionEntityType, String shortMessage, String fullMessage) {
        this(
                exceptionEntityType,
                Map.of(
                        ATTRIBUTE_SHORT_MESSAGE, shortMessage,
                        ATTRIBUTE_FULL_MESSAGE, fullMessage
                )
        );
    }

    public ExceptionEntity(MethodArgumentNotValidException exception) {
        this.exceptionEntityType = ExceptionEntityType.ERROR;
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

    public ExceptionEntity(Exception exception) {
        this(
                ExceptionEntityType.ERROR,
                exception.getMessage(),
                exception.getMessage()
        );
    }

    public void addAttribute(String attributeKey, Object value) {
        this.attributes.put(attributeKey, value);
    }
}
