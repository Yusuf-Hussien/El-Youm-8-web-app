package phi.elyoum8.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import phi.elyoum8.model.Student;
import phi.elyoum8.service.StudentService;
import phi.elyoum8.util.dataBinding.FormData;
import phi.elyoum8.util.dataBinding.NameForm;
import phi.elyoum8.util.dataBinding.SeatNumberForm;
import phi.elyoum8.util.dataBinding.SeatNumbersForm;

import java.util.List;

@Controller
@RequestMapping("natega")
@RequiredArgsConstructor
public class StudentMVCController {
    private final StudentService studentService;

    @GetMapping({"","/","/searchWithSeatNumber"})
    public String showSeatSearch(Model model) {
        model.addAttribute("seatNumberForm", new SeatNumberForm());
        model.addAttribute("activePage", "seat");
        return "searchWithSeatNumber";
    }

    @PostMapping("/searchWithSeatNumber")
    public String searchBySeat(@ModelAttribute("seatNumberForm")SeatNumberForm seatNumberForm, Model model) {
        Student student = studentService.findBySeatNumber(seatNumberForm.getSeatNumber());
        model.addAttribute("student", student);
        model.addAttribute("error", student == null ? "رقم الجلوس غير صحيح" : null);
        model.addAttribute("seatNumberForm", seatNumberForm);
        model.addAttribute("activePage", "seat");
        return "searchWithSeatNumber";
    }

    @GetMapping("/searchByName")
    public String showNameSearch(Model model) {
        model.addAttribute("formData", new NameForm());
        model.addAttribute("activePage", "name");
        return "searchWithArabicName";
    }

    @PostMapping("/searchByName")
    public String searchByName(@ModelAttribute("formData") NameForm formData, Model model) {
        List<Student> students = formData.getSpellCheck() ?
                 studentService.findByName(formData.getText())
                :studentService.findByNameIgnoreSpillCheck(formData.getText());
        model.addAttribute("students", students);
        model.addAttribute("error", students == null || students.isEmpty() ? "لايوجد طالب بهذا الاسم!" : null);
        model.addAttribute("formData", formData);
        model.addAttribute("activePage", "name");
        return "searchWithArabicName";
    }

    @GetMapping("/listAll")
    public String listAll(Model model, @RequestParam(defaultValue = "0") Integer page) {
        Page<Student> studentPage = studentService.findAll(page, 20);
        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("activePage", "all");
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
            @ModelAttribute("seatNumbers") SeatNumbersForm seatNumbers
            , Model model)
    {
        List<Student>students = studentService.findBySeatNumberRange(
                seatNumbers.getStartSeatNumber(),
                seatNumbers.getEndSeatNumber()
        );
        model.addAttribute("students", students);
        model.addAttribute("error",students ==null || students.isEmpty() ? "لا يوجد طلاب بهذه الأرقام":null );
        model.addAttribute("seatNumbers", seatNumbers);
        model.addAttribute("activePage", "range");
        return "searchByRange";
    }
}