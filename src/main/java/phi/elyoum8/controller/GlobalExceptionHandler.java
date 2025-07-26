package phi.elyoum8.controller;

import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.thymeleaf.exceptions.TemplateProcessingException;
import phi.elyoum8.util.dataBinding.FormData;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({TemplateProcessingException.class, SpelEvaluationException.class})
    public String handleNotFound(Exception ex, Model model)
    {
        model.addAttribute("errorMessage","no student with this seat number exists");
        model.addAttribute("formData",new FormData());
        return "home-view";
    }
}
