package phi.elyoum8.util.dataBinding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class SeatNumbersForm {

    @NotBlank(message = "رقم البداية مينفعش يكون فاضي")
    @Pattern(
            regexp = "^[0-9\\u0660-\\u0669]+$",
            message = "رقم البداية ده مش رقم جلوس! دخل ارقام بس"
    )
    String startSeatNumber;


    @NotBlank(message = "رقم النهاية مينفعش يكون فاضي")
    @Pattern(
            regexp = "^[0-9\\u0660-\\u0669]+$",
            message = "رقم النهاية ده مش رقم جلوس! دخل ارقام بس"
    )
    String endSeatNumber;

}
