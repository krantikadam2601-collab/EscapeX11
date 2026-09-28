package escapex.service;

import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: ENCAPSULATION & DELEGATION (Swing Timer Wrapper)
 * ============================================================================
 * Why this matters in OOP:
 * The `GameTimer` encapsulates the countdown clock of EscapeX.
 *
 * Instead of forcing the UI to manage raw threads or `javax.swing.Timer` details,
 * `GameTimer` wraps the Swing Timer and exposes clean domain operations:
 *   - `start()`, `pause()`, `resume()`, `stop()`, `addTime(seconds)`.
 *
 * Because it internally delegates to `javax.swing.Timer`, every 1-second tick
 * runs safely on the Swing Event Dispatch Thread (EDT), preventing thread-safety
 * glitches without requiring manual synchronization.
 * ============================================================================
 */
public class GameTimer {

    public interface TickCallback {
        void onTick(int secondsRemaining, int secondsElapsed);
        void onTimeExpired();
    }

    public static final int DEFAULT_START_SECONDS = 1800; // 30 minutes countdown

    private final Timer swingTimer;
    private final TickCallback callback;
    private int secondsRemaining;
    private int secondsElapsed;
    private boolean running;

    public GameTimer(int initialSeconds, TickCallback callback) {
        this.secondsRemaining = Math.max(1, initialSeconds);
        this.secondsElapsed = 0;
        this.callback = callback;
        this.running = false;

        // Ticks once every 1,000 milliseconds (1 second)
        this.swingTimer = new Timer(1000, e -> handleOneSecondTick());
        this.swingTimer.setRepeats(true);
    }

    public GameTimer(TickCallback callback) {
        this(DEFAULT_START_SECONDS, callback);
    }

    private void handleOneSecondTick() {
        if (!running) return;

        secondsElapsed++;
        secondsRemaining--;

        if (callback != null) {
            callback.onTick(secondsRemaining, secondsElapsed);
        }

        if (secondsRemaining <= 0) {
            stop();
            if (callback != null) {
                callback.onTimeExpired();
            }
        }
    }

    public void start() {
        running = true;
        swingTimer.start();
    }

    public void pause() {
        running = false;
        swingTimer.stop();
    }

    public void resume() {
        if (secondsRemaining > 0) {
            running = true;
            swingTimer.start();
        }
    }

    public void stop() {
        running = false;
        swingTimer.stop();
    }

    /**
     * Adds bonus seconds to the clock (e.g. when solving a difficult puzzle).
     */
    public void addBonusSeconds(int seconds) {
        if (seconds > 0) {
            this.secondsRemaining += seconds;
        }
    }

    public int getSecondsRemaining() {
        return secondsRemaining;
    }

    public void setSecondsRemaining(int seconds) {
        this.secondsRemaining = seconds;
    }

    public int getSecondsElapsed() {
        return secondsElapsed;
    }

    public void setSecondsElapsed(int seconds) {
        this.secondsElapsed = seconds;
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Formats seconds into a stylish digital clock display: MM:SS or HH:MM:SS.
     */
    public static String formatDigitalTime(int totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
