package phi.elyoum8.util.validation;


public class CustomValidator {

    public static boolean isValidArabicName(String input) {
        if (input == null || input.trim().isEmpty()) return false;

        return input.matches("^[\\u0621-\\u064A\\s]+$"); // Allow Arabic letters and spaces
    }

    public static boolean isValidNumber(String input) {
        return input.matches("^[0-9\\u0660-\\u0669]+$"); // Allow Arabic and english numbers
    }

}
