package phi.elyoum8.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import phi.elyoum8.exception.StudentNotFoundException;
import phi.elyoum8.model.Student;
import phi.elyoum8.repository.StudentRepository;
import phi.elyoum8.util.genric.ArabicNameNormalizer;
import phi.elyoum8.util.validation.CustomValidator;

import java.util.List;

@Service("studentRestService")
public class StudentRestService extends StudentService{

    private final StudentRepository studentRepository;

    @Value("${students.response.size.max}")
    private Long MAX_STUDENTS_SIZE;

    public StudentRestService(StudentRepository studentRepository) {
        super(studentRepository);
        this.studentRepository = studentRepository;
    }


    @Override
    public Student findBySeatNumber(Long seatNumber){
        return studentRepository.findBySeatNumber(seatNumber)
                .orElseThrow(() -> new StudentNotFoundException("No student with seat number '" + seatNumber + "' exists")
                );
    }


    @Override
    public List<Student> findByName(String name,Boolean spellCheck,Boolean isMidName) {
        if(!CustomValidator.isValidArabicName(name))
            throw new RuntimeException("Invalid input it's not an arabic name");

        if(!spellCheck) name = ArabicNameNormalizer.normalize(name);
        var students = isMidName ?
                        studentRepository.findAllStudentsByNameContains(name):
                        studentRepository.findAllStudentsByNameStartsWith(name);


        if(students.isEmpty()) throw new StudentNotFoundException("No student with name '" + name + "' exists");

        if(students.size() > MAX_STUDENTS_SIZE) throw new StudentNotFoundException("You have requested more than '"+ students.size() +"' student, Maximum allowed students size per request is " + MAX_STUDENTS_SIZE);

        return students;
    }


    @Override
    public List<Student> findBySeatNumberRange(Long startSeat, Long endSeat) {
        List<Student>students = studentRepository.findAllBetween(Math.min(startSeat,endSeat),Math.max(startSeat,endSeat));

        if(students.isEmpty()) throw new StudentNotFoundException("No Students between " + startSeat + " and " + endSeat + " exists");
        if(students.size() > MAX_STUDENTS_SIZE) throw new StudentNotFoundException("You have requested more than '"+ students.size() +"' student, Maximum allowed students size per request is " + MAX_STUDENTS_SIZE);

        return studentRepository.findAllBetween(Math.min(startSeat,endSeat),Math.max(startSeat,endSeat));
    }


    @Override
    public Page<Student> findAll(Integer page, Integer size) {
        if(size > MAX_STUDENTS_SIZE) throw new StudentNotFoundException("You have requested more than '"+ size +"' student, Maximum allowed allowed students size per page is " + MAX_STUDENTS_SIZE);

        return studentRepository.findAllByOrderByPercentageDescArabicNameAsc(PageRequest.of(page,size));
    }


}
