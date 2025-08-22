package phi.elyoum8.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import phi.elyoum8.controller.ApiResponseWrapper;

import java.util.stream.Collectors;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalRestExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleStudentNotFound(StudentNotFoundException ex) {
        return new ResponseEntity<>(ApiResponseWrapper.error(ex.getMessage()), HttpStatus.CREATED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseWrapper<?>>handleIllegalArgument(IllegalArgumentException ex)
    {
        return new ResponseEntity<>(ApiResponseWrapper.error(ex.getMessage()), HttpStatus.CREATED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseWrapper<?>>handleValidationException(MethodArgumentNotValidException ex)
    {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error->error.getField()+": "+error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(ApiResponseWrapper.error(errorMessage), HttpStatus.CREATED);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseWrapper<?>>handleConstraintViolation(ConstraintViolationException ex)
    {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(error->error.getMessage())
                .collect(Collectors.joining(" , "));
        return new ResponseEntity<>(ApiResponseWrapper.error(errorMessage), HttpStatus.CREATED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseWrapper<?>>handleWrongArgType(MethodArgumentTypeMismatchException ex)
    {
        String variableName = ex.getName();
        String requiredType = ex.getRequiredType().getSimpleName();
        String invalidType = ex.getValue().getClass().getSimpleName();
        String invalidValue = ex.getValue().toString();

        String errorMessage = String.format("the field '%s' must be of type '%s', not of type '%s' as you provided '%s'",
                variableName,
                requiredType,
                invalidType,
                invalidValue
        );
        return new ResponseEntity<>(ApiResponseWrapper.error(errorMessage), HttpStatus.CREATED);
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleNotFound(NoHandlerFoundException ex) {
       ApiResponseWrapper<?> response =  ApiResponseWrapper
               .error("The requested route '" + ex.getRequestURL() + "' doesn't exist");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ApiResponseWrapper.error(ex.getMessage()),HttpStatus.CREATED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper<?>>handleGenericException(Exception ex)
    {
        return new ResponseEntity<>(ApiResponseWrapper.error("An Un Expected Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
