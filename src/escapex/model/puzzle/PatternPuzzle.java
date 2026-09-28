package escapex.model.puzzle;

import java.util.List;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: INHERITANCE & ALGORITHMIC VALIDATION
 * ============================================================================
 * Why this matters in OOP:
 * `PatternPuzzle` models puzzles involving sequences, binary matrices, or
 * directional sequences (e.g. "UP, RIGHT, DOWN" or "10110").
 *
 * It demonstrates:
 * 1. Polymorphic Behavior: The UI calls `puzzle.attemptSolution()` without
 *    needing to know whether it's validating a text password, a choice, or a pattern!
 * 2. Normalization: Removes spaces, dashes, or commas to allow user input flexibility
 *    (e.g., "1, 0, 1" is treated the same as "101").
 * ============================================================================
 */
public class PatternPuzzle extends Puzzle {

    private static final long serialVersionUID = 1L;

    private final String targetPatternNormalized;

    public PatternPuzzle(String id, String title, String description, String prompt,
                         PuzzleDifficulty difficulty, List<String> hints,
                         String targetPattern) {
        super(id, title, description, prompt, difficulty, hints);
        this.targetPatternNormalized = normalize(targetPattern);
    }

    private static String normalize(String str) {
        if (str == null) return "";
        // Strip spaces, hyphens, commas, colons
        return str.replaceAll("[\\s,\\-_:]+", "").toUpperCase();
    }

    @Override
    protected boolean checkAnswer(String input) {
        if (input == null) return false;
        String normalizedInput = normalize(input);
        return normalizedInput.equals(targetPatternNormalized);
    }

    @Override
    public String getPuzzleTypeDescription() {
        return "Pattern / Sequence Alignment (Enter sequence or binary array)";
    }
}
