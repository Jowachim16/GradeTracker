import java.util.Collections;

/**
 * A component that takes one score, such as a midterm or final exam.
 * Entering -1 clears the score, which lets the user undo a mistake.
 */
public class SingleScoreComponent extends GradeComponent {

    public SingleScoreComponent(String name, double weight) {
        super(name, weight);
    }

    @Override
    public String enterScores(InputReader in) {
        double score = in.readDouble(
                "  score for " + getName() + " (0-100, -1 clears): ", -1, 100);

        if (score < 0) {
            setScores(Collections.emptyList());
            return "  cleared.";
        }
        setScores(Collections.singletonList(score));
        return String.format("  saved - %s = %.2f (earns %.2f pts)",
                getName(), score, contribution());
    }
}