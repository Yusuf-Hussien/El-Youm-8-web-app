package phi.elyoum8.repository;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import phi.elyoum8.model.Student;

import java.util.List;

@Repository
@Profile("mssql")
public interface StudentMsSqlRepository extends StudentRepository {

    @Override
    @Query(value = "SELECT * FROM student WHERE arabic_name LIKE :name + '%'", nativeQuery = true)
    List<Student> findAllStudentsByNameStartsWith(@Param("name") String name);

    @Override
    @Query(value = "SELECT * FROM student WHERE arabic_name LIKE '%' + :name + '%'", nativeQuery = true)
    List<Student> findAllStudentsByNameContains(@Param("name") String name);

    @Override
    @Query(value = """
                      SELECT * FROM student s 
                      WHERE s.seat_number BETWEEN :from_id AND :to_id 
                      ORDER BY s.seat_number ASC
                   """, nativeQuery = true)
    List<Student> findAllBetween(@Param("from_id") Long from, @Param("to_id") Long to);

    @Override
    @Transactional
    @Modifying
    @Query(value = """
        UPDATE s
        SET s.student_rank = r.student_rank,
            s.rank_with_duplicates = r.rank_with_duplicates
        FROM student s
        INNER JOIN (
            SELECT seat_number,
                   DENSE_RANK() OVER (ORDER BY percentage DESC) AS rank_with_duplicates,
                   ROW_NUMBER() OVER (ORDER BY percentage DESC, arabic_name ASC) AS student_rank
            FROM student
        ) r ON s.seat_number = r.seat_number
        """, nativeQuery = true)
    void setRanks();

    @Override
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM student", nativeQuery = true)
    void deleteAllStudents();

    @Override
    @Query(value = """
        SELECT TOP 1 student_rank 
        FROM student s 
        WHERE s.percentage = (SELECT MIN(t.percentage) FROM student t)
        """, nativeQuery = true)
    Long getMinRank();

    @Override
    @Query(value = "SELECT COUNT(s.seat_number) FROM student s", nativeQuery = true)
    Long countStudent();
}
