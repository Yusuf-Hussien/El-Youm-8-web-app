package phi.elyoum8.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import phi.elyoum8.model.Student;
import phi.elyoum8.repository.StudentRepository;
import phi.elyoum8.util.genric.NameVariationsGenerator;
import phi.elyoum8.util.validation.CustomValidator;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class StudentService {

    private final StudentRepository studentRepository;


    public abstract Student findBySeatNumber(Long seatNumber) ;


    public abstract List<Student> findBySeatNumberRange(Long startSeat, Long endSeat) ;


    public Page<Student> findAll(Integer page, Integer size) {
        return studentRepository.findAllByOrderByPercentageDescArabicNameAsc(PageRequest.of(page,size));
    }

    public List<Student> findByName(String name,Boolean spellCheck,Boolean isMidName) {
        if(!CustomValidator.isValidArabicName(name))
            throw new RuntimeException("Invalid input it's not an arabic name");
        return
                spellCheck ?
                findByNameUsingSpillCheck(name,isMidName) :
                findByNameIgnoreSpillCheck(name,isMidName);
    }

    protected List<Student> findByNameUsingSpillCheck(String name,Boolean isMidName) {
        return
                isMidName ?
                studentRepository.findAllStudentsByNameContains(name):
                studentRepository.findAllStudentsByNameStartsWith(name);
    }

    protected List<Student> findByNameIgnoreSpillCheck(String name,Boolean isMidName) {
        List<String>variations = NameVariationsGenerator.generateNameVariations(name);
        List<Student>results = new ArrayList<>();
        for (String variation : variations)
            results.addAll(
                    isMidName?
                            studentRepository.findAllStudentsByNameContains(variation):
                            studentRepository.findAllStudentsByNameStartsWith(variation));
        return results.stream().distinct().collect(Collectors.toList());
    }


}