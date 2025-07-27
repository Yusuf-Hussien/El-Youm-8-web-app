package phi.elyoum8.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import phi.elyoum8.model.Student;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {

    public Student findBySeatNumber(long id);

    Page<Student> findAllByOrderByPercentageDescArabicNameAsc(Pageable pageable);

    @Query(value = "SELECT * FROM student WHERE arabic_name LIKE CONCAT(:name, '%')", nativeQuery = true)
    List<Student> findAllStudentsByNameStartsWith(@Param("name") String name);


    @Query(value = """
                      SELECT * FROM  student AS s WHERE s.seat_number BETWEEN :from_id AND :to_id ORDER BY s.seat_number ASC;""" ,nativeQuery = true)
    public List<Student>findAllBetween(@Param("from_id")Long from, @Param("to_id")Long to);


    @Transactional
    @Modifying
    @Query(value = """
            UPDATE student s
            JOIN (
                SELECT seat_number,
                       DENSE_RANK() OVER (ORDER BY percentage DESC) AS rank_with_duplicates,
                       ROW_NUMBER() OVER (ORDER BY percentage DESC, arabic_name ASC) AS  student_rank
                FROM student
            ) r ON s.seat_number = r.seat_number
            SET s.student_rank = r.student_rank,
                s.rank_with_duplicates = r.rank_with_duplicates;
        """,nativeQuery = true)
    public void setRanks();



    @Transactional
    @Modifying
    @Query(value = "DELETE FROM student",nativeQuery = true)
    public void deleteAllStudents();


    // Helper Queries
    @Query(value = "SELECT student_rank FROM student AS s WHERE s.percentage = (SELECT MIN(t.percentage) FROM student AS t) LIMIT 1 ;",nativeQuery = true)
    public Long getMinRank();


    @Query(value = "select count(s.seat_number) from student as s;",nativeQuery = true)
    public Long countStudent();
}
