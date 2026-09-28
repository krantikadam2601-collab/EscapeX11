package escapex.model;

import escapex.model.puzzle.Puzzle;
import escapex.model.puzzle.PuzzleDifficulty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: COMPOSITION & DELEGATION
 * ============================================================================
 * Why this matters in OOP:
 * A `Room` does not inherit from `Puzzle` or `Item`. Instead, it USES them!
 *
 * 1. Composition (Strong "Has-A" relationship):
 *    - A Room HAS-A list of `Puzzle` objects (Easy, Medium, Hard).
 *    - A Room HAS-A list of `RoomObject` interactive hotspots.
 *
 * 2. Delegation:
 *    When asked `isDoorUnlocked()`, the `Room` delegates to its composed `Puzzle`
 *    collection, asking each puzzle if it `isSolved()`.
 *
 * 3. State Management:
 *    A room cannot be exited until all 3 puzzles are solved, keeping game rules
 *    cohesive and self-contained within this model class.
 * ============================================================================
 */
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int roomNumber;
    private final String name;
    private final String subtitle;
    private final String loreDescription;
    private final String visualThemeColorHex;

    private final List<Puzzle> puzzles;
    private final List<RoomObject> roomObjects;
    private boolean doorUnlocked;

    /**
     * Constructs a new Room sector.
     */
    public Room(int roomNumber, String name, String subtitle, String loreDescription,
                String visualThemeColorHex, List<Puzzle> puzzles, List<RoomObject> roomObjects) {
        this.roomNumber = roomNumber;
        this.name = name;
        this.subtitle = subtitle;
        this.loreDescription = loreDescription;
        this.visualThemeColorHex = visualThemeColorHex;
        this.puzzles = new ArrayList<>(puzzles != null ? puzzles : Collections.emptyList());
        this.roomObjects = new ArrayList<>(roomObjects != null ? roomObjects : Collections.emptyList());
        this.doorUnlocked = false;
    }

    // --- Business / State Check Methods ---

    /**
     * Checks if all 3 puzzles in this room are solved.
     */
    public boolean areAllPuzzlesSolved() {
        if (puzzles.isEmpty()) return true;
        for (Puzzle p : puzzles) {
            if (!p.isSolved()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Unlocks the door if all 3 puzzles have been resolved.
     * @return true if the door state transitioned to unlocked.
     */
    public boolean updateDoorStatus() {
        if (areAllPuzzlesSolved() && !doorUnlocked) {
            this.doorUnlocked = true;
            return true;
        }
        return false;
    }

    public int getSolvedPuzzlesCount() {
        int count = 0;
        for (Puzzle p : puzzles) {
            if (p.isSolved()) count++;
        }
        return count;
    }

    public int getRemainingPuzzlesCount() {
        return puzzles.size() - getSolvedPuzzlesCount();
    }

    /**
     * Finds a puzzle by difficulty level.
     */
    public Optional<Puzzle> getPuzzleByDifficulty(PuzzleDifficulty difficulty) {
        return puzzles.stream()
                .filter(p -> p.getDifficulty() == difficulty)
                .findFirst();
    }

    public Optional<RoomObject> findObject(String objectId) {
        return roomObjects.stream()
                .filter(obj -> obj.getId().equalsIgnoreCase(objectId))
                .findFirst();
    }

    // --- Getters ---

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getName() {
        return name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getLoreDescription() {
        return loreDescription;
    }

    public String getVisualThemeColorHex() {
        return visualThemeColorHex;
    }

    public List<Puzzle> getPuzzles() {
        return Collections.unmodifiableList(puzzles);
    }

    public List<RoomObject> getRoomObjects() {
        return Collections.unmodifiableList(roomObjects);
    }

    public boolean isDoorUnlocked() {
        return doorUnlocked;
    }

    public void setDoorUnlocked(boolean doorUnlocked) {
        this.doorUnlocked = doorUnlocked;
    }

    @Override
    public String toString() {
        return "Sector 0" + roomNumber + ": " + name + " [" + getSolvedPuzzlesCount() + "/" + puzzles.size() + " Puzzles]";
    }
}
