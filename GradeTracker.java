import java.util.List;
import java.util.Scanner;

/**
 * ============================================================
 *  GradeTracker - terminal grade tracking system
 * ------------------------------------------------------------
 *  Entry point. Shows the banner and main menu, then hands each
 *  choice to the matching Screen. The grading rules live in the
 *  model classes; this file only handles menus and display.
 *
 *  HOW TO RUN (from the folder that contains these files):
 *    1. Compile : javac *.java
 *    2. Execute : java GradeTracker
 *
 *  WHAT IT DOES
 *    - Records scores for quizzes, prelims, midterms, final project
 *      and final exam (quizzes can hold many scores).
 *    - Each component has an editable weight; weights must total 100.
 *    - Shows a grade report that grows as components are graded.
 *    - Lets you edit the transmutation table.
 *    - Calculates the score you still need to reach a target grade point.
 * ============================================================
 */
public class GradeTracker {

    public static void main(String[] args) {
        GradeBook book = new GradeBook();
        InputReader in = new InputReader(new Scanner(System.in));

        // screens[N - 1] handles menu option N. Option 0 (exit) is handled by the loop.
        Screen[] screens = {
            new ScoreScreen(book, in),
            new WeightScreen(book, in),
            new ReportScreen(book, in),
            new TransmutationScreen(book, in),
            new IdealScoreScreen(book, in),
            new ResetScreen(book, in),
        };

        printBanner();
        while (true) {
            printMenu();
            int choice = in.readInt("  select [0-6]: ", 0, screens.length);
            if (choice == 0) break;
            // Polymorphic call: each Screen runs its own behaviour.
            screens[choice - 1].show();
        }
        System.out.println("  Session ended. Keep aiming for that 1.00!");
    }

    private static void printBanner() {
        System.out.println("============================================================");
        System.out.println("   G T R A C K  -  SEMESTER GRADE TRACKING SYSTEM");
        System.out.println("   scale: 1.00 (highest) -> 3.00 (lowest passing) -> 5.00");
        System.out.println("============================================================");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("  ---------- MAIN MENU ----------");
        System.out.println("   [1] Enter / update scores");
        System.out.println("   [2] Configure component weights");
        System.out.println("   [3] View grade report");
        System.out.println("   [4] Transmutation table settings");
        System.out.println("   [5] Ideal score calculator");
        System.out.println("   [6] Reset all data");
        System.out.println("   [0] Exit");
    }

    /* ==================== SCREENS ==================== */

    /**
     * Base class for every main-menu option.
     *
     * The main loop only knows about Screen. To add a new option, write a new
     * subclass and add it to the array in main(); the loop itself doesn't change.
     */
    static abstract class Screen {

        protected final GradeBook book;
        protected final InputReader in;

        Screen(GradeBook book, InputReader in) {
            this.book = book;
            this.in = in;
        }

        /** Runs this option once. Each subclass decides what it shows and asks for. */
        public abstract void show();
    }

    /** Option 1: pick a component and enter its score(s). */
    static class ScoreScreen extends Screen {

        ScoreScreen(GradeBook book, InputReader in) {
            super(book, in);
        }

        @Override
        public void show() {
            List<GradeComponent> components = book.getComponents();

            System.out.println();
            System.out.println("  SELECT COMPONENT");
            for (int i = 0; i < components.size(); i++) {
                GradeComponent c = components.get(i);
                System.out.printf("   [%d] %-14s %s%n", i + 1, c.getName(), c.menuLabel());
            }
            System.out.println("   [0] back");

            int pick = in.readInt("  component [0-" + components.size() + "]: ", 0, components.size());
            if (pick == 0) return;

            // The component decides how its scores are entered.
            System.out.println(components.get(pick - 1).enterScores(in));
        }
    }

    /** Option 2: change each component's weight. The weights must total 100. */
    static class WeightScreen extends Screen {

        WeightScreen(GradeBook book, InputReader in) {
            super(book, in);
        }

        @Override
        public void show() {
            List<GradeComponent> components = book.getComponents();

            System.out.println();
            System.out.println("  CURRENT WEIGHTS");
            for (GradeComponent c : components) {
                System.out.printf("   %-14s %.2f%%%n", c.getName(), c.getWeight());
            }

            // Read every weight before validating, so the user enters the full set
            // and sees one clear result.
            double[] weights = new double[components.size()];
            double sum = 0;
            for (int i = 0; i < components.size(); i++) {
                weights[i] = in.readDouble(
                        "  weight for " + components.get(i).getName() + " (0-100): ", 0, 100);
                sum += weights[i];
            }

            if (!book.setWeights(weights)) {
                System.out.printf("  rejected - weights total %.2f, must be %.0f.%n",
                        sum, GradeBook.REQUIRED_WEIGHT_TOTAL);
                return;
            }
            System.out.println("  weights updated.");
        }
    }

    /**
     * Option 3: the grade report. It shows what is locked in, the current
     * standing, and the range the final grade could still land in.
     */
    static class ReportScreen extends Screen {

        ReportScreen(GradeBook book, InputReader in) {
            super(book, in);
        }

