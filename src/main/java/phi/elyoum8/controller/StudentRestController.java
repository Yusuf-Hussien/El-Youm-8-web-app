package phi.elyoum8.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import phi.elyoum8.repository.StudentRepository;
import phi.elyoum8.service.StudentService;

@RestController
@RequestMapping("api/v1/students")
@RequiredArgsConstructor
@Validated
public class StudentRestController {
    private final StudentRepository studentRepository;
    private final StudentService studentService;



    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<?>> getStudent(@PathVariable long id)
    {
        return ResponseEntity.ok(ApiResponseWrapper.success(studentService.findBySeatNumber(id)));
    }


    @GetMapping()
    public ResponseEntity<ApiResponseWrapper<?>>getAllByName(
            @RequestParam @NotBlank(message = "Name cannot be blank") String name,
            @RequestParam(defaultValue="false") Boolean spellCheck)
    {
        return spellCheck? ResponseEntity.ok(ApiResponseWrapper.success(studentService.findByName(name)))
                : ResponseEntity.ok(ApiResponseWrapper.success(studentService.findByNameIgnoreSpillCheck(name)));
    }


    @GetMapping("/all")
    public ResponseEntity<ApiResponseWrapper<?>>getAllSorted(
            @RequestParam(defaultValue = "0") @Min(value = 1,message = "Page must be non-negative") Integer page ,
            @RequestParam(defaultValue = "100") @Min(value = 1,message = "Size must be at least 1") Integer size )
    {
        return ResponseEntity.ok(ApiResponseWrapper.success(studentService.findAll(page,size).getContent()));
    }

    @GetMapping("/ranged")
    public ResponseEntity<ApiResponseWrapper<?>>getAllBetween(
            @RequestParam @Positive(message = " 'from' must be positive") Long from ,
            @RequestParam @Positive(message = " 'to' must be positive") Long to)
    {
        return ResponseEntity.ok(ApiResponseWrapper.success(studentService.findBySeatNumberRange(from,to)));
    }
}
