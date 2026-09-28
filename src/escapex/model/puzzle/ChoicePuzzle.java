package escapex.model.puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: INHERITANCE & COMPOSITION WITHIN SPECIALIZED CLASS
 * ============================================================================
 * Why this matters in OOP:
 * `ChoicePuzzle` extends `Puzzle` to provide a multiple-choice diagnostic dilemma.
 *
 * In addition to the base puzzle properties, it encapsulates a list of options (A, B, C, D)
 * and the zero-based index or letter of the correct choice.
 *
 * This shows how a derived class can:
 * 1. Extend the state of the parent class by adding its own member fields (`options`, `correctIndex`).
 * 2. Specialize the abstract behavior `checkAnswer(String input)`.
 * ============================================================================
 */
public class ChoicePuzzle extends Puzzle {

    private static final long serialVersionUID = 1L;

    private final List<String> options;
    private final int correctIndex; // 0 for Option A, 1 for Option B, etc.

    public ChoicePuzzle(String id, String title, String description, String prompt,
                        PuzzleDifficulty difficulty, List<String> hints,
                        List<String> options, int correctIndex) {
        super(id, title, description, prompt, difficulty, hints);
        this.options = new ArrayList<>(options != null ? options : Collections.emptyList());
        this.correctIndex = correctIndex;
    }

    @Override
    protected boolean checkAnswer(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        String clean = input.trim().toUpperCase();

        // Check if player entered a single letter: A, B, C, D...
        if (clean.length() == 1) {
            char ch = clean.charAt(0);
            int idxFromLetter = ch - 'A';
            if (idxFromLetter >= 0 && idxFromLetter < options.size()) {
                return idxFromLetter == correctIndex;
            }
        }

        // Check if player entered a 1-based number: "1", "2", "3", "4"
        try {
            int num = Integer.parseInt(clean);
            if (num >= 1 && num <= options.size()) {
                return (num - 1) == correctIndex;
            }
        } catch (NumberFormatException ignored) {
            // Not a raw number, continue to string match
        }

        // Check if player entered the exact option text
        if (correctIndex >= 0 && correctIndex < options.size()) {
            String correctText = options.get(correctIndex);
            return clean.equalsIgnoreCase(correctText.trim().toUpperCase());
        }

        return false;
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    @Override
    public String getPuzzleTypeDescription() {
        return "Multiple Choice Analysis (Choose the correct protocol/directive)";
    }
}
