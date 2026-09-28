package escapex.observer;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: ENUMERATION (Type-Safe Constants)
 * ============================================================================
 * Why this matters in OOP:
 * An `enum` provides a strictly typed set of constant values.
 *
 * Before Java Enums, developers used raw integer constants (e.g., `int EVENT_ROOM_CHANGED = 1`).
 * That approach was error-prone because any invalid integer could be passed.
 *
 * Enums enforce compile-time type safety, self-documentation, and can even
 * have custom methods and fields!
 * ============================================================================
 */
public enum GameEventType {
    ROOM_CHANGED("Player moved to a different room sector"),
    ITEM_COLLECTED("An item was picked up and placed into inventory"),
    PUZZLE_SOLVED("A puzzle was successfully decoded and solved"),
    PUZZLE_FAILED("Incorrect solution attempted on a puzzle"),
    DOOR_UNLOCKED("All puzzles in the sector are solved; the blast door opened"),
    TIMER_TICK("The countdown timer ticked by one second"),
    TIME_EXPIRED("The countdown timer ran out; containment failure"),
    HINT_REQUESTED("A hint was requested by the player"),
    SCORE_UPDATED("The player's score was modified"),
    GAME_WON("The player escaped all 5 sectors successfully!"),
    GAME_LOADED("A saved game state was restored");

    private final String description;

    GameEventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
