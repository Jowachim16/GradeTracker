import java.util.ArrayList;
import java.util.List;

/**
 * A component that holds several scores, such as quizzes.
 *
 * The scores are averaged exactly like any other component. What's different
 * is that the user first states how many items there are, then enters each
 * one, and the menu also shows how many were taken.
 */
public class MultiScoreComponent extends GradeComponent {

    private final String itemSingular;  // e.g. "quiz"
    private final String itemPlural;    // e.g. "quizzes"
    private final int maxCount;         // upper limit on how many items can be entered

    public MultiScoreComponent(String name, double weight,
                               String itemSingular, String itemPlural, int maxCount) {
        super(name, weight);
        this.itemSingular = itemSingular;
        this.itemPlural = itemPlural;
        this.maxCount = maxCount;
    }

    /** Overrides the base label to add the count, e.g. "87.50 (x4)". */
    @Override
    public String menuLabel() {
        return isGraded()
                ? scoreText() + " (x" + getScores().size() + ")"
                : super.menuLabel();
    }

    @Override
    public String enterScores(InputReader in) {
        int count = in.readInt(
                "  how many " + itemPlural + "? (1-" + maxCount + "): ", 1, maxCount);

        // Read every score before storing any, so the old scores are only replaced
        // once the full set has been collected.
        List<Double> entered = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            entered.add(in.readDouble(
                    "  " + itemSingular + " #" + i + " score (0-100): ", 0, 100));
        }
        setScores(entered);

        return String.format("  saved - %d %s score(s), average %.2f",
                count, itemSingular, average());
    }
}