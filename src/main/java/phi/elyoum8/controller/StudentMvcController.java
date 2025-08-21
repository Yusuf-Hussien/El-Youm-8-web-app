package phi.elyoum8.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
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
import phi.elyoum8.util.validation.CustomValidator;

import java.util.List;

@Controller
@RequestMapping({"natega"})
public class StudentMvcController {

    private final StudentService studentService;

    public StudentMvcController(@Qualifier("studentMvcService") StudentService studentService) {
        this.studentService = studentService;
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
    public String listAll(Model model, @ModelAttribute("currentPage") Integer page) {
        model.addAttribute("activePage", "all");
        Page<Student> firstPage = studentService.findAll(0, 30);
        int totalPages = firstPage.getTotalPages();

        if (page < 1) {
            model.addAttribute("error", "رقم الصفحة لازم يكون موجب");
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            return "listAll";
        }

        if (page > totalPages && totalPages > 0) {
            model.addAttribute("error", "رقم الصفحة أكبر من عدد الصفحات المتاح");
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
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

        if (startSeatNumber-endSeatNumber+1 > 1000) {
            model.addAttribute("error", " البحث أرجع أكثر من 1000 طالب، يرجى تضييق نطاق البحث.");
            return "searchByRange";
        }

        List<Student>students = studentService.findBySeatNumberRange(startSeatNumber, endSeatNumber);
        model.addAttribute("students", students);
        model.addAttribute("error",students ==null || students.isEmpty() ? "لا يوجد طلاب بهذه الأرقام":null );
        model.addAttribute("seatNumbers", seatNumbers);
        return "searchByRange";
    }

}