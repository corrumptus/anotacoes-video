package corrumptus.anotacoes_video.utils.errorhandling;

import java.io.IOException;

import org.bytedeco.javacv.FrameGrabber;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.JWTCreationException;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ErrorHandling {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> bodySerializationHandling(HttpMessageNotReadableException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> notFoundHandling(EntityNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ExceptionResponse> notFoundHandling(EntityExistsException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
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
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse("Problem with the file"));
    }

    @ExceptionHandler(FrameGrabber.Exception.class)
    public ResponseEntity<ExceptionResponse> ffmpegHandling(FrameGrabber.Exception ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse("Problem with the video"));
    }

    @ExceptionHandler(JWTCreationException.class)
    public ResponseEntity<ExceptionResponse> jwtHandling(JWTCreationException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ExceptionResponse("Invalid Token JWT"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> accessDeniedHandling(AccessDeniedException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> notValidHandling(MethodArgumentNotValidException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionResponse(ex.getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> defaultResponse(Exception e) {
        return ResponseEntity
            .internalServerError()
            .body(new ExceptionResponse("Some Error occured in the middle of the execution"));
    }
}
