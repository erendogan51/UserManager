package at.ac.fhcampuswien.usermanager.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorResponseHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorResponseHandler.class);

    @ExceptionHandler(value = {ErrorResponseException.class})
    protected ResponseEntity<usermanager.v1.model.Response> errorResponseExceptionHandler(
            ErrorResponseException ex, WebRequest request) {
        if ((ex.getStatusCode().equals(HttpStatus.FORBIDDEN))) {
            log.warn("Request denied: {}", ex.getMessage());
        }
        return ResponseEntity.status(ex.getStatusCode())
                .body(new usermanager.v1.model.Response().message(ex.getMessage()));
    }

    @ExceptionHandler(value = {ResponseStatusException.class})
    protected ResponseEntity<usermanager.v1.model.Response> responseStatusExceptionHandler(
            ResponseStatusException ex, WebRequest request) {
        if ((ex.getStatus().equals(HttpStatus.FORBIDDEN))) {
            log.warn("Request denied: {}", ex.getMessage());
        }
        return ResponseEntity.status(ex.getStatus())
                .body(new usermanager.v1.model.Response().message(ex.getMessage()));
    }
}
