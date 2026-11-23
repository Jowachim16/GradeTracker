import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The course being tracked: its components, their weights, and the
 * transmutation table.
 *
 * All grade arithmetic lives here, so the screens only have to display results.
 */
public class GradeBook {

    /** Weights must total this so the final grade stays on a 0-100 scale. */
    public static final double REQUIRED_WEIGHT_TOTAL = 100;

    // Decimal weights like 33.3 + 33.3 + 33.4 can't always add to exactly 100
    // in floating point, so allow a tiny margin.
    private static final double WEIGHT_TOLERANCE = 0.001;

    private final List<GradeComponent> components = new ArrayList<>();
    private final TransmutationTable table = new TransmutationTable();

    public GradeBook() {
        reset();
    }

    /**
     * Restores the default components and weights, clears all scores, and
     * restores the standard transmutation table. Components are rebuilt here
     * so the defaults are defined in one place.
     */
    public void reset() {
        components.clear();
        components.add(new MultiScoreComponent("Quizzes", 20, "quiz", "quizzes", 20));
        components.add(new SingleScoreComponent("Prelims", 15));
        components.add(new SingleScoreComponent("Midterms", 20));
        components.add(new SingleScoreComponent("Final Project", 20));
        components.add(new SingleScoreComponent("Final Exam", 25));
        table.restoreStandard();
    }

    public List<GradeComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }

    public TransmutationTable getTable() {
        return table;
    }

    /**
     * Applies new weights in component order.
     * Returns false and changes nothing if the total is not 100, so a rejected
     * attempt leaves the old weights in place.
     */
    public boolean setWeights(double[] newWeights) {
        double sum = 0;
        for (double w : newWeights) sum += w;
        if (Math.abs(sum - REQUIRED_WEIGHT_TOTAL) > WEIGHT_TOLERANCE) return false;

        for (int i = 0; i < components.size(); i++) {
            components.get(i).setWeight(newWeights[i]);
        }
        return true;
    }

    /** Points already locked in by graded components, out of 100. */
    public double earnedPoints() {
        double total = 0;
        for (GradeComponent c : components) total += c.contribution();
        return total;
    }

    /** Weight (%) of components that have at least one score. */
    public double gradedWeight() {
        double total = 0;
        for (GradeComponent c : components) {
            if (c.isGraded()) total += c.getWeight();
        }
        return total;
    }

    /** Weight (%) still waiting on scores. */
    public double pendingWeight() {
        return REQUIRED_WEIGHT_TOTAL - gradedWeight();
    }

    /** Names of components still waiting on scores, in table order. */
    public List<String> pendingNames() {
        List<String> names = new ArrayList<>();
        for (GradeComponent c : components) {
            if (!c.isGraded()) names.add(c.getName());
        }
        return names;
    }

    /**
     * Current standing: the average over graded components only, so it
     * reflects the work so far and isn't dragged down by missing components.
     */
    public double standing() {
        return gradedWeight() == 0 ? 0 : earnedPoints() / gradedWeight() * 100;
    }

    /** Lowest possible final percentage: every pending component scores 0. */
    public double floor() {
        return earnedPoints();
    }

    /** Highest possible final percentage: every pending component scores 100. */
    public double ceiling() {
        return earnedPoints() + pendingWeight();
    }

    /**
     * Average the remaining components must reach for the final grade to hit
     * the target grade point.
     *   - Zero or less: the target is already secured.
     *   - Above 100: the target can no longer be reached.
     * The caller must make sure pendingWeight() is greater than 0.
     */
    public double requiredAverageFor(double targetGradePoint) {
        double neededPercent = table.minimumFor(targetGradePoint);
        return (neededPercent - earnedPoints()) * 100 / pendingWeight();
    }
}