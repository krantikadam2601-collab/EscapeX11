package escapex.model;

import java.io.Serializable;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: ENCAPSULATION & BUSINESS RULE VALIDATION
 * ============================================================================
 * Why this matters in OOP:
 * The `Player` class encapsulates the agent's identity, score, statistics,
 * and current progression inside the simulation.
 *
 * It enforces business invariants:
 * 1. Score can never drop below zero.
 * 2. Player name is sanitized and defaults gracefully if empty.
 * 3. Mutation occurs through meaningful methods (`addScore`, `recordHintUsed`)
 *    rather than naked setter calls (`setScore(score + 10)`), which preserves
 *    domain logic integrity.
 * ============================================================================
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private int score;
    private int hintsUsedTotal;
    private int puzzlesSolvedTotal;
    private int roomsClearedCount;

    public Player(String name) {
        setName(name);
        this.score = 0;
        this.hintsUsedTotal = 0;
        this.puzzlesSolvedTotal = 0;
        this.roomsClearedCount = 0;
    }

    public Player() {
        this("Agent Operative");
    }

    // --- Domain Mutation Methods ---

    /**
     * Adds points to the player's total score.
     * @param points Points to add (ignored if negative).
     */
    public void addScore(int points) {
        if (points > 0) {
            this.score += points;
        }
    }

    /**
     * Deducts points from score (e.g. hint penalty), ensuring score >= 0.
     */
    public void deductScore(int penalty) {
        if (penalty > 0) {
            this.score = Math.max(0, this.score - penalty);
        }
    }

    public void recordHintUsed() {
        this.hintsUsedTotal++;
    }

    public void recordPuzzleSolved() {
        this.puzzlesSolvedTotal++;
    }

    public void recordRoomCleared() {
        this.roomsClearedCount++;
    }

    // --- Getters & Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            this.name = "Operative " + (int)(Math.random() * 900 + 100);
        } else {
            this.name = name.trim();
        }
    }

    public int getScore() {
        return score;
    }

    public int getHintsUsedTotal() {
        return hintsUsedTotal;
    }

    public int getPuzzlesSolvedTotal() {
        return puzzlesSolvedTotal;
    }

    public int getRoomsClearedCount() {
        return roomsClearedCount;
    }

    @Override
    public String toString() {
        return name + " [Score: " + score + " | Rooms Escaped: " + roomsClearedCount + "]";
    }
}
