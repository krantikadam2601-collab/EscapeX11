package escapex.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: MEMENTO PATTERN & SERIALIZATION
 * ============================================================================
 * Why this matters in OOP:
 * The Memento Pattern captures and externalizes an object's internal state
 * so that the object can be restored to this state later, without violating
 * encapsulation.
 *
 * `GameState` bundles the entire runtime state of the game:
 *   - The active `Player`
 *   - The player's `Inventory`
 *   - The state of all 5 `Room` instances (which puzzles are solved, which items picked up)
 *   - Current room index
 *   - Timer countdown seconds remaining
 *
 * Because every referenced class implements `Serializable`, Java's object graph
 * serialization can write this complete snapshot to disk in a single method call!
 * ============================================================================
 */
public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Player player;
    private final Inventory inventory;
    private final List<Room> rooms;
    private int currentRoomIndex;
    private int secondsRemaining;
    private int secondsElapsed;
    private boolean gameCompleted;
    private final long saveTimestamp;

    public GameState(Player player, Inventory inventory, List<Room> rooms,
                     int currentRoomIndex, int secondsRemaining, int secondsElapsed,
                     boolean gameCompleted) {
        this.player = player;
        this.inventory = inventory;
        this.rooms = new ArrayList<>(rooms);
        this.currentRoomIndex = currentRoomIndex;
        this.secondsRemaining = secondsRemaining;
        this.secondsElapsed = secondsElapsed;
        this.gameCompleted = gameCompleted;
        this.saveTimestamp = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public int getCurrentRoomIndex() {
        return currentRoomIndex;
    }

    public void setCurrentRoomIndex(int currentRoomIndex) {
        this.currentRoomIndex = currentRoomIndex;
    }

    public int getSecondsRemaining() {
        return secondsRemaining;
    }

    public void setSecondsRemaining(int secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }

    public int getSecondsElapsed() {
        return secondsElapsed;
    }

    public void setSecondsElapsed(int secondsElapsed) {
        this.secondsElapsed = secondsElapsed;
    }

    public boolean isGameCompleted() {
        return gameCompleted;
    }

    public void setGameCompleted(boolean gameCompleted) {
        this.gameCompleted = gameCompleted;
    }

    public long getSaveTimestamp() {
        return saveTimestamp;
    }
}
