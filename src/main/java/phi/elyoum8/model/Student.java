package phi.elyoum8.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import phi.elyoum8.util.genric.ArabicNameNormalizer;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="student"
        ,indexes = {@Index(name = "idx_normalized_arabic_name",columnList = "normalized_arabic_name")
                   ,@Index(name = "idx_student_percentage_name", columnList = "percentage DESC, arabic_name ASC")})
public class Student {

    @Id
    @Column(name = "seat_number")
    Long seatNumber;

    @Column(name = "arabic_name")
    String arabicName;

    @Column(name = "normalized_arabic_name")
    String normalizedArabicName;


    @Column(name = "total_degree")
    Double totalDegree;

    Double percentage;

    @Column(name = "student_rank")
    Long studentRank;

    @Column(name = "rank_with_duplicates")
    Long rankWithDuplicates;

    public void setArabicName(String arabicName) {
        this.arabicName = arabicName;
        this.normalizedArabicName = ArabicNameNormalizer.normalize(arabicName);
    }

    public void setTotalDegree(double totalDegree)
    {
        this.totalDegree = totalDegree;
        this.percentage = Math.round(
                (totalDegree / (totalDegree >= 320 ? 410.0 : 320.0)) * 100 * 100.0
        ) / 100.0;
        this.percentage = Math.round(percentage*1000.0)/1000.0;
    }
}
