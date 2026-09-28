package escapex.observer;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: INTERFACES & OBSERVER DESIGN PATTERN
 * ============================================================================
 * Why this matters in OOP:
 * An `interface` defines a strict behavioral contract without dictating HOW
 * that behavior is implemented.
 *
 * In the Observer Pattern:
 * - The GameEngine is the "Subject" (it publishes events).
 * - Any class implementing `GameEventListener` is an "Observer" (it consumes events).
 *
 * This achieves "Loose Coupling":
 * The GameEngine does not need to know anything about Swing, JFrames, or audio!
 * It only knows that some objects implement `GameEventListener`.
 * This makes the core game logic testable even in command-line or headless mode.
 * ============================================================================
 */
public interface GameEventListener {

    /**
     * Called whenever a game event is published by the game engine.
     * Implementing classes (like Swing UI panels or SoundEngine) react appropriately.
     *
     * @param event The event details (type, message, and attached data).
     */
    void onGameEvent(GameEvent event);
}
