package jbst.server.ops.handlers.exceptions;

import jbst.foundation.domain.exceptions.JbstExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static jbst.foundation.domain.strings.JbstMessages.contactDevelopmentTeam;
import static jbst.foundation.domain.strings.JbstMessages.unexpectedErrorOccurred;

@Slf4j
@ControllerAdvice
public class ResourceExceptionHandler {

    // =================================================================================================================
    // GROUPED EXCEPTIONS
    // =================================================================================================================
    @ExceptionHandler({
            AccessDeniedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<JbstExceptionResponse> forbiddenExceptions(Exception ex) {
        return new ResponseEntity<>(JbstExceptionResponse.of(JbstExceptionResponse.Type.ERROR, ex), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            HttpMessageConversionException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<JbstExceptionResponse> badRequestExceptions(Exception ex) {
        return new ResponseEntity<>(
                new JbstExceptionResponse(
                        JbstExceptionResponse.Type.ERROR,
                        contactDevelopmentTeam("Malformed request syntax"),
                        ex
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({
            Exception.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<JbstExceptionResponse> generalException(Exception ex) {
        return new ResponseEntity<>(
                new JbstExceptionResponse(
                        JbstExceptionResponse.Type.ERROR,
                        unexpectedErrorOccurred(),
                        ex
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
