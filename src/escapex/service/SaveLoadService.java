package escapex.service;

import escapex.exception.GameSaveException;
import escapex.model.GameState;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: OBJECT SERIALIZATION & EXCEPTION TRANSLATION
 * ============================================================================
 * Why this matters in OOP:
 * 1. Object Serialization:
 *    Java Serialization converts active in-memory object graphs (GameState,
 *    Player, Inventory, Rooms, Puzzles) into a persistent sequence of bytes.
 *    Restoring that byte stream recreates the exact object graph with all states
 *    intact!
 *
 * 2. Separation of Concerns & Single Responsibility Principle (SRP):
 *    The `Room` or `Player` classes don't need to know how they are saved to disk.
 *    `SaveLoadService` is solely responsible for storage and retrieval mechanics.
 *
 * 3. Exception Translation:
 *    Any low-level `IOException` or `ClassNotFoundException` is caught and
 *    translated into our domain exception `GameSaveException`, protecting higher-level
 *    callers from leaky abstractions.
 * ============================================================================
 */
public class SaveLoadService {

    public static final String DEFAULT_SAVE_FILENAME = "escapex_savegame.dat";

    /**
     * Persists the current GameState snapshot to a file.
     *
     * @param state The game snapshot to serialize.
     * @param targetFile Destination file.
     * @throws GameSaveException if writing to disk fails.
     */
    public void saveGame(GameState state, File targetFile) throws GameSaveException {
        if (state == null) {
            throw new IllegalArgumentException("GameState cannot be null for saving");
        }
        File destination = targetFile != null ? targetFile : new File(DEFAULT_SAVE_FILENAME);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(destination))) {
            oos.writeObject(state);
            oos.flush();
        } catch (IOException e) {
            throw new GameSaveException("Failed to save simulation progress: " + e.getMessage(), e);
        }
    }

    /**
     * Restores a serialized GameState snapshot from disk.
     *
     * @param sourceFile File to read from.
     * @return The reconstituted GameState object graph.
     * @throws GameSaveException if file is corrupted, missing, or incompatible.
     */
    public GameState loadGame(File sourceFile) throws GameSaveException {
        File file = sourceFile != null ? sourceFile : new File(DEFAULT_SAVE_FILENAME);
        if (!file.exists()) {
            throw new GameSaveException("No saved game file found at: " + file.getAbsolutePath());
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof GameState) {
                return (GameState) obj;
            } else {
                throw new GameSaveException("Corrupted save file: Unrecognized data format.");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new GameSaveException("Unable to restore saved game: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if a default saved game file exists.
     */
    public boolean hasDefaultSaveFile() {
        return new File(DEFAULT_SAVE_FILENAME).exists();
    }
}
