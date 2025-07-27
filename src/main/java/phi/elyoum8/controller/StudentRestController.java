package phi.elyoum8.controller;


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
@RequestMapping("${routes.api.base}"+"students")
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
    public List<Student>getAllByName(@RequestParam String name,@RequestParam(defaultValue="false") Boolean spellCheck)
    {
        return spellCheck?studentService.findByName(name):studentService.findByNameIgnoreSpillCheck(name);
    }


    @GetMapping("/sorted")
    public List<Student>getAllSorted(@RequestParam Integer page , @RequestParam Integer size )
    {
        return studentService.findAll(page,size).getContent();
    }

    @GetMapping("/part")
    public List<Student>getAllBetween(@RequestParam Long from , @RequestParam Long to)
    {
        return studentService.findBySeatNumberRange(from,to);
    }
}
