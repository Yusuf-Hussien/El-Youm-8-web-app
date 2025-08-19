package phi.elyoum8.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import phi.elyoum8.model.Student;

import static org.junit.jupiter.api.Assertions.*;

class StudentRestControllerTest {

    private StudentRestController controller;

    @BeforeEach
    void setUp() {
        controller = new StudentRestController(null,null,null);
    }

    @Test
    void should_return_student_by_id() {
        var s  = controller.getStudent(-1);
        System.out.println(s);
        Assertions.assertNull(s);
    }
}