        @Override
        public void show() {
            System.out.println();
            System.out.println("  ----------------------- GRADE REPORT -----------------------");
            System.out.printf("  %-14s %8s %10s %12s   %s%n",
                    "COMPONENT", "WEIGHT", "SCORE", "EARNED", "STATUS");
            System.out.println("  ------------------------------------------------------------");
            for (GradeComponent c : book.getComponents()) {
                System.out.printf("  %-14s %7.2f%% %10s %10.2f p   %s%n",
                        c.getName(), c.getWeight(), c.scoreText(), c.contribution(),
                        c.isGraded() ? "GRADED" : "PENDING");
            }
            System.out.println("  ------------------------------------------------------------");
            System.out.printf("  %-14s %8s %10s %12.2f p%n",
                    "TOTAL", "100.00%", "", book.earnedPoints());

            // Standing is an average over graded work, so it's undefined until something is graded.
            if (book.gradedWeight() == 0) {
                System.out.println("  no scores yet - use option [1].");
                return;
            }

            TransmutationTable table = book.getTable();
            double standing = book.standing();
            double grade = table.gradePointFor(standing);

            System.out.println();
            System.out.printf("   coverage : %.2f%% of the final grade is locked in%n",
                    book.gradedWeight());
            System.out.printf("   standing : %.2f%% -> %.2f %s (provisional)%n",
                    standing, grade, TransmutationTable.remark(grade));
            System.out.printf("   floor    : %.2f%% -> %.2f  (if you score 0 on the rest)%n",
                    book.floor(), table.gradePointFor(book.floor()));
            System.out.printf("   ceiling  : %.2f%% -> %.2f  (if you score 100 on the rest)%n",
                    book.ceiling(), table.gradePointFor(book.ceiling()));
        }
    }

    /**
     * Option 4: view and edit the transmutation table. Stays open until the
     * user goes back, so several rows can be changed in one visit.
     */
    static class TransmutationScreen extends Screen {

        TransmutationScreen(GradeBook book, InputReader in) {
            super(book, in);
        }

        @Override
        public void show() {
            // The table object stays the same across resets (see restoreStandard),
            // so holding one reference for the whole visit is safe.
            TransmutationTable table = book.getTable();

            boolean back = false;
            while (!back) {
                System.out.println();
                System.out.println("  TRANSMUTATION TABLE  (grade point -> minimum %)");
                List<Double> gradePoints = table.gradePoints();
                for (int i = 0; i < gradePoints.size(); i++) {
                    double gp = gradePoints.get(i);
                    System.out.printf("   [%d] %.2f requires >= %d%%%n",
                            i + 1, gp, table.minimumFor(gp));
                }
                System.out.println("   [0] back   [-1] reset to standard scale");

                int pick = in.readInt("  select: ", -1, gradePoints.size());
                if (pick == 0) {
                    back = true;
                } else if (pick == -1) {
                    table.restoreStandard();
                    System.out.println("  reset done.");
                } else {
                    double gp = gradePoints.get(pick - 1);
                    if (!table.isEditable(gp)) {
                        System.out.println("  the 5.00 fail floor is fixed.");
                        continue;
                    }
                    int lo = table.lowestAllowed(gp);
                    int hi = table.highestAllowed(gp);
                    int min = in.readInt(
                            "  new minimum % for " + gp + " (" + lo + "-" + hi + "): ", lo, hi);
                    table.setMinimum(gp, min);
                    System.out.println("  updated.");
                }
            }
        }
    }

    /**
     * Option 5: tells the user the average they need on the remaining
     * components to reach a target grade point.
     */
    static class IdealScoreScreen extends Screen {

        IdealScoreScreen(GradeBook book, InputReader in) {
            super(book, in);
        }

        @Override
        public void show() {
            double earned = book.earnedPoints();
            double pending = book.pendingWeight();

            // Without pending components there's nothing left to aim for.
            if (pending == 0) {
                System.out.println("  everything is graded - see option [3].");
                return;
            }

            TransmutationTable table = book.getTable();
            printTable(table);
            double target = readTarget(table);

            double needed = table.minimumFor(target);
            double average = book.requiredAverageFor(target);

            System.out.printf("  earned %.2f pts - remaining weight %.2f%% (%s)%n",
                    earned, pending, String.join(", ", book.pendingNames()));

            if (average <= 0) {
                System.out.printf("  TARGET SECURED - even 0%% on the rest keeps you >= %.0f%%.%n", needed);
            } else if (average > 100) {
                System.out.printf("  NOT REACHABLE - 100%% on everything left tops out at %.2f%% -> %.2f.%n",
                        earned + pending, table.gradePointFor(earned + pending));
            } else {
                System.out.printf("  you need an average of %.2f%% across the remaining components.%n",
                        average);
                for (String name : book.pendingNames()) {
                    System.out.printf("     - %-14s aim >= %.2f%%%n", name, average);
                }
            }
        }

        /** Keeps asking until the user enters a grade point that can be targeted. */
        private double readTarget(TransmutationTable table) {
            while (true) {
                double target = in.readDecimal("  target grade point (1.00-3.00): ");
                if (table.isValidTarget(target)) return target;
                System.out.println("  invalid target.");
            }
        }

        private void printTable(TransmutationTable table) {
            System.out.println("  TRANSMUTATION TABLE");
            for (double gp : table.gradePoints()) {
                System.out.printf("   %.2f requires >= %d%%%n", gp, table.minimumFor(gp));
            }
        }
    }

    /**
     * Option 6: wipe all scores, weights, and table edits.
     * Requires typing 'y' so a stray keypress can't erase a semester.
     */
    static class ResetScreen extends Screen {

        ResetScreen(GradeBook book, InputReader in) {
            super(book, in);
        }

        @Override
        public void show() {
            if (in.readLine("  type 'y' to wipe everything: ").equalsIgnoreCase("y")) {
                book.reset();
                System.out.println("  fresh semester, fresh start.");
            } else {
                System.out.println("  cancelled.");
            }
        }
    }
}