package escapex.model.puzzle;

import java.util.Arrays;
import java.util.List;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: INHERITANCE & METHOD OVERRIDING (Polymorphism)
 * ============================================================================
 * Why this matters in OOP:
 * `CodeInputPuzzle` is-a `Puzzle`.
 *
 * It inherits all attributes and behaviors from `Puzzle` (title, difficulty, hints)
 * using `extends Puzzle` and `super(...)`.
 *
 * It overrides `checkAnswer(String input)` using `@Override` to implement
 * text-based passcode verification. It can accept a primary answer or a list of
 * valid aliases (for instance, case-insensitive or binary/decimal equivalents).
 * ============================================================================
 */
public class CodeInputPuzzle extends Puzzle {

    private static final long serialVersionUID = 1L;

    private final List<String> acceptableAnswers;
    private final boolean caseSensitive;

    /**
     * Constructs a CodeInputPuzzle with multiple valid answers.
     */
    public CodeInputPuzzle(String id, String title, String description, String prompt,
                           PuzzleDifficulty difficulty, List<String> hints,
                           List<String> acceptableAnswers, boolean caseSensitive) {
        super(id, title, description, prompt, difficulty, hints);
        this.acceptableAnswers = acceptableAnswers;
        this.caseSensitive = caseSensitive;
    }

    public CodeInputPuzzle(String id, String title, String description, String prompt,
                           PuzzleDifficulty difficulty, List<String> hints,
                           List<String> acceptableAnswers) {
        this(id, title, description, prompt, difficulty, hints, acceptableAnswers, false);
    }

    public CodeInputPuzzle(String id, String title, String description, String prompt,
                           PuzzleDifficulty difficulty, List<String> hints,
                           String singleAnswer) {
        this(id, title, description, prompt, difficulty, hints, Arrays.asList(singleAnswer), false);
    }

    @Override
    protected boolean checkAnswer(String input) {
        if (input == null || acceptableAnswers == null) {
            return false;
        }
        String cleanInput = input.trim();
        for (String validAnswer : acceptableAnswers) {
            if (caseSensitive) {
                if (validAnswer.trim().equals(cleanInput)) return true;
            } else {
                if (validAnswer.trim().equalsIgnoreCase(cleanInput)) return true;
            }
        }

        // Try numeric comparison if numeric
        try {
            double num = Double.parseDouble(cleanInput);
            for (String validAnswer : acceptableAnswers) {
                try {
                    double validNum = Double.parseDouble(validAnswer.trim());
                    if (Math.abs(num - validNum) < 1e-6) return true;
                } catch (NumberFormatException ignored) {}
            }
        } catch (NumberFormatException ignored) {}

        // Normalize alphanumeric characters (for phrases like "Prompt Injection" vs "Prompt-Injection")
        String normInput = cleanInput.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        for (String validAnswer : acceptableAnswers) {
            String normAnswer = validAnswer.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            if (!normAnswer.isEmpty() && normInput.equals(normAnswer)) return true;
        }

        return false;
    }

    @Override
    public String getPuzzleTypeDescription() {
        return "Terminal Passcode Entry (Direct alphanumeric or formula answer)";
    }
}
