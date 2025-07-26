package phi.elyoum8.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import phi.elyoum8.model.Student;
import phi.elyoum8.repository.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;


    public Student findBySeatNumber(Long seatNumber) {
        return studentRepository.findBySeatNumber(seatNumber);
    }

    public List<Student> findByName(String name) {
        return studentRepository.findAllStudentsByNameStartsWith(name);
    }

    public List<Student> findBySeatNumberRange(Long startSeat, Long endSeat) {
        return studentRepository.findAllBetween(Math.min(startSeat,endSeat),Math.max(startSeat,endSeat));
    }

    public Page<Student> findAll(Integer page, Integer size) {
        return studentRepository.findAllByOrderByPercentageDescArabicNameAsc(PageRequest.of(page,size));
    }
}