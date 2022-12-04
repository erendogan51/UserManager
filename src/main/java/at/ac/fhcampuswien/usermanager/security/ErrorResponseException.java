package at.ac.fhcampuswien.usermanager.security;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.http.HttpStatus;

public class ErrorResponseException extends RuntimeException {
    HttpStatus statusCode;

    public ErrorResponseException(HttpStatus httpStatus, String message) {
        super(message);
        this.statusCode = httpStatus;
    }

    public ErrorResponseException(HttpStatus httpStatus, String message, Object... objects) {
        super(MessageFormatter.arrayFormat(message, objects).getMessage());
        this.statusCode = httpStatus;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
