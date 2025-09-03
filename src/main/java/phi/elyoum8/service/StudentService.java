package phi.elyoum8.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import phi.elyoum8.model.Student;
import phi.elyoum8.repository.StudentRepository;
import phi.elyoum8.util.genric.ArabicNameNormalizer;
import phi.elyoum8.util.validation.CustomValidator;

import java.util.*;

public abstract class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public abstract Student findBySeatNumber(Long seatNumber) ;


    public abstract List<Student> findBySeatNumberRange(Long startSeat, Long endSeat) ;


    public Page<Student> findAll(Integer page, Integer size) {
        return studentRepository.findAllByOrderByPercentageDescArabicNameAsc(PageRequest.of(page,size));
    }

    public List<Student> findByName(String name,Boolean spellCheck,Boolean isMidName) {
        if(!CustomValidator.isValidArabicName(name))
            throw new RuntimeException("Invalid input it's not an arabic name");
        if(!spellCheck) return findByNormalizedName(name,isMidName);
        return
                isMidName ?
                        studentRepository.findAllStudentsByNameContains(name):
                        studentRepository.findAllStudentsByNameStartsWith(name);
    }

    protected List<Student> findByNormalizedName(String name,Boolean isMidName) {
        name = ArabicNameNormalizer.normalize(name);
        return
                isMidName ?
                        studentRepository.findAllStudentsByNormalizedNameContains(name):
                        studentRepository.findAllStudentsByNormalizedNameStartsWith(name);
    }

}