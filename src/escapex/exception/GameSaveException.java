package escapex.exception;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: INHERITANCE & EXCEPTION TRANSLATION
 * ============================================================================
 * Why this matters in OOP:
 * Low-level I/O operations throw java.io.IOException or ClassNotFoundException.
 * If our UI directly catches IOException, it couples UI code to file storage details.
 *
 * By translating low-level exceptions into `GameSaveException`, we maintain
 * high cohesion and loose coupling (a key OOP principle), abstracting the storage
 * layer from the presentation layer.
 * ============================================================================
 */
public class GameSaveException extends EscapeXException {

    public GameSaveException(String message) {
        super(message);
    }

    public GameSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
