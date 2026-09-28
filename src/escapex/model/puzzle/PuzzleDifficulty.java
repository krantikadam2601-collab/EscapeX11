package escapex.model.puzzle;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: ENUM WITH STATE & BEHAVIOR
 * ============================================================================
 * Why this matters in OOP:
 * In Java, `enum` is much more than a list of strings or numbers. It is a full class!
 *
 * Here, `PuzzleDifficulty` demonstrates:
 * 1. Enums can have fields (`baseScore`, `timeBonusSeconds`, `label`).
 * 2. Enums can have constructors and methods.
 * 3. Encapsulates difficulty-based game balancing rules directly inside the enum
 *    instead of scattering arbitrary `if (diff == "HARD")` statements everywhere.
 * ============================================================================
 */
public enum PuzzleDifficulty {
    EASY("Level 1: Novice", 100, 30),
    MEDIUM("Level 2: Intermediate", 250, 45),
    HARD("Level 3: Expert", 500, 60);

    private final String label;
    private final int baseScore;
    private final int timeBonusSeconds;

    PuzzleDifficulty(String label, int baseScore, int timeBonusSeconds) {
        this.label = label;
        this.baseScore = baseScore;
        this.timeBonusSeconds = timeBonusSeconds;
    }

    public String getLabel() {
        return label;
    }

    public int getBaseScore() {
        return baseScore;
    }

    public int getTimeBonusSeconds() {
        return timeBonusSeconds;
    }

    @Override
    public String toString() {
        return label + " (" + baseScore + " pts)";
    }
}
