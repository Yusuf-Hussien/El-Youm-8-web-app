package phi.elyoum8.repository;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import phi.elyoum8.model.Student;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mysql")
public interface StudentMysqlRepository extends StudentRepository {

    @Override
    @Query(value = "SELECT * FROM student WHERE arabic_name LIKE CONCAT(:name, '%')", nativeQuery = true)
    List<Student> findAllStudentsByNameStartsWith(@Param("name") String name);

    @Override
    @Query(value = "SELECT * FROM student WHERE arabic_name LIKE CONCAT('%',:name, '%')", nativeQuery = true)
    List<Student> findAllStudentsByNameContains(@Param("name") String name);

    @Override
    @Query(value = "SELECT * FROM student WHERE normalized_arabic_name LIKE CONCAT(:name, '%')", nativeQuery = true)
    List<Student> findAllStudentsByNormalizedNameStartsWith(@Param("name") String name);

    @Override
    @Query(value = "SELECT * FROM student WHERE normalized_arabic_name LIKE CONCAT('%',:name, '%')", nativeQuery = true)
    List<Student> findAllStudentsByNormalizedNameContains(@Param("name") String name);


    @Override
    @Query(value = """
                      SELECT * FROM  student AS s WHERE s.seat_number BETWEEN :from_id AND :to_id ORDER BY s.seat_number ASC;""" ,nativeQuery = true)
    List<Student>findAllBetween(@Param("from_id")Long from, @Param("to_id")Long to);


    @Override
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
    void setRanks();



    @Override
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM student",nativeQuery = true)
    void deleteAllStudents();


    // Helper Queries
    @Override
    @Query(value = "SELECT student_rank FROM student AS s WHERE s.percentage = (SELECT MIN(t.percentage) FROM student AS t) LIMIT 1 ;",nativeQuery = true)
    Long getMinRank();


    @Override
    @Query(value = "select count(s.seat_number) from student as s;",nativeQuery = true)
    Long countStudent();
}
