package phi.elyoum8.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phi.elyoum8.model.Student;
import phi.elyoum8.service.StudentService;
import phi.elyoum8.util.generator.HtmlGenerator;
import phi.elyoum8.util.generator.StudentPdfGenerator;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("api/v1/students")
@Validated
@Tag(name = "El-Youm-8 'the 8th day' API", description = "API for managing Egyptian students secondary school 'الثانوية العامة' results")
public class StudentRestController {


    private final HtmlGenerator htmlGenerator;
    private final StudentPdfGenerator studentPdfGenerator;
    private final StudentService studentService;

    public StudentRestController(@Qualifier("studentRestService") StudentService studentService, HtmlGenerator htmlGenerator,StudentPdfGenerator studentPdfGenerator) {
        this.studentService = studentService;
        this.htmlGenerator = htmlGenerator;
        this.studentPdfGenerator = studentPdfGenerator;
    }


    @Operation(summary = "Get student by seat number", description = "Retrieves a student by their unique seat number.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<?>> getStudent(
            @Parameter(description = "Seat number of the student", required = true)
            @PathVariable @Positive(message = " seat number 'id' must be positive") long id) {
        Student student = studentService.findBySeatNumber(id);
        return ResponseEntity.ok(ApiResponseWrapper.success(student));
    }


    @Operation(summary = "Get students by name", description = "Retrieves students by name with optional spell-check and mid-name search.")
    @GetMapping()
    public ResponseEntity<ApiResponseWrapper<?>> getAllByName(
            @Parameter(description = "Name to search for", required = true)
            @RequestParam @NotBlank(message = "Name cannot be blank") String name,

            @Parameter(description = "Enable spell-checking for name search")
            @RequestParam(defaultValue = "false") Boolean spellCheck,

            @Parameter(description = "Search for name as a substring if true")
            @RequestParam(defaultValue = "false") Boolean isMidName) {
        return ResponseEntity.ok(ApiResponseWrapper.success(studentService.findByName(name, spellCheck, isMidName)));
    }


    @Operation(summary = "Get all students with pagination", description = "Retrieves all students with pagination and sorting descending based on their degree percentage.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponseWrapper<?>> getAllSorted(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be non-negative") Integer page,

            @Parameter(description = "Number of students per page", example = "100")
            @RequestParam(defaultValue = "100") @Min(value = 1, message = "Size must be at least 1") Integer size) {
        return ResponseEntity.ok(ApiResponseWrapper.success(studentService.findAll(page, size).getContent()));
    }


    @Operation(summary = "Get students by seat number range", description = "Retrieves students whose seat numbers fall within the specified range.")
    @GetMapping("/ranged")
    public ResponseEntity<ApiResponseWrapper<?>> getAllBetween(
            @RequestParam @Positive(message = " 'from' must be positive") Long from,
            @RequestParam @Positive(message = " 'to' must be positive") Long to) {
        return ResponseEntity.ok(ApiResponseWrapper.success(studentService.findBySeatNumberRange(from, to)));
    }


    @Operation(summary = "Get HTML page student by seat number", description = "Retrieves a student by their unique seat number in static HTML file")
    @GetMapping("/{id}/html")
    public ResponseEntity<byte[]> getStudentHtml(
            @Parameter(description = "Seat number of the student", required = true)
            @PathVariable @Positive(message = " seat number 'id' must be positive") long id) {
        Student student = studentService.findBySeatNumber(id);
        String html  = htmlGenerator.generateStudentHtml(student,true);
        byte[] data = html.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_" + id + "_result.html")
                .contentType(MediaType.TEXT_HTML)
                .body(data);
    }


    @Operation(summary = "Get PDF for student by seat number", description = "Retrieves a student by their unique seat number as a PDF file")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getStudentPdf(
            @Parameter(description = "Seat number of the student", required = true)
            @PathVariable @Positive(message = " seat number 'id' must be positive") long id) {
        Student student = studentService.findBySeatNumber(id);
        byte[] data = studentPdfGenerator.generatePdf(student);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_" + id + "_result.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @Operation(summary = "Get HTML page students by Arabic Name", description = "Retrieves students by their Arabic Name in static HTML file")
    @GetMapping("/html")
    public ResponseEntity<byte[]> getStudentsHtml(
            @Parameter(description = "Name to search for", required = true)
            @RequestParam @NotBlank(message = "Name cannot be blank") String name,

            @Parameter(description = "Enable spell-checking for name search")
            @RequestParam(defaultValue = "false") Boolean spellCheck,

            @Parameter(description = "Search for name as a substring if true")
            @RequestParam(defaultValue = "false") Boolean isMidName) {
        List<Student> students = studentService.findByName(name,spellCheck,isMidName);
        String html  = htmlGenerator.generateStudentsHtml(students,true);
        byte[] data = html.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+name+ ".html")
                .contentType(MediaType.TEXT_HTML)
                .body(data);
    }


    @Operation(summary = "Get PDF for students by Arabic Name", description = "Retrieves a table of students by their Arabic Name as a PDF file")
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getStudentsPdf(
            @Parameter(description = "Name to search for", required = true)
            @RequestParam @NotBlank(message = "Name cannot be blank") String name,

            @Parameter(description = "Enable spell-checking for name search")
            @RequestParam(defaultValue = "false") Boolean spellCheck,

            @Parameter(description = "Search for name as a substring if true")
            @RequestParam(defaultValue = "false") Boolean isMidName) {
        List<Student> students = studentService.findByName(name,spellCheck,isMidName);
        byte[] data = studentPdfGenerator.generatePdf(students);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+name+".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

}
