package escapex.observer;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: ENCAPSULATION & IMMUTABILITY (Value Object)
 * ============================================================================
 * Why this matters in OOP:
 * A `GameEvent` acts as a data carrier between the game engine (subject)
 * and the user interface (observer).
 *
 * It demonstrates:
 * 1. Encapsulation: All event data is bundled together in one cohesive object.
 * 2. Immutability: All fields are `final` with only getters (no setters).
 *    Once created, an event cannot be tampered with by any listener, preventing
 *    subtle side-effects across threads.
 * ============================================================================
 */
public class GameEvent {

    private final GameEventType type;
    private final String message;
    private final Object payload;
    private final long timestamp;

    /**
     * Constructs an immutable GameEvent.
     *
     * @param type What category of event happened (from the enum).
     * @param message Human-readable status description for UI terminal logging.
     * @param payload Optional relevant object (e.g. an Item, Puzzle, or Room).
     */
    public GameEvent(GameEventType type, String message, Object payload) {
        this.type = type;
        this.message = message;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }

    public GameEvent(GameEventType type, String message) {
        this(type, message, null);
    }

    public GameEventType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Object getPayload() {
        return payload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + message;
    }
}
