package phi.elyoum8.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalMvcExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // or HttpStatus.OK
    public String handleNumberFormating(NumberFormatException ex, Model model) {
        model.addAttribute("error", " انت دخلت رقم جلوس اكبر من المتاحين بكتيييييييييييييير");
        return "error";
    }

    @ExceptionHandler({
            TemplateProcessingException.class,
            SpelEvaluationException.class,
            SpelParseException.class,
            Exception.class,
            RuntimeException.class
    })
    @ResponseStatus(HttpStatus.OK) // show error page but not 500
    public String handleGenericError(Exception ex, Model model) {
       // if(ex.getMessage()!="No static resource favicon.ico.")System.out.println("Caught exception: " + ex.getMessage());
        model.addAttribute("error", "حدث خطأ: اتأكد ان الرابط بتاعك يحتوي على 'natega/' وان مفيش حاجه بعدها" );
        return "error";
    }
}