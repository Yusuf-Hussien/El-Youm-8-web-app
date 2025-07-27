package phi.elyoum8.controller;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phi.elyoum8.model.Student;
import phi.elyoum8.repository.StudentRepository;
import phi.elyoum8.service.StudentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/students")
@RequiredArgsConstructor
public class StudentRestController {
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(@PathVariable long id)
    {
        Student student = studentService.findBySeatNumber(id);
        if(student!=null) return new ResponseEntity<>(student, HttpStatusCode.valueOf(200));
       return new ResponseEntity<>(Map.of("message","no student with seat number "+id+ " exists"), HttpStatusCode.valueOf(404));
    }


    @GetMapping()
    public List<Student>getAllByName(
            @RequestParam @NotBlank(message = "Name cannot be blank") String name,
            @RequestParam(defaultValue="false") Boolean spellCheck)
    {
        return spellCheck?studentService.findByName(name):studentService.findByNameIgnoreSpillCheck(name);
    }


    @GetMapping("/all")
    public List<Student>getAllSorted(
            @RequestParam(defaultValue = "0") @Min(value = 1,message = "Page must be non-negative") Integer page ,
            @RequestParam(defaultValue = "100") @Min(value = 1,message = "Size must be at least 1") Integer size )
    {
        return studentService.findAll(page,size).getContent();
    }

    @GetMapping("/ranged")
    public List<Student>getAllBetween(
            @RequestParam @Positive(message = " 'from' must be positive") Long from ,
            @RequestParam @Positive(message = " 'to' must be positive") Long to)
    {
        return studentService.findBySeatNumberRange(from,to);
    }
}
