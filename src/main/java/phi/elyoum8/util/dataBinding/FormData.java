package phi.elyoum8.util.dataBinding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FormData {
    @NotBlank(message = "Seat number is required")
    @Pattern(regexp = "\\d+", message = "Seat number must be numeric")
    private String text;
}