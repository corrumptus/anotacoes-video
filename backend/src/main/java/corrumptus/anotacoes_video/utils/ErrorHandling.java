package corrumptus.anotacoes_video.utils;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ErrorHandling {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> notFoundHandling(EntityNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> illegalArgumentHandling(IllegalArgumentException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ExceptionResponse> ioHandling(IOException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ExceptionResponse("Video doesnt exists"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> defaultResponse() {
        return ResponseEntity
            .internalServerError()
            .body(new ExceptionResponse("Some Error occured in the middle of the execution"));
    }
}
