package phi.elyoum8.repository;

import jakarta.transaction.Transactional;
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
    public List<Student> findAllByArabicName(String name);
    public List<Student> findAllByTotalDegree(Double degree);

    public List<Student>findAllByTotalDegreeBetween(Double from, Double to);
    public List<Student>findGreaterByTotalDegree(Double degree);

    List<Student> findAllByOrderByTotalDegreeDescArabicNameAsc(Pageable pageable);

    @Query(value = "SELECT student_rank FROM student WHERE seat_number=1859654;",nativeQuery = true)
    public Long getRecordRank();

    @Query(value = "SELECT * FROM student ORDER BY total_degree DESC;" ,nativeQuery = true)
    public List<Student>findTopNStudent(Pageable pageable);

    @Query(value = "select count(s.seat_number) from student as s;",nativeQuery = true)
    public Long countStudent();

    @Query(value = "SELECT * FROM student WHERE arabic_name = :name ;", nativeQuery = true)
    List<Student> findAllStudentsByNameLike(@Param("name") String name);

    @Query(value = "SELECT * FROM student WHERE arabic_name LIKE CONCAT(:name, '%')", nativeQuery = true)
    List<Student> findAllStudentsByNameStartsWith(@Param("name") String name);


    @Query(value = """
                      SELECT CASE
                               WHEN s.total_degree IS NULL THEN FALSE
                               ELSE THEN TRUE 
                             END 
                       FROM student AS s WHERE s.seat_number=1917864;""",nativeQuery = true)
    Boolean isLastStudentDone();


    @Query(value = """
                      SELECT CASE
                               WHEN COUNT(s.seat_number) = :sheetSize THEN TRUE
                               ELSE FALSE 
                             END 
                       FROM student AS s ;""",nativeQuery = true)
    Boolean areAllInserted(@Param("sheetSize")Long sheetSize);


    @Modifying
    @Query(value = """
            UPDATE student s
            JOIN (
                SELECT seat_number,
                       RANK() OVER (ORDER BY total_degree DESC, arabic_name ASC) AS rank_with_duplicates,
                       ROW_NUMBER() OVER (ORDER BY total_degree DESC, arabic_name ASC) AS  student_rank
                FROM student
                WHERE seat_number BETWEEN :startSeatNumber AND :endSeatNumber
            ) r ON s.seat_number = r.seat_number
            SET s.student_rank = r.student_rank,
                s.rank_with_duplicates = r.rank_with_duplicates
            WHERE s.seat_number BETWEEN :startSeatNumber AND :endSeatNumber
        """,nativeQuery = true)
    public void setRanks(@Param("startSeatNumber")long startSeatNumber, @Param("endSeatNumber")long endSeatNumber);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM student",nativeQuery = true)
    public void deleteAllStudents();
    //public void deleteAll();
}
