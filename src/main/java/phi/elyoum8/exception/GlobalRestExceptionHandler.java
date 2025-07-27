package phi.elyoum8.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Map<?,?> handleNotFound(Exception ex)
    {
        return Map.of("message","no student with this seat number exists");
    }
}
