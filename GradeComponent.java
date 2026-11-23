import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for one graded part of the course (Quizzes, Midterms, ...).
 *
 * Everything every component shares (name, weight, averaging, and how many
 * points it contributes) is implemented here, so all components are
 * calculated the same way. Subclasses differ in two ways, and they express
 * that difference by overriding the methods marked below:
 *   - how scores are entered        -> enterScores()
 *   - how the score is summarised   -> menuLabel() (and scoreText() if needed)
 */
public abstract class GradeComponent {

    private final String name;
    private double weight;                                  // percent of final grade, 0-100
    private final List<Double> scores = new ArrayList<>();  // raw scores, each 0-100

    protected GradeComponent(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() { return name; }

    public double getWeight() { return weight; }

    public void setWeight(double weight) { this.weight = weight; }

    /** Read-only view, so callers can't bypass the rules in the subclasses. */
    public List<Double> getScores() {
        return Collections.unmodifiableList(scores);
    }

    /** Subclasses store new scores through this so the list is always replaced in one step. */
    protected void setScores(List<Double> newScores) {
        scores.clear();
        scores.addAll(newScores);
    }

    public boolean isGraded() {
        return !scores.isEmpty();
    }

    /** Mean of the raw scores (0-100). Returns 0 when nothing is graded yet. */
    public double average() {
        if (!isGraded()) return 0;
        double sum = 0;
        for (double s : scores) sum += s;
        return sum / scores.size();
    }

    /** Points this component adds to the final grade, out of 100. */
    public double contribution() {
        return isGraded() ? average() * weight / 100.0 : 0;
    }

    /** Average as text for the report's SCORE column, or "-" if ungraded. */
    public String scoreText() {
        return isGraded() ? String.format("%.2f", average()) : "-";
    }

    /** Short status shown in the score-entry menu. Subclasses may override to add detail. */
    public String menuLabel() {
        return isGraded() ? scoreText() : "- pending";
    }

    /**
     * Asks the user for this component's scores and stores them.
     * Each subclass has its own dialogue.
     *
     * @return a confirmation message for the caller to display
     */
    public abstract String enterScores(InputReader in);
}