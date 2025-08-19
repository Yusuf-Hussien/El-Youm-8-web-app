package phi.elyoum8.service;

import org.springframework.stereotype.Service;
import phi.elyoum8.model.Student;
import phi.elyoum8.repository.StudentRepository;

import java.util.List;

@Service("studentMvcService")
public class StudentMvcService extends StudentService{

    private final StudentRepository studentRepository;

    public StudentMvcService(StudentRepository studentRepository) {
        super(studentRepository);
        this.studentRepository = studentRepository;
    }


    @Override
    public Student findBySeatNumber(Long seatNumber) {
        return studentRepository.findBySeatNumber(seatNumber).orElse(null);
    }

    @Override
    public List<Student> findBySeatNumberRange(Long startSeat, Long endSeat) {
        return studentRepository.findAllBetween(Math.min(startSeat,endSeat),Math.max(startSeat,endSeat));
    }

}
