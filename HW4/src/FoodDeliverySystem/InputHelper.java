package FoodDeliverySystem;


/**
 * Utility class for input VALIDATION.
 *
 * Each method takes a value and returns true/false, so callers can check
 * user input (taken from the JavaFX text fields) before building or updating
 * a model object. In Assignment 4 the system is driven entirely by JavaFX,
 * so this class no longer performs any console reading - it only validates.
 *
 *   - Strings must not be null, empty, or whitespace-only.
 *   - Validation happens BEFORE constructor calls, not inside setters.
 *
 * All methods are static so the class never needs to be instantiated.
 */
public class InputHelper {

    // Private constructor prevents instantiation - this is a pure utility class.
    private InputHelper() { }
    

    /**
     * Validates a string: must be non-null AND contain at least one
     * non-whitespace character. Empty strings ("") and whitespace-only
     * strings ("   ") are both rejected.
     *
     * @param s the string to validate
     * @return true if the string is valid (non-null, non-empty after trimming)
     */
    public static boolean isValidString(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /**
     * Validates an email address per the course staff specification:
     *   1. Must be a valid string (non-null, non-empty).
     *   2. Must contain EXACTLY ONE '@' symbol.
     *   3. The '@' must NOT be at the start or the end.
     *   4. Must contain at least one '.' AFTER the '@'.
     *   5. The dot must NOT be immediately after the '@' (rejects "user@.com").
     *   6. The dot must NOT be the very last character (rejects "user@domain.").
     *
     * Examples:
     *   "yossi@gmail.com"      -> valid
     *   "yossi@huji.ac.il"     -> valid
     *   "yossi@.com"           -> invalid (dot right after @)
     *   "yossi@gmail."         -> invalid (dot at end)
     *   "yossigmail.com"       -> invalid (no @)
     *   "yossi@gmailcom"       -> invalid (no dot after @)
     *   "@gmail.com"           -> invalid (@ at start)
     *   "a@@b.com"             -> invalid (more than one @)
     *
     * @param email the email string to check
     * @return true if the email passes all the checks above
     */
    public static boolean isValidEmail(String email) {
        if (!isValidString(email)) {
            return false;
        }
        String trimmed = email.trim();

        int firstAt = trimmed.indexOf('@');
        int lastAt  = trimmed.lastIndexOf('@');

        if (firstAt == -1) {
            return false; // no '@'
        }
        if (firstAt != lastAt) {
            return false; // more than one '@'
        }
        if (firstAt == 0) {
            return false; // '@' at the start
        }
        if (firstAt == trimmed.length() - 1) {
            return false; // '@' at the end
        }

        // Find the FIRST dot AFTER the '@'
        int dotIndex = trimmed.indexOf('.', firstAt + 1);
        if (dotIndex == -1) {
            return false; // no dot after '@'
        }

        // Dot must not be immediately after '@'
        if (dotIndex == firstAt + 1) {
            return false;
        }

        // Dot must not be the last character
        if (dotIndex == trimmed.length() - 1) {
            return false;
        }

        return true;
    }

    /**
     * Validates a phone number with a basic check:
     *   - Must be a valid string.
     *   - Must contain only digits, dashes, plus signs, parentheses, or spaces.
     *   - Must contain at least 7 digits in total.
     *
     * @param phone the phone string to check
     * @return true if the phone passes the basic checks
     */
    public static boolean isValidPhone(String phone) {
        if (!isValidString(phone)) {
            return false;
        }
        String trimmed = phone.trim();
        int digitCount = 0;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (Character.isDigit(c)) {
                digitCount++;
            } else if (c != '-' && c != '+' && c != '(' && c != ')' && c != ' ') {
                return false; // invalid character
            }
        }
        return digitCount >= 7;
    }

    /**
     * Validates that a string contains only LETTERS.
     * No digits, no symbols, no spaces. Used for first names.
     *
     * Examples:
     *   "David"   -> valid
     *   "Noa"     -> valid
     *   "David123"-> invalid (contains digits)
     *   "Da vid"  -> invalid (contains space; use isLettersAndSpaces instead)
     *   ""        -> invalid (empty)
     *
     * @param s the string to check
     * @return true if the string is non-empty and contains only letters
     */
    public static boolean isLettersOnly(String s) {
        if (!isValidString(s)) {
            return false;
        }
        String trimmed = s.trim();
        for (int i = 0; i < trimmed.length(); i++) {
            if (!Character.isLetter(trimmed.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates that a string contains only LETTERS and SPACES.
     * Used for last names (compound surnames like "Ben Gurion") and city names
     * (like "Tel Aviv"). At least one letter must be present.
     *
     * Examples:
     *   "Cohen"      -> valid
     *   "Ben Gurion" -> valid
     *   "Tel Aviv"   -> valid
     *   "Cohen123"   -> invalid (contains digits)
     *   "Cohen!"     -> invalid (contains symbol)
     *   "   "        -> invalid (no letters at all)
     *
     * @param s the string to check
     * @return true if the string contains only letters and spaces, with at least one letter
     */
    public static boolean isLettersAndSpaces(String s) {
        if (!isValidString(s)) {
            return false;
        }
        String trimmed = s.trim();
        boolean hasLetter = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (c != ' ') {
                return false; // invalid character (not a letter and not a space)
            }
        }
        return hasLetter;
    }

    /**
     * Validates that a string contains ONLY digits, within an optional length range.
     * Pass minLen == maxLen to require an exact length.
     *
     * Used for zip code (5-7 digits), national ID (exactly 9 digits), etc.
     *
     * Examples (minLen=5, maxLen=7):
     *   "3303220" -> valid
     *   "12345"   -> valid
     *   "1234"    -> invalid (too short)
     *   "abc"     -> invalid (contains letters)
     *   "12 34"   -> invalid (contains space)
     *
     * @param s      the string to check
     * @param minLen minimum allowed length (inclusive)
     * @param maxLen maximum allowed length (inclusive)
     * @return true if the string is digits-only and within length bounds
     */
    public static boolean isDigitsOnly(String s, int minLen, int maxLen) {
        if (!isValidString(s)) {
            return false;
        }
        String trimmed = s.trim();
        if (trimmed.length() < minLen || trimmed.length() > maxLen) {
            return false;
        }
        for (int i = 0; i < trimmed.length(); i++) {
            if (!Character.isDigit(trimmed.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates an Israeli national ID (תז):
     *   - Digits only
     *   - Exactly 9 characters long
     * Note: This does NOT verify the check digit, per assignment guidance.
     *
     * @param id the national ID string
     * @return true if the ID is exactly 9 digits
     */
    public static boolean isValidNationalId(String id) {
        return isDigitsOnly(id, 9, 9);
    }

    /**
     * Validates a rating between 0.0 and 5.0 (inclusive on both ends).
     *
     * @param rating the rating value
     * @return true if 0.0 <= rating <= 5.0
     */
    public static boolean isValidRating(double rating) {
        return rating >= 0.0 && rating <= 5.0;
    }
}