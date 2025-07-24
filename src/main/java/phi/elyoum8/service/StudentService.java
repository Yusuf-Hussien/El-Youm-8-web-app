package phi.elyoum8.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import phi.elyoum8.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;


}
