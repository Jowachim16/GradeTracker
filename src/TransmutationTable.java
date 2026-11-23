import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts a raw percentage into a grade point.
 *
 * The table is a list of rows meaning "a score of at least X% earns grade point Y".
 * Rows run from best (1.00) to worst (5.00), so the first row a score qualifies
 * for is the answer. LinkedHashMap keeps that order, and the row order is what
 * the lookup and the edit limits depend on.
 */
public class TransmutationTable {

    /**
     * The failing grade. It is the fallback for anything below every other row,
     * so it is fixed and cannot be edited.
     */
    public static final double FAIL_GRADE = 5.00;

    private static final Map<Double, String> REMARKS = new HashMap<>();
    static {
        REMARKS.put(1.00, "EXCELLENT");
        REMARKS.put(1.25, "OUTSTANDING");
        REMARKS.put(1.50, "VERY GOOD");
        REMARKS.put(1.75, "GOOD");
        REMARKS.put(2.00, "SATISFACTORY");
        REMARKS.put(2.25, "FAIRLY GOOD");
        REMARKS.put(2.50, "FAIR");
        REMARKS.put(2.75, "MARGINAL");
        REMARKS.put(3.00, "PASSING");
    }

    private final LinkedHashMap<Double, Integer> rows = new LinkedHashMap<>();

    public TransmutationTable() {
        restoreStandard();
    }

    /**
     * Restores the standard Philippine scale. Changes the rows in place so any
     * code holding a reference to this table sees the reset.
     */
    public void restoreStandard() {
        rows.clear();
        rows.put(1.00, 97);  rows.put(1.25, 94);  rows.put(1.50, 91);
        rows.put(1.75, 88);  rows.put(2.00, 85);  rows.put(2.25, 82);
        rows.put(2.50, 79);  rows.put(2.75, 76);  rows.put(3.00, 75);
        rows.put(FAIL_GRADE, 0);
    }

    /** Returns the grade point for a raw percentage (0-100). */
    public double gradePointFor(double percent) {
        for (Map.Entry<Double, Integer> row : rows.entrySet()) {
            if (percent >= row.getValue()) return row.getKey();
        }
        // Unreachable while the failing row is 0%; kept as a safety net.
        return FAIL_GRADE;
    }

    /** Verbal remark for a grade point, such as "PASSING". */
    public static String remark(double gradePoint) {
        return REMARKS.getOrDefault(gradePoint, "FAILED");
    }

    /** Grade points in table order, best first. */
    public List<Double> gradePoints() {
        return new ArrayList<>(rows.keySet());
    }

    /** Minimum percentage needed for a grade point. The grade point must exist. */
    public int minimumFor(double gradePoint) {
        return rows.get(gradePoint);
    }

    /**
     * A valid target for the ideal score calculator: any row except the failing
     * grade, since aiming to fail is not a useful target.
     */
    public boolean isValidTarget(double gradePoint) {
        return rows.containsKey(gradePoint) && isEditable(gradePoint);
    }

    public boolean isEditable(double gradePoint) {
        return gradePoint != FAIL_GRADE;
    }

    public void setMinimum(double gradePoint, int minimum) {
        rows.put(gradePoint, minimum);
    }

    /**
     * Lowest value a row can be set to without overlapping the row below it.
     * Rows must stay strictly descending, or the lookup would give wrong answers.
     */
    public int lowestAllowed(double gradePoint) {
        List<Double> keys = gradePoints();
        int i = keys.indexOf(gradePoint);
        return (i + 1 < keys.size()) ? rows.get(keys.get(i + 1)) + 1 : 1;
    }

    /** Highest value a row can be set to without overlapping the row above it. */
    public int highestAllowed(double gradePoint) {
        List<Double> keys = gradePoints();
        int i = keys.indexOf(gradePoint);
        return (i > 0) ? rows.get(keys.get(i - 1)) - 1 : 100;
    }
}