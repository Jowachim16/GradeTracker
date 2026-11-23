import java.util.Scanner;

/**
 * Single place for reading user input.
 *
 * Every prompt goes through here so bad input is handled the same way
 * everywhere: the prompt repeats until the answer is valid.
 */
public class InputReader {

    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Reads an integer in [lo, hi], re-prompting until one is given. */
    public int readInt(String prompt, int lo, int hi) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= lo && value <= hi) return value;
            } catch (NumberFormatException ignored) {
                // Not a number at all; fall through to the message below.
            }
            System.out.println("  invalid - enter a number " + lo + " to " + hi + ".");
        }
    }

    /** Reads a decimal in [lo, hi], re-prompting until one is given. */
    public double readDouble(String prompt, double lo, double hi) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= lo && value <= hi) return value;
            } catch (NumberFormatException ignored) {
                // Not a number at all; fall through to the message below.
            }
            System.out.println("  invalid - enter a number " + lo + " to " + hi + ".");
        }
    }

    /**
     * Reads any decimal with no range check. Used when the caller validates
     * the value itself (for example, against the transmutation table).
     */
    public double readDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException ignored) {
                System.out.println("  not a number, try again.");
            }
        }
    }

    /** Prints the prompt and returns the trimmed line the user typed. */
    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}