package phi.elyoum8.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="student"
        ,indexes = {@Index(name = "idx_arabic_name",columnList = "arabic_name")
                   ,@Index(name = "idx_student_degree_name", columnList = "total_degree DESC, arabic_name ASC")})
public class Student {

    @Id
    @Column(name = "seat_number")
    Long seatNumber;

    @Column(name = "arabic_name")
    String arabicName;

    @Column(name = "total_degree")
    Double totalDegree;

    Double percentage;

    @Column(name = "student_rank")
    Long studentRank;

    @Column(name = "rank_with_duplicates")
    Long rankWithDuplicates;

    public void setTotalDegree(double totalDegree)
    {
        this.totalDegree = totalDegree;
        this.percentage = (totalDegree/320)*100;
        this.percentage = Math.round(percentage*1000.0)/1000.0;
    }
}
