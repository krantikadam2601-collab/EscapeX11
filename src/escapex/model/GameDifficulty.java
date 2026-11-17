package escapex.model;

import java.io.Serializable;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: ENUM WITH ENCAPSULATED ATTRIBUTES & METHODS
 * ============================================================================
 * Why this matters in OOP:
 * The `GameDifficulty` enum defines the simulation difficulty presets.
 *
 * Each constant encapsulates:
 *   - Display label
 *   - Countdown duration in total seconds
 *   - Narrative description of the containment protocol
 *
 * Encapsulating these rules directly inside the enum ensures high cohesion
 * and avoids scattering raw integer constants across the codebase.
 * ============================================================================
 */
public enum GameDifficulty implements Serializable {
    EASY("Easy", 45 * 60, "45 Minutes - Extended containment inspection protocol for novice operatives"),
    NORMAL("Normal", 30 * 60, "30 Minutes - Standard automated lockdown procedure"),
    HARD("Hard", 20 * 60, "20 Minutes - Critical breach with rapid containment collapse");

    private final String label;
    private final int totalSeconds;
    private final String description;

    GameDifficulty(String label, int totalSeconds, String description) {
        this.label = label;
        this.totalSeconds = totalSeconds;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public String getDescription() {
        return description;
    }

    public String getFormattedDuration() {
        return (totalSeconds / 60) + " min";
    }

    @Override
    public String toString() {
        return label + " (" + getFormattedDuration() + ")";
    }
}
