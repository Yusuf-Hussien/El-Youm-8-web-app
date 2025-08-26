package phi.elyoum8.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import phi.elyoum8.model.Student;
import phi.elyoum8.service.StudentRestService;
import phi.elyoum8.service.StudentService;
import phi.elyoum8.util.dataBinding.NameForm;
import phi.elyoum8.util.dataBinding.SeatNumberForm;
import phi.elyoum8.util.dataBinding.SeatNumbersForm;
import phi.elyoum8.util.generator.StudentPdfGenerator;
import phi.elyoum8.util.validation.CustomValidator;

import java.util.List;

@Controller
@RequestMapping({"natega",""})
public class StudentMvcController {

    private final StudentService studentService;
    private  final StudentPdfGenerator pdfService;

    public StudentMvcController(@Qualifier("studentMvcService") StudentService studentService, StudentPdfGenerator pdfService) {
        this.studentService = studentService;
        this.pdfService = pdfService;
    }




    @GetMapping({"","/","/searchWithSeatNumber"})
    public String showSeatSearch(Model model) {
        model.addAttribute("seatNumberForm", new SeatNumberForm());
        model.addAttribute("activePage", "seat");
        return "searchWithSeatNumber";
    }

    @PostMapping("/searchWithSeatNumber")
    public String searchBySeat(
            @Valid @ModelAttribute("seatNumberForm")SeatNumberForm seatNumberForm,
            BindingResult result,
            Model model) {

        model.addAttribute("activePage", "seat");
        model.addAttribute("seatNumberForm", seatNumberForm);

        if(!CustomValidator.isValidLong(seatNumberForm.getSeatNumber())) {
            model.addAttribute("error", "دخل رقم صحيح");
        }
        if (result.hasErrors()) {
            model.addAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            return "searchWithSeatNumber";
        }

        Student student = studentService.findBySeatNumber(Long.parseLong(seatNumberForm.getSeatNumber()));
        model.addAttribute("student", student);
        model.addAttribute("error", student == null ? "رقم الجلوس غير صحيح" : null);
        return "searchWithSeatNumber";
    }





    @GetMapping("/searchByName")
    public String showNameSearch(Model model) {
        model.addAttribute("nameForm", new NameForm());
        model.addAttribute("activePage", "name");
        return "searchWithArabicName";
    }

    @PostMapping("/searchByName")
    public String searchByName(@Valid @ModelAttribute("nameForm") NameForm nameForm, BindingResult result, Model model) {

        model.addAttribute("activePage", "name");
        model.addAttribute("nameForm", nameForm);

        if (result.hasErrors()) {
            model.addAttribute("error",
                    result.getAllErrors().get(0).getDefaultMessage());
            return "searchWithArabicName";
        }

        List<Student> students = studentService.findByName(
                nameForm.getText(),
                nameForm.getIsMidName()? true : nameForm.getSpellCheck(),
                nameForm.getIsMidName()
        );

        if (students != null && students.size() > 1000)
            model.addAttribute("error", " البحث رجع اكتر من 1000 طالب، ضيق نطاق البحث.");
        else {
            if(students == null || students.isEmpty())
                model.addAttribute("error","لايوجد طالب بهذا الاسم!");
            else
                model.addAttribute("students", students);
        }

        return "searchWithArabicName";
    }





    @GetMapping("/listAll")
    public String showListAll(Model model) {
        Page<Student> firstPage = studentService.findAll(0, 30);
        model.addAttribute("students", firstPage.getContent());
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", firstPage.getTotalPages());
        model.addAttribute("activePage", "all");
        return "listAll";
    }

    @PostMapping("/listAll")
    public String listAll(Model model, @ModelAttribute("currentPage") String pageStr) {
        model.addAttribute("activePage", "all");
        Page<Student> firstPage = studentService.findAll(0, 30);
        int totalPages = firstPage.getTotalPages();
        model.addAttribute("totalPages", firstPage.getTotalPages());

        Integer page=1 ;
        try {
            page = Integer.parseInt(pageStr);
        }catch (NumberFormatException e) {
            model.addAttribute("error", "رقم الصفحة أكبر من عدد الصفحات المتاح بكتير جدااااااااااا");
            model.addAttribute("currentPage", page);
            return "listAll";
        }

        if (page < 1) {
            model.addAttribute("error", "رقم الصفحة لازم يكون موجب");
            model.addAttribute("currentPage", page);
            return "listAll";
        }

        if (page > totalPages && totalPages > 0) {
            model.addAttribute("error", "رقم الصفحة أكبر من عدد الصفحات المتاح");
            model.addAttribute("currentPage", page);
            return "listAll";
        }
        Page<Student> studentPage = studentService.findAll(page-1, 30);
        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        return "listAll";
    }





    @GetMapping("/searchByRange")
    public String showRangeSearch(Model model) {
        model.addAttribute("seatNumbers", new SeatNumbersForm());
        model.addAttribute("activePage", "range");
        return "searchByRange";
    }


    @PostMapping("/searchByRange")
    public String searchByRange(
            @Valid @ModelAttribute("seatNumbers") SeatNumbersForm seatNumbers,
            BindingResult result,
            Model model)
    {

        model.addAttribute("activePage", "range");
        model.addAttribute("seatNumbers", seatNumbers);

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(err -> err.getDefaultMessage())
                    .toList();

            model.addAttribute("errors", errors);
            return "searchByRange";
        }


        Long startSeatNumber = Long.parseLong(seatNumbers.getStartSeatNumber());
        Long endSeatNumber = Long.parseLong(seatNumbers.getEndSeatNumber());

        if (endSeatNumber-startSeatNumber+1 > 1000) {
            model.addAttribute("errors", " البحث رجع اكتر من 1000 طالب، ضيق نطاق البحث.");
            return "searchByRange";
        }

        List<Student>students = studentService.findBySeatNumberRange(startSeatNumber, endSeatNumber);
        model.addAttribute("students", students);
        model.addAttribute("errors",students ==null || students.isEmpty() ? "لا يوجد طلاب بهذه الأرقام":null );
        model.addAttribute("seatNumbers", seatNumbers);
        return "searchByRange";
    }





    @PostMapping("/showStudentDetails")
    public String showStudentDetails(
            @ModelAttribute("seatNumberForm") Student student,
            Model model) {

        model.addAttribute("student", student);

        return "single-student-result";
    }


    @PostMapping("/student/downloadPdf")
    public ResponseEntity<byte[]> downloadStudentPdf(@ModelAttribute Student student) {
        try {
            byte[] pdfBytes = pdfService.generatePdf(student);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("student_" + student.getSeatNumber() + "_result.pdf")
                    .build());
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/students/downloadPdf")
    public ResponseEntity<byte[]> downloadBulkStudentsPdf(@RequestParam("studentData") String studentDataJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Student> students = objectMapper.readValue(studentDataJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Student.class));

            byte[] pdfBytes = pdfService.generatePdf(students);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("students_results_bulk.pdf")
                    .build());
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}