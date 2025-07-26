package phi.elyoum8.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.util.Map;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Map<?,?> handleNotFound(Exception ex)
    {
        return Map.of("message","no student with this seat number exists");
    }
}
