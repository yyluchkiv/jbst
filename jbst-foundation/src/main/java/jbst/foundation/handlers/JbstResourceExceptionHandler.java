package jbst.foundation.handlers;

import jbst.foundation.domain.exceptions.JbstExceptionResponse;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.strings.JbstMessages.contactDevelopmentTeam;
import static jbst.foundation.domain.strings.JbstMessages.unexpectedErrorOccurred;

// WARNING: @Order by default uses Ordered.LOWEST_PRECEDENCE
@Slf4j
@Order
@ControllerAdvice
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstResourceExceptionHandler {

    private final JbstIncidentsPublisher incidentsPublisher;

    // =================================================================================================================
    // DEDICATED EXCEPTIONS
    // =================================================================================================================
    @ExceptionHandler({
            JbstExceptions.Login.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<JbstExceptionResponse> handleLoginException(JbstExceptions.Login ex) {
        var response = new JbstExceptionResponse(
                JbstExceptionResponse.Type.ERROR,
                ex.getMessage(),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            JbstExceptions.Registration.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<JbstExceptionResponse> registerException(JbstExceptions.Registration ex) {
        var response = new JbstExceptionResponse(
                JbstExceptionResponse.Type.ERROR,
                contactDevelopmentTeam("Registration Failure"),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            JbstExceptions.UserTokenValidation.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<JbstExceptionResponse> userEmailTokenValidationException(JbstExceptions.UserTokenValidation ex) {
        var response = new JbstExceptionResponse(
                JbstExceptionResponse.Type.ERROR,
                contactDevelopmentTeam("Token Validation Failure"),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<JbstExceptionResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(new JbstExceptionResponse(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            JbstExceptions.TooManyRequests.class
    })
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseEntity<JbstExceptionResponse> tooManyRequestsException(JbstExceptions.TooManyRequests ignoredEx) {
        var response = new JbstExceptionResponse(
                JbstExceptionResponse.Type.ERROR,
                "Too many requests, please wait",
                "Too many requests, please wait"
        );
        return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
    }

    // =================================================================================================================
    // GROUPED EXCEPTIONS
    // =================================================================================================================

    @ExceptionHandler({
            JbstExceptions.CookieNotFound.class,
            JbstExceptions.AccessTokenNotFound.class,
            JbstExceptions.AccessTokenInvalid.class,
            JbstExceptions.AccessTokenExpired.class,
            JbstExceptions.AccessTokenDbNotFound.class,
            JbstExceptions.RefreshTokenNotFound.class,
            JbstExceptions.RefreshTokenInvalid.class,
            JbstExceptions.RefreshTokenExpired.class,
            JbstExceptions.RefreshTokenDbNotFound.class,
            JbstExceptions.Unauthorized.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<JbstExceptionResponse> unauthorizedExceptions(Exception ex) {
        return new ResponseEntity<>(new JbstExceptionResponse(ex), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            AccessDeniedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<JbstExceptionResponse> forbiddenExceptions(Exception ex) {
        return new ResponseEntity<>(new JbstExceptionResponse(ex), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            HttpMessageConversionException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<JbstExceptionResponse> badRequestExceptions(Exception ex) {
        var response = new JbstExceptionResponse(
                JbstExceptionResponse.Type.ERROR,
                contactDevelopmentTeam("Malformed request syntax"),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            IllegalArgumentException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<JbstExceptionResponse> internalServerError(Exception ex) {
        return new ResponseEntity<>(new JbstExceptionResponse(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            Exception.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<JbstExceptionResponse> generalException(Exception ex) {
        if (isNull(ex) || isNull(ex.getMessage())) {
            var response = new JbstExceptionResponse(
                    JbstExceptionResponse.Type.ERROR,
                    unexpectedErrorOccurred(),
                    unexpectedErrorOccurred()
            );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            LOGGER.error("Unexpected error occurred", ex);
            var response = new JbstExceptionResponse(
                    JbstExceptionResponse.Type.ERROR,
                    unexpectedErrorOccurred(),
                    ex.getMessage()
            );
            this.incidentsPublisher.publishThrowable(ex);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
