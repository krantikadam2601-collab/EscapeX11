package escapex.model.puzzle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: ABSTRACTION, INHERITANCE & TEMPLATE METHOD PATTERN
 * ============================================================================
 * Why this matters in OOP:
 * 1. Abstraction:
 *    `Puzzle` is an `abstract` class. You cannot directly instantiate `new Puzzle()`.
 *    Instead, it defines the common contract and shared state that ALL puzzles share,
 *    such as `title`, `description`, `difficulty`, and `hints`.
 *
 * 2. Template Method Pattern:
 *    The concrete method `attemptSolution(String input)` defines the standardized
 *    workflow for attempting a puzzle (tracking attempts, validating input, updating
 *    solved status). Inside it, it calls the abstract method `checkAnswer(String input)`,
 *    letting subclasses provide the exact answer-validation logic!
 *
 * 3. Polymorphism:
 *    Different puzzle subclasses (Code, Choice, Pattern, Item Combination) can all be
 *    stored together in a `List<Puzzle>` and treated uniformly by the game engine.
 * ============================================================================
 */
public abstract class Puzzle implements Hintable, Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String title;
    private final String description;
    private final String prompt;
    private final PuzzleDifficulty difficulty;
    private final List<String> hints;

    private boolean solved;
    private int hintsRevealedCount;
    private int attemptsCount;

    /**
     * Base constructor initializing common puzzle fields.
     */
    public Puzzle(String id, String title, String description, String prompt,
                  PuzzleDifficulty difficulty, List<String> hints) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.prompt = prompt;
        this.difficulty = difficulty != null ? difficulty : PuzzleDifficulty.EASY;
        this.hints = new ArrayList<>(hints != null ? hints : Collections.emptyList());
        this.solved = false;
        this.hintsRevealedCount = 0;
        this.attemptsCount = 0;
    }

    // ========================================================================
    // ABSTRACT METHODS (Must be implemented by subclasses - Polymorphism!)
    // ========================================================================

    /**
     * Subclasses must provide their own validation algorithm.
     * For example, a Code puzzle compares strings; a Choice puzzle matches option IDs;
     * a Pattern puzzle validates a sequence.
     *
     * @param input Raw user input from the UI.
     * @return true if correct, false otherwise.
     */
    protected abstract boolean checkAnswer(String input);

    /**
     * Returns a human-friendly description of the puzzle mechanism
     * (e.g. "Direct Terminal Code Input", "Multiple Choice Dilemma", "Item Synergizer").
     */
    public abstract String getPuzzleTypeDescription();

    // ========================================================================
    // TEMPLATE METHOD: attemptSolution
    // ========================================================================

    /**
     * Template method executing the standard validation pipeline.
     *
     * @param rawInput The player's proposed answer.
     * @return true if the answer was correct and the puzzle is now marked solved.
     */
    public final boolean attemptSolution(String rawInput) {
        if (solved) {
            return true; // Already solved
        }
        attemptsCount++;

        boolean correct = checkAnswer(rawInput);
        if (correct) {
            this.solved = true;
        }
        return correct;
    }

    // ========================================================================
    // HINTABLE INTERFACE IMPLEMENTATION
    // ========================================================================

    @Override
    public boolean hasMoreHints() {
        return hintsRevealedCount < hints.size();
    }

    @Override
    public String getNextHint() {
        if (hasMoreHints()) {
            String hint = hints.get(hintsRevealedCount);
            hintsRevealedCount++;
            return hint;
        }
        return "No further diagnostic hints available for this puzzle.";
    }

    @Override
    public int getHintsRevealedCount() {
        return hintsRevealedCount;
    }

    // ========================================================================
    // GETTERS & ENCAPSULATED STATE
    // ========================================================================

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrompt() {
        return prompt;
    }

    public PuzzleDifficulty getDifficulty() {
        return difficulty;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public int getAttemptsCount() {
        return attemptsCount;
    }

    /**
     * Calculates net score awarded for solving this puzzle,
     * deducting a penalty for each hint requested.
     */
    public int calculateScore() {
        int base = difficulty.getBaseScore();
        int hintPenalty = hintsRevealedCount * 25;
        return Math.max(20, base - hintPenalty);
    }

    public List<String> getAllHintsDefensive() {
        return Collections.unmodifiableList(hints);
    }

    @Override
    public String toString() {
        return "[" + difficulty.name() + "] " + title + (solved ? " (SOLVED)" : " (LOCKED)");
    }
}
