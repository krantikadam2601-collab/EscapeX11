package escapex.exception;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: INHERITANCE & EXCEPTION HIERARCHY
 * ============================================================================
 * Why this matters in OOP:
 * In Java, creating custom exception classes by inheriting from {@link Exception}
 * allows us to define application-specific error conditions.
 *
 * Instead of throwing generic exceptions like `RuntimeException`, having a base
 * `EscapeXException` lets our game logic catch and handle errors specific to
 * the Escape Room domain while preserving the standard Java exception mechanics.
 * ============================================================================
 */
public class EscapeXException extends Exception {

    /**
     * Constructs a new EscapeXException with the specified detail message.
     * Demonstrates using `super(message)` to pass information up the inheritance tree.
     *
     * @param message Human-readable explanation of what went wrong.
     */
    public EscapeXException(String message) {
        super(message);
    }

    /**
     * Constructs a new EscapeXException with a message and an underlying cause.
     * Demonstrates "Exception Chaining" (wrapping low-level exceptions like IOException).
     *
     * @param message Human-readable explanation.
     * @param cause The root cause exception.
     */
    public EscapeXException(String message, Throwable cause) {
        super(message, cause);
    }
}
