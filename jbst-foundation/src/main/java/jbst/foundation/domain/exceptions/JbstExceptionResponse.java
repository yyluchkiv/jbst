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

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;
import static org.springframework.util.StringUtils.capitalize;

@Getter
@EqualsAndHashCode
@ToString
public class JbstExceptionResponse {
    private final long jbsTimestamp;
    private final Type jbstType;
    private final String jbstMessageOnClient;
    private final Map<String, Object> jbstAttributes;

    public JbstExceptionResponse(
            @NotNull JbstExceptionResponse.Type type,
            @NotNull String jbstMessageOnClient,
            @NotNull Exception exception,
            @NotNull Map<String, Object> attributes
    ) {
        Map<String, Object> res = new HashMap<>(attributes);
        if (nonNull(exception) && nonNull(exception.getMessage())) {
            res.put(Constants.TRACE, exception.getMessage());
        } else {
            res.put(Constants.TRACE, "No trace found");
        }
        // V2
        this.jbsTimestamp = getCurrentTimestamp();
        this.jbstType = type;
        this.jbstMessageOnClient = jbstMessageOnClient;
        this.jbstAttributes = unmodifiableMap(res);
    }

    public JbstExceptionResponse(@NotNull JbstExceptionResponse.Type type, @NotNull String jbstMessageOnClient, @NotNull Exception exception) {
        this(type, jbstMessageOnClient, exception, new HashMap<>());
    }

    public static JbstExceptionResponse of(@NotNull JbstExceptionResponse.Type type, @NotNull Exception exception) {
        var jbstMessageOnClient = nonNull(exception) && nonNull(exception.getMessage()) ? exception.getMessage() : "No message found";
        return new JbstExceptionResponse(type, jbstMessageOnClient, exception);
    }

    public static JbstExceptionResponse of(@NotNull MethodArgumentNotValidException exception) {
        var message = exception.getBindingResult().getFieldErrors().stream()
                .map(item -> {
                    var fieldName = Stream.of(item.getField().split("\\."))
                            .map(JbstStrings::convertCamelCaseToSplit)
                            .collect(Collectors.joining(" "));
                    fieldName = capitalize(fieldName.toLowerCase());
                    return new Tuple2<>(fieldName, item.getDefaultMessage());
                })
                .map(tuple2 -> tuple2.a() + " " + tuple2.b())
                .sorted()
                .collect(Collectors.joining(". "));
        return new JbstExceptionResponse(Type.ERROR, message, exception);
    }

    // =================================================================================================================
    // CLASSES
    // =================================================================================================================
    public static class Constants {
        public static final String TRACE = "jbstTrace";
    }

    public enum Type {
        PARTIALLY, WARNING, ERROR
    }
}
