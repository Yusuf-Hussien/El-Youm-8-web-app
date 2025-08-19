package phi.elyoum8.util.generator;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import phi.elyoum8.model.Student;

import static org.junit.jupiter.api.Assertions.*;

class HtmlGeneratorTest {

    @Test
    void should_generate_html() throws Exception {
        // 1. Configure Thymeleaf template resolver
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        // 2. Set up SpringTemplateEngine
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        // 3. Prepare test data
        Student student = new Student();
        student.setSeatNumber(101L);
        student.setArabicName("يوسف حسين");
        student.setTotalDegree(120);
        student.setPercentage(4.040);
        student.setStudentRank(1L);
        student.setRankWithDuplicates(598L);

        // 4. Create Spring WebContext with mock objects
//        MockServletContext servletContext = new MockServletContext();
//        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        WebContext context = new WebContext(request, response, servletContext);
//        context.setVariable("student", student);
//
//        // 5. Process template
//        String finalHtml = templateEngine.process("student_template", context);
//
//        // 6. Output or validate HTML
//        System.out.println(finalHtml);
    }
}