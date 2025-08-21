package phi.elyoum8.util.dataBinding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SeatNumberForm {

    @Pattern(
            regexp = "^[0-9\\u0660-\\u0669]+$",
            message = "ده مش رقم جلوس! دخل ارقام بس"
    )
    String seatNumber;
}
