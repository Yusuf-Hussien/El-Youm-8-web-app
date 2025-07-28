package phi.elyoum8.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import phi.elyoum8.controller.ApiResponseWrapper;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleStudentNotFound(StudentNotFoundException ex) {
        return new ResponseEntity<>(ApiResponseWrapper.error(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseWrapper<?>>handleIllegalArgument(IllegalArgumentException ex)
    {
        return new ResponseEntity<>(ApiResponseWrapper.error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseWrapper<?>>handleValidationException(MethodArgumentNotValidException ex)
    {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error->error.getField()+": "+error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(ApiResponseWrapper.error(errorMessage), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseWrapper<?>>handleConstraintViolation(ConstraintViolationException ex)
    {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(error->error.getMessage())
                .collect(Collectors.joining(" , "));
        return new ResponseEntity<>(ApiResponseWrapper.error(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper<?>>handleGenericArgument(Exception ex)
    {
        return new ResponseEntity<>(ApiResponseWrapper.error("An Un Expected Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
