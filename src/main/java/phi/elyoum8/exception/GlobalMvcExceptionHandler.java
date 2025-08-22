package phi.elyoum8.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(annotations = Controller.class)
public class GlobalMvcExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // or HttpStatus.OK
    public String handleNumberFormating(NumberFormatException ex, Model model) {
        model.addAttribute("error", " انت دخلت رقم جلوس اكبر من المتاحين بكتيييييييييييييير");
        return "error";
    }




    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("error", "الصفحة المطلوبة '" + ex.getRequestURL() + "' غير موجودة.");
        model.addAttribute("errorCode", "ERR404");
        return "error";
    }

    @ExceptionHandler({
            TemplateProcessingException.class,
            SpelEvaluationException.class,
            SpelParseException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleTemplateErrors(Exception ex, Model model) {
        model.addAttribute("error", "حدث خطأ في معالجة الصفحة. الرجاء التحقق من الرابط.");
        model.addAttribute("errorCode", "ERR500");
        return "error";
    }

    @ExceptionHandler({
            Exception.class,
            RuntimeException.class
    })    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericError(Exception ex, Model model) {
        model.addAttribute("error", "حدث خطأ غير متوقع. الرجاء التأكد من صحة الرابط أو المحاولة لاحقًا.");
        model.addAttribute("errorCode", "ERR500");
        return "error";
    }
}