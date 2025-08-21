package phi.elyoum8.util.dataBinding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NameForm {

    @NotBlank(message = "الاسم مينفعش يكون فاضي")
    @Pattern(
            regexp = "^[\\u0621-\\u064A\\s]+$",
            message = "الاسم لازم يكون بالعربي ولايحتوي على ارقام او علامات"
    )
    String text;
    Boolean spellCheck;
    Boolean isMidName;
}
