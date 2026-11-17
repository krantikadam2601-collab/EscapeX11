package escapex;

import escapex.service.GameEngine;
import escapex.ui.EscapeXFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: APPLICATION BOOTSTRAPPING & THREAD DELEGATION
 * ============================================================================
 * Why this matters in OOP:
 * The `Main` class serves as the single entry point (Bootstrap) of the EscapeX
 * application.
 *
 * 1. Single Responsibility Principle (SRP):
 *    The `Main` class does not manage rooms, puzzles, or scores. Its sole
 *    responsibility is to initialize the Swing Event Dispatch Thread (EDT)
 *    and launch the application window.
 *
 * 2. Swing Thread Safety (Concurrency):
 *    In Java Swing, all GUI creation, manipulation, and event handling MUST take
 *    place on the Event Dispatch Thread (EDT). Using `SwingUtilities.invokeLater()`
 *    ensures our UI is created safely without thread race conditions.
 *
 * 3. Non-Blocking Launch & Lifecycle:
 *    The application launches cleanly into the futuristic Welcome / Main Menu screen
 *    without raw modal prompts, and the countdown timer begins only after the
 *    operative initiates a New Game session.
 * ============================================================================
 */
public class Main {

    public static void main(String[] args) {
        // Set system look and feel for native window decorations
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default Swing look and feel
        }

        // Launch on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Instantiate Game Controller (Facade) and View
            GameEngine gameEngine = new GameEngine();
            EscapeXFrame frame = new EscapeXFrame(gameEngine);
            frame.setVisible(true);
        });
    }
}
