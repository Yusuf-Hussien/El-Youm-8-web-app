package phi.elyoum8.controller;


import jakarta.validation.constraints.Future;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phi.elyoum8.model.Student;
import phi.elyoum8.repository.StudentRepository;
import phi.elyoum8.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("${routes.base}"+"students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentRepository studentRepository;


    @GetMapping("/all")
    public List<Student> getStudents()
    {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(@PathVariable long id)
    {
        return new ResponseEntity<>(studentRepository.findById(id), HttpStatusCode.valueOf(200));
    }


    @GetMapping()
    public List<Student>getAllByName(@RequestParam String name)
    {
        return studentRepository.findAllStudentsByNameStartsWith(name);
    }

    @GetMapping("/sorted")
    public List<Student>getAllSorted(@RequestParam Integer page , @RequestParam Integer size )
    {
        return studentRepository.findAllByOrderByPercentageDescArabicNameAsc(PageRequest.of(page,size));
    }

    @GetMapping("/part")
    public List<Student>getAllBetween(@RequestParam Long from , @RequestParam Long to)
    {
        return studentRepository.findAllBetween(from, to);
    }
}
