package FoodDeliverySystem;

import java.util.Scanner;

/**
 * Utility class for input validation and safe Scanner reads.
 *
 * This class provides two types of helpers:
 *   1. PURE VALIDATORS - methods that take a value and return true/false.
 *      Use these when you already have the value (e.g. in a constructor caller
 *      or a menu loop) and just want to check if it's valid.
 *
 *   2. INTERACTIVE READERS - methods that take a Scanner and a prompt, repeatedly
 *      ask the user until valid input is provided, then return that input.
 *      Use these in the menu code to read user input safely.

 *   - Strings must not be null, empty, or whitespace-only.
 *   - Validation happens BEFORE constructor calls, not inside setters.
 *   - All input methods are self-recovering (they don't throw on bad input).
 *
 * All methods are static so the class never needs to be instantiated.
 */
public class InputHelper {

    // Private constructor prevents instantiation - this is a pure utility class.
    private InputHelper() { }
    private static String safeNextLine(Scanner sc) {
        if (!sc.hasNextLine()) {
            System.out.println("\nInput ended unexpectedly. Exiting safely. Goodbye!");
            System.exit(0);
        }
        return sc.nextLine();
    }
    

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

    // =====================================================================
    // INTERACTIVE READERS (loop until valid input)
    // =====================================================================

    /**
     * Repeatedly prompts the user until they enter a non-empty, non-whitespace string.
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return the trimmed valid string
     */
    public static String readNonEmptyString(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = safeNextLine(sc);
            if (isValidString(line)) {
                return line.trim();
            }
            System.out.println("  ERROR: Input cannot be empty or only spaces. Please try again.");
        }
    }

    /**
     * Repeatedly prompts the user until they enter a valid email address.
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return the valid email string
     */
    public static String readValidEmail(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = safeNextLine(sc);
            if (isValidEmail(line)) {
                return line.trim();
            }
            System.out.println("  ERROR: Invalid email format. Must contain '@' and a '.' after it.");
        }
    }

    /**
     * Repeatedly prompts the user until they enter a valid phone number.
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return the valid phone string
     */
    public static String readValidPhone(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = safeNextLine(sc);
            if (isValidPhone(line)) {
                return line.trim();
            }
            System.out.println("  ERROR: Invalid phone format. Must contain at least 7 digits.");
        }
    }

    /**
     * Repeatedly prompts the user until they enter a string containing only letters.
     * Used for first names.
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return the valid letters-only string (trimmed)
     */
    public static String readLettersOnly(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = safeNextLine(sc);
            if (isLettersOnly(line)) {
                return line.trim();
            }
            System.out.println("  ERROR: Letters only (no digits, no symbols, no spaces).");
        }
    }

    /**
     * Repeatedly prompts the user until they enter a string containing only
     * letters and spaces. Used for last names and city names (e.g. "Tel Aviv").
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return the valid letters-and-spaces string (trimmed)
     */
    public static String readLettersAndSpaces(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = safeNextLine(sc);            if (isLettersAndSpaces(line)) {
                return line.trim();
            }
            System.out.println("  ERROR: Letters and spaces only (no digits, no symbols).");
        }
    }

    /**
     * Repeatedly prompts the user until they enter a string containing only
     * digits within the given length range.
     * Pass minLen == maxLen for exact-length requirements (e.g. zip code).
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @param minLen minimum allowed length
     * @param maxLen maximum allowed length
     * @return the valid digits-only string (trimmed)
     */
    public static String readDigitsOnly(Scanner sc, String prompt, int minLen, int maxLen) {
        while (true) {
            System.out.print(prompt);
            String line = safeNextLine(sc);            if (isDigitsOnly(line, minLen, maxLen)) {
                return line.trim();
            }
            if (minLen == maxLen) {
                System.out.println("  ERROR: Must be exactly " + minLen + " digits.");
            } else {
                System.out.println("  ERROR: Must be " + minLen + "-" + maxLen
                        + " digits (digits only, no other characters).");
            }
        }
    }

    /**
     * Repeatedly prompts the user until they enter a valid Israeli national ID
     * (exactly 9 digits). Used for rider IDs.
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return the valid 9-digit national ID
     */
    public static String readValidNationalId(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = safeNextLine(sc);            if (isValidNationalId(line)) {
                return line.trim();
            }
            System.out.println("  ERROR: National ID must be exactly 9 digits.");
        }
    }

    /**
     * Repeatedly prompts the user until they enter a valid integer.
     * Handles non-numeric input gracefully (no crash).
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return the integer entered by the user
     */
    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = safeNextLine(sc);            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("  ERROR: Please enter a valid whole number.");
            }
        }
    }

    /**
     * Repeatedly prompts the user until they enter a positive integer (> 0).
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return a positive integer
     */
    public static int readPositiveInt(Scanner sc, String prompt) {
        while (true) {
            int value = readInt(sc, prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("  ERROR: Value must be greater than zero.");
        }
    }

    /**
     * Repeatedly prompts the user until they enter an integer within [min, max].
     * Used mainly for menu choices.
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @param min    minimum allowed value (inclusive)
     * @param max    maximum allowed value (inclusive)
     * @return an integer between min and max
     */
    public static int readIntInRange(Scanner sc, String prompt, int min, int max) {
        while (true) {
            int value = readInt(sc, prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("  ERROR: Value must be between " + min + " and " + max + ".");
        }
    }

    /**
     * Repeatedly prompts the user until they enter a valid double.
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return the double entered by the user
     */
    public static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = safeNextLine(sc);            try {
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("  ERROR: Please enter a valid number (e.g. 25.50).");
            }
        }
    }

    /**
     * Repeatedly prompts the user until they enter a non-negative double (>= 0).
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return a non-negative double
     */
    public static double readNonNegativeDouble(Scanner sc, String prompt) {
        while (true) {
            double value = readDouble(sc, prompt);
            if (value >= 0) {
                return value;
            }
            System.out.println("  ERROR: Value cannot be negative.");
        }
    }

    /**
     * Repeatedly prompts the user until they enter a positive double (> 0).
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return a positive double
     */
    public static double readPositiveDouble(Scanner sc, String prompt) {
        while (true) {
            double value = readDouble(sc, prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("  ERROR: Value must be greater than zero.");
        }
    }

    /**
     * Repeatedly prompts the user until they enter a valid rating (0.0 to 5.0).
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return a rating between 0.0 and 5.0
     */
    public static double readRating(Scanner sc, String prompt) {
        while (true) {
            double value = readDouble(sc, prompt);
            if (isValidRating(value)) {
                return value;
            }
            System.out.println("  ERROR: Rating must be between 0.0 and 5.0.");
        }
    }

    /**
     * Repeatedly prompts the user until they enter a yes/no answer.
     * Accepts: yes, y, true, 1 -> true.
     *          no, n, false, 0 -> false.
     *
     * @param sc     the Scanner to read from
     * @param prompt the message to display before each attempt
     * @return true if the user said yes, false if no
     */
    public static boolean readYesNo(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt + " (yes/no): ");
            String line = safeNextLine(sc);            if (line == null) {
                continue;
            }
            String t = line.trim().toLowerCase();
            if (t.equals("yes") || t.equals("y") || t.equals("true") || t.equals("1")) {
                return true;
            }
            if (t.equals("no")  || t.equals("n") || t.equals("false") || t.equals("0")) {
                return false;
            }
            System.out.println("  ERROR: Please answer with 'yes' or 'no'.");
        }
    }
}