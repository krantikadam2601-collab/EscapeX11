package escapex.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: COMPARABLE INTERFACE, STREAMS & FILE PERSISTENCE
 * ============================================================================
 * Why this matters in OOP:
 * 1. Comparable Interface:
 *    The inner class `ScoreEntry` implements `Comparable<ScoreEntry>`.
 *    This defines the "Natural Ordering" of scores (higher scores rank first).
 *    Once implemented, standard methods like `Collections.sort()` work automatically!
 *
 * 2. Encapsulation & File I/O:
 *    `HighScoreService` encapsulates the file operations for saving and reading
 *    the leaderboard to/from a CSV file (`escapex_scores.csv`).
 *
 * 3. Try-with-resources:
 *    Demonstrates Java's standard resource management idiom to ensure file handles
 *    are closed properly even if exceptions occur.
 * ============================================================================
 */
public class HighScoreService {

    private static final String HIGH_SCORE_FILE = "escapex_scores.csv";

    /**
     * Inner Value Class representing an immutable score record.
     */
    public static class ScoreEntry implements Comparable<ScoreEntry> {
        private final String playerName;
        private final int score;
        private final int roomsCleared;
        private final String timeFormatted;
        private final String date;

        public ScoreEntry(String playerName, int score, int roomsCleared, String timeFormatted, String date) {
            this.playerName = playerName;
            this.score = score;
            this.roomsCleared = roomsCleared;
            this.timeFormatted = timeFormatted;
            this.date = date;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }

        public int getRoomsCleared() {
            return roomsCleared;
        }

        public String getTimeFormatted() {
            return timeFormatted;
        }

        public String getDate() {
            return date;
        }

        /**
         * Overriding compareTo: Sorts in descending order (highest score first).
         */
        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score);
        }
    }

    /**
     * Loads all historical scores, sorts them, and returns top records.
     */
    public List<ScoreEntry> loadScores() {
        List<ScoreEntry> scores = new ArrayList<>();
        File file = new File(HIGH_SCORE_FILE);
        if (!file.exists()) {
            return scores;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    try {
                        String name = parts[0].trim();
                        int score = Integer.parseInt(parts[1].trim());
                        int rooms = Integer.parseInt(parts[2].trim());
                        String time = parts[3].trim();
                        String date = parts[4].trim();
                        scores.add(new ScoreEntry(name, score, rooms, time, date));
                    } catch (NumberFormatException ignored) {
                        // Skip corrupted row
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read high scores: " + e.getMessage());
        }

        Collections.sort(scores);
        return scores;
    }

    /**
     * Records a new score entry to disk.
     */
    public void recordScore(String playerName, int score, int roomsCleared, int totalSecondsElapsed) {
        String timeStr = GameTimer.formatDigitalTime(totalSecondsElapsed);
        String dateStr = LocalDate.now().toString();

        ScoreEntry newEntry = new ScoreEntry(playerName, score, roomsCleared, timeStr, dateStr);

        List<ScoreEntry> existing = loadScores();
        existing.add(newEntry);
        Collections.sort(existing);

        // Keep top 20 scores
        if (existing.size() > 20) {
            existing = existing.subList(0, 20);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.write("# PlayerName,Score,RoomsCleared,TimeElapsed,Date\n");
            for (ScoreEntry entry : existing) {
                writer.write(String.format("%s,%d,%d,%s,%s\n",
                        entry.getPlayerName().replace(",", " "),
                        entry.getScore(),
                        entry.getRoomsCleared(),
                        entry.getTimeFormatted(),
                        entry.getDate()));
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not write high scores: " + e.getMessage());
        }
    }
}
