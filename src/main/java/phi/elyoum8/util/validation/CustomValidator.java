package phi.elyoum8.util.validation;


public class CustomValidator {

    public static boolean isValidArabicName(String input) {
        if (input == null || input.trim().isEmpty()) return false;

        return input.matches("^[\\p{InArabic}\\s]+$"); // Allow Arabic letters and spaces
    }

}
