package phi.elyoum8.util.generator;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentTemplateFields {
    String title;
    String seatNumberField;
    String arabicNameField;
    String totalDegreeField;
    String percentageField;
    String rankField;
    String rankWithDuplicatesField;
    String footerMessage;

}
