package escapex.exception;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: INHERITANCE (Specialization of Domain Exceptions)
 * ============================================================================
 * Why this matters in OOP:
 * `RoomLockedException` extends our custom `EscapeXException`.
 * By specializing exceptions, calling code can choose to catch either:
 *   1. Specifically `RoomLockedException` (to show a door-shaking UI animation), or
 *   2. Generally `EscapeXException` (polymorphic catch block for any game error).
 *
 * This represents the "Is-A" relationship:
 * A `RoomLockedException` IS-A `EscapeXException` IS-A `Exception`.
 * ============================================================================
 */
public class RoomLockedException extends EscapeXException {

    private final int roomNumber;
    private final int puzzlesRemaining;

    /**
     * Constructs a RoomLockedException with room context.
     * Demonstrates encapsulation of contextual error data within an exception object.
     *
     * @param roomNumber The 1-based index of the room.
     * @param puzzlesRemaining How many puzzles still need to be solved to open the door.
     */
    public RoomLockedException(int roomNumber, int puzzlesRemaining) {
        super("Security Lockdown Active! Sector " + roomNumber + " door is sealed. " +
              puzzlesRemaining + " puzzle(s) remain unsolved.");
        this.roomNumber = roomNumber;
        this.puzzlesRemaining = puzzlesRemaining;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getPuzzlesRemaining() {
        return puzzlesRemaining;
    }
}
