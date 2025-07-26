package phi.elyoum8.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phi.elyoum8.model.Student;
import phi.elyoum8.repository.StudentRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${routes.api.base}"+"students")
@RequiredArgsConstructor
public class StudentRestController {
    private final StudentRepository studentRepository;


    @GetMapping("/all")
    public List<Student> getStudents()
    {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(@PathVariable long id)
    {
        var student = studentRepository.findById(id);
        if(student.isPresent()) return new ResponseEntity<>(student, HttpStatusCode.valueOf(200));
       return new ResponseEntity<>(Map.of("message","no student with seat number "+id+ " exists"), HttpStatusCode.valueOf(404));
    }


    @GetMapping()
    public List<Student>getAllByName(@RequestParam String name)
    {
        return studentRepository.findAllStudentsByNameStartsWith(name);
    }

    @GetMapping("/sorted")
    public List<Student>getAllSorted(@RequestParam Integer page , @RequestParam Integer size )
    {
        return studentRepository.findAllByOrderByPercentageDescArabicNameAsc(PageRequest.of(page,size)).getContent();
    }

    @GetMapping("/part")
    public List<Student>getAllBetween(@RequestParam Long from , @RequestParam Long to)
    {
        return studentRepository.findAllBetween(from, to);
    }
}
