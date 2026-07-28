package jbst.foundation.domain.http.requests;

import jakarta.servlet.http.HttpServletRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static java.util.Objects.isNull;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class JbstUserAgentHeader {
    private final String value;

    public static JbstUserAgentHeader fixed() {
        return new JbstUserAgentHeader("Chrome, macOS on Desktop");
    }

    public JbstUserAgentHeader(HttpServletRequest request) {
        if (isNull(request) || isNull(request.getHeader("User-Agent"))) {
            this.value = "";
        } else {
            this.value = request.getHeader("User-Agent");
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private JbstUserAgentHeader(String value) {
        this.value = value;
    }
}
