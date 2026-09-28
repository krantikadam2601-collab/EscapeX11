package escapex.model.puzzle;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: INTERFACE CONTRACT (Capability Interface)
 * ============================================================================
 * Why this matters in OOP:
 * An interface allows us to specify a capability without forcing a specific
 * inheritance hierarchy.
 *
 * Any puzzle (or room object, or NPC) that implements `Hintable` guarantees
 * that it can provide hints to the player upon request, and tracks how many
 * hints have already been dispensed.
 *
 * This fulfills the "Interface Segregation" principle (the 'I' in SOLID):
 * Only classes that actually support hints need to implement this interface.
 * ============================================================================
 */
public interface Hintable {

    /**
     * Checks if another hint is available.
     * @return true if there are more hints left to reveal.
     */
    boolean hasMoreHints();

    /**
     * Retrieves the next available hint.
     * In an escape room, hints usually start subtle and get progressively more explicit.
     *
     * @return The text of the next hint.
     */
    String getNextHint();

    /**
     * Returns the total count of hints that have been unlocked so far.
     */
    int getHintsRevealedCount();
}
