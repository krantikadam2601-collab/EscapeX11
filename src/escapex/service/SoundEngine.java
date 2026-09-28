package escapex.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: SINGLETON PATTERN & RESOURCE ABSTRACTION
 * ============================================================================
 * Why this matters in OOP:
 * 1. Singleton Pattern:
 *    The `SoundEngine` manages computer audio hardware. Having multiple instances
 *    trying to acquire the same audio output device could cause conflicts.
 *    The Singleton pattern ensures exactly ONE centralized audio engine exists.
 *
 * 2. Pure JDK Audio Synthesis (Zero External Files!):
 *    Instead of relying on external .mp3/.wav files that might get lost,
 *    `SoundEngine` mathematically generates sound waves (sine and square waves)
 *    using standard `javax.sound.sampled.SourceDataLine`.
 *
 * 3. Asynchronous Execution:
 *    Uses an `ExecutorService` background worker thread so audio playback never
 *    stalls or freezes the Swing user interface.
 * ============================================================================
 */
public class SoundEngine {

    // Eagerly instantiated Singleton instance
    private static final SoundEngine INSTANCE = new SoundEngine();

    private final ExecutorService soundThread;
    private boolean soundEnabled;

    private SoundEngine() {
        this.soundThread = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "EscapeX-Audio-Worker");
            t.setDaemon(true); // Daemon thread won't prevent JVM shutdown
            return t;
        });
        this.soundEnabled = true;
    }

    public static SoundEngine getInstance() {
        return INSTANCE;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public void toggleSound() {
        this.soundEnabled = !this.soundEnabled;
    }

    // ========================================================================
    // SOUND EFFECTS (Mathematical Audio Synthesis)
    // ========================================================================

    /**
     * Subtle, tactile click for UI button interactions.
     */
    public void playClick() {
        playTone(900, 35, 0.25, WaveType.SINE);
    }

    /**
     * Bright dual-tone chirp when an item is discovered and collected.
     */
    public void playItemCollected() {
        if (!soundEnabled) return;
        soundThread.submit(() -> {
            generateToneSynchronous(523, 70, 0.4, WaveType.SINE); // C5
            generateToneSynchronous(784, 110, 0.5, WaveType.SINE); // G5
        });
    }

    /**
     * Triumphant harmonic arpeggio when a puzzle is correctly decoded.
     */
    public void playPuzzleSolved() {
        if (!soundEnabled) return;
        soundThread.submit(() -> {
            generateToneSynchronous(523, 80, 0.4, WaveType.SINE);  // C5
            generateToneSynchronous(659, 80, 0.4, WaveType.SINE);  // E5
            generateToneSynchronous(784, 80, 0.5, WaveType.SINE);  // G5
            generateToneSynchronous(1046, 180, 0.6, WaveType.SINE); // C6
        });
    }

    /**
     * Low square-wave electronic buzz indicating an invalid passcode or error.
     */
    public void playPuzzleFailed() {
        playTone(150, 200, 0.5, WaveType.SQUARE);
    }

    /**
     * Majestic progressive chord when all 3 puzzles are solved and the blast door opens.
     */
    public void playDoorUnlocked() {
        if (!soundEnabled) return;
        soundThread.submit(() -> {
            int[] notes = {440, 554, 659, 880, 1108}; // A Major fanfare
            for (int note : notes) {
                generateToneSynchronous(note, 90, 0.45, WaveType.SINE);
            }
        });
    }

    /**
     * Warning chime when the countdown clock is running low.
     */
    public void playAlarm() {
        playTone(880, 90, 0.4, WaveType.SINE);
    }

    /**
     * Victorious fanfare when escaping all 5 sectors.
     */
    public void playVictory() {
        if (!soundEnabled) return;
        soundThread.submit(() -> {
            int[] chords = {523, 659, 784, 1046, 784, 1046, 1318};
            for (int freq : chords) {
                generateToneSynchronous(freq, 140, 0.5, WaveType.SINE);
            }
        });
    }

    // ========================================================================
    // LOW-LEVEL PCM AUDIO SYNTHESIS
    // ========================================================================

    private enum WaveType { SINE, SQUARE }

    private void playTone(int frequencyHz, int durationMs, double volume, WaveType wave) {
        if (!soundEnabled) return;
        soundThread.submit(() -> generateToneSynchronous(frequencyHz, durationMs, volume, wave));
    }

    private void generateToneSynchronous(int frequencyHz, int durationMs, double volume, WaveType wave) {
        final float sampleRate = 44100f;
        int numSamples = (int) (sampleRate * (durationMs / 1000.0));
        byte[] pcmData = new byte[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double angle = 2.0 * Math.PI * i / (sampleRate / frequencyHz);
            double sampleValue;

            if (wave == WaveType.SQUARE) {
                sampleValue = Math.sin(angle) >= 0 ? 1.0 : -1.0;
            } else {
                sampleValue = Math.sin(angle);
            }

            // Apply quick attack and decay envelope to prevent audio clicking/pops
            double envelope = 1.0;
            int fadeSamples = (int)(sampleRate * 0.005); // 5ms fade
            if (i < fadeSamples) {
                envelope = (double) i / fadeSamples;
            } else if (i > numSamples - fadeSamples) {
                envelope = (double) (numSamples - i) / fadeSamples;
            }

            pcmData[i] = (byte) (sampleValue * volume * envelope * 127);
        }

        AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format);
            line.start();
            line.write(pcmData, 0, pcmData.length);
            line.drain();
        } catch (LineUnavailableException | IllegalArgumentException e) {
            // Audio hardware may not be available; ignore silently in headless environments
        }
    }
}
