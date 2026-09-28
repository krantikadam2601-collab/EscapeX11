package escapex.service;

import escapex.exception.EscapeXException;
import escapex.exception.GameSaveException;
import escapex.exception.RoomLockedException;
import escapex.model.GameState;
import escapex.model.Inventory;
import escapex.model.Item;
import escapex.model.Player;
import escapex.model.Room;
import escapex.model.RoomObject;
import escapex.model.puzzle.ItemCombinationPuzzle;
import escapex.model.puzzle.Puzzle;
import escapex.observer.GameEvent;
import escapex.observer.GameEventType;
import escapex.observer.GameEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: FACADE PATTERN, CONTROLLER (MVC) & OBSERVER DISPATCHER
 * ============================================================================
 * Why this matters in OOP:
 * 1. Facade Pattern:
 *    The `GameEngine` acts as a unified, high-level facade over the entire game domain.
 *    Instead of the Swing UI having to manage rooms, items, timers, scores, and audio
 *    separately, it interacts solely with `GameEngine`.
 *
 * 2. Controller in MVC:
 *    - Model: `Room`, `Puzzle`, `Player`, `Inventory` (State & Rules)
 *    - View: Swing components (`EscapeXFrame`, `RoomViewPanel`)
 *    - Controller: `GameEngine` (Coordinates state mutations and updates views)
 *
 * 3. Observer Pattern Dispatcher:
 *    Maintains a list of `GameEventListener` observers and notifies them when events
 *    occur, ensuring zero direct coupling between game logic and the GUI!
 * ============================================================================
 */
public class GameEngine implements GameTimer.TickCallback {

    private final RoomFactory roomFactory;
    private final SaveLoadService saveLoadService;
    private final HighScoreService highScoreService;
    private final SoundEngine soundEngine;

    private final List<GameEventListener> listeners;

    private Player player;
    private Inventory inventory;
    private List<Room> rooms;
    private int currentRoomIndex;
    private GameTimer gameTimer;
    private boolean gameWon;
    private boolean gameOver;

    public GameEngine() {
        this.roomFactory = new RoomFactory();
        this.saveLoadService = new SaveLoadService();
        this.highScoreService = new HighScoreService();
        this.soundEngine = SoundEngine.getInstance();
        this.listeners = new ArrayList<>();

        startNewGame("Cadet Operative");
    }

    // ========================================================================
    // GAME LIFECYCLE METHODS
    // ========================================================================

    /**
     * Initializes a brand new game session.
     */
    public void startNewGame(String playerName) {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        this.player = new Player(playerName);
        this.inventory = new Inventory(Inventory.DEFAULT_MAX_CAPACITY);
        this.rooms = roomFactory.createAllRooms();
        this.currentRoomIndex = 0;
        this.gameWon = false;
        this.gameOver = false;

        this.gameTimer = new GameTimer(this);
        this.gameTimer.start();

        dispatchEvent(new GameEvent(
                GameEventType.ROOM_CHANGED,
                "Welcome to EscapeX, " + player.getName() + "! Simulation initiated in " + getCurrentRoom().getName() + ".",
                getCurrentRoom()
        ));
    }

    // ========================================================================
    // EXPLORATION & INVENTORY (The "Explore -> Collect" Flow)
    // ========================================================================

    /**
     * Interacts with an object in the current room.
     */
    public String exploreObject(String objectId) {
        Room current = getCurrentRoom();
        Optional<RoomObject> target = current.findObject(objectId);

        if (target.isEmpty()) {
            return "Object not found in current sector.";
        }

        RoomObject obj = target.get();
        String resultText = obj.investigate();
        soundEngine.playClick();

        // Check if there is an item to collect
        if (obj.hasCollectibleItem()) {
            Item itemToCollect = obj.getHiddenItem();
            if (itemToCollect != null) {
                if (inventory.addItem(itemToCollect)) {
                    obj.harvestItem(); // Safely mark harvested ONLY after successful add!
                    soundEngine.playItemCollected();
                    player.addScore(50); // Exploration reward
                    dispatchEvent(new GameEvent(
                            GameEventType.ITEM_COLLECTED,
                            "Discovered: " + itemToCollect.getName() + "! Added to inventory. (+50 pts)",
                            itemToCollect
                    ));
                    dispatchEvent(new GameEvent(
                            GameEventType.SCORE_UPDATED,
                            "Score updated: " + player.getScore() + " pts",
                            player.getScore()
                    ));
                } else {
                    dispatchEvent(new GameEvent(
                            GameEventType.ITEM_COLLECTED,
                            "Inventory is full! Could not store: " + itemToCollect.getName() + ". (Capacity: " + inventory.getMaxCapacity() + ")",
                            itemToCollect
                    ));
                }
            }
        }

        return resultText;
    }

    // ========================================================================
    // PUZZLE SOLVING (The "Solve -> Escape" Flow)
    // ========================================================================

    /**
     * Submits a proposed solution for a puzzle in the current room.
     * Demonstrates Polymorphism: works identically for Code, Choice, Pattern, and Item puzzles!
     */
    public boolean solvePuzzle(String puzzleId, String input) {
        if (gameOver || gameWon) return false;

        Room room = getCurrentRoom();
        Optional<Puzzle> puzzleOpt = room.getPuzzles().stream()
                .filter(p -> p.getId().equalsIgnoreCase(puzzleId))
                .findFirst();

        if (puzzleOpt.isEmpty()) {
            return false;
        }

        Puzzle puzzle = puzzleOpt.get();
        if (puzzle.isSolved()) {
            return true; // Already solved
        }

        boolean solved;
        // Check if this is an ItemCombinationPuzzle requiring an item
        if (puzzle instanceof ItemCombinationPuzzle itemPuzzle) {
            if (!itemPuzzle.hasRequiredItem(inventory)) {
                soundEngine.playPuzzleFailed();
                dispatchEvent(new GameEvent(
                        GameEventType.PUZZLE_FAILED,
                        "Missing required hardware item: " + itemPuzzle.getRequiredItemName() + " in your inventory!",
                        puzzle
                ));
                return false;
            }
            solved = itemPuzzle.attemptWithInventory(inventory, input);
        } else {
            solved = puzzle.attemptSolution(input);
        }

        if (solved) {
            int scoreEarned = puzzle.calculateScore();
            player.addScore(scoreEarned);
            player.recordPuzzleSolved();
            gameTimer.addBonusSeconds(puzzle.getDifficulty().getTimeBonusSeconds());

            soundEngine.playPuzzleSolved();

            dispatchEvent(new GameEvent(
                    GameEventType.PUZZLE_SOLVED,
                    "SUCCESS! Puzzle decoded: " + puzzle.getTitle() + " (+" + scoreEarned + " pts, +" +
                    puzzle.getDifficulty().getTimeBonusSeconds() + "s bonus)",
                    puzzle
            ));

            dispatchEvent(new GameEvent(
                    GameEventType.SCORE_UPDATED,
                    "Score: " + player.getScore() + " pts",
                    player.getScore()
            ));

            // Check if all 3 puzzles are now cleared
            if (room.updateDoorStatus()) {
                soundEngine.playDoorUnlocked();
                dispatchEvent(new GameEvent(
                        GameEventType.DOOR_UNLOCKED,
                        "LOCKDOWN OVERRIDDEN! All 3 diagnostics in " + room.getName() + " resolved. Blast door is OPEN!",
                        room
                ));
            }
            return true;
        } else {
            soundEngine.playPuzzleFailed();
            dispatchEvent(new GameEvent(
                    GameEventType.PUZZLE_FAILED,
                    "ACCESS DENIED! Invalid solution code for: " + puzzle.getTitle() + " (Attempt #" + puzzle.getAttemptsCount() + ")",
                    puzzle
            ));
            return false;
        }
    }

    /**
     * Requests a hint for a puzzle.
     */
    public String requestHint(String puzzleId) {
        Room room = getCurrentRoom();
        Optional<Puzzle> puzzleOpt = room.getPuzzles().stream()
                .filter(p -> p.getId().equalsIgnoreCase(puzzleId))
                .findFirst();

        if (puzzleOpt.isEmpty()) {
            return "Puzzle not found.";
        }

        Puzzle puzzle = puzzleOpt.get();
        if (!puzzle.hasMoreHints()) {
            return "No more diagnostic hints available for this puzzle.";
        }

        String hint = puzzle.getNextHint();
        player.recordHintUsed();
        player.deductScore(15); // Small penalty for hint usage
        soundEngine.playClick();

        dispatchEvent(new GameEvent(
                GameEventType.HINT_REQUESTED,
                "Hint requested for [" + puzzle.getTitle() + "]: " + hint + " (-15 pts)",
                hint
        ));
        dispatchEvent(new GameEvent(
                GameEventType.SCORE_UPDATED,
                "Score: " + player.getScore() + " pts",
                player.getScore()
        ));

        return hint;
    }

    // ========================================================================
    // SECTOR PROGRESSION & ESCAPE
    // ========================================================================

    /**
     * Moves to the next sector if the current room's door is unlocked.
     * @throws RoomLockedException if puzzles remain unsolved.
     */
    public void moveToNextRoom() throws RoomLockedException {
        Room current = getCurrentRoom();
        if (!current.isDoorUnlocked()) {
            soundEngine.playPuzzleFailed();
            throw new RoomLockedException(current.getRoomNumber(), current.getRemainingPuzzlesCount());
        }

        player.recordRoomCleared();
        player.addScore(200); // Room clear bonus

        if (currentRoomIndex < rooms.size() - 1) {
            currentRoomIndex++;
            Room nextRoom = getCurrentRoom();
            soundEngine.playDoorUnlocked();

            dispatchEvent(new GameEvent(
                    GameEventType.ROOM_CHANGED,
                    "Moved to Sector 0" + nextRoom.getRoomNumber() + ": " + nextRoom.getName() + " (+200 pts bonus)",
                    nextRoom
            ));
        } else {
            // Player escaped the 5th and final room!
            this.gameWon = true;
            this.gameOver = true;
            gameTimer.stop();

            // Time remaining bonus: 2 points per second remaining!
            int timeBonus = gameTimer.getSecondsRemaining() * 2;
            player.addScore(timeBonus);

            soundEngine.playVictory();

            // Record to High Score Leaderboard
            highScoreService.recordScore(
                    player.getName(),
                    player.getScore(),
                    player.getRoomsClearedCount(),
                    gameTimer.getSecondsElapsed()
            );

            dispatchEvent(new GameEvent(
                    GameEventType.GAME_WON,
                    "VICTORY! All 5 quarantine sectors breached! Facility containment released. Final Score: " +
                    player.getScore() + " pts! (Time bonus: +" + timeBonus + " pts)",
                    player
            ));
        }
    }

    // ========================================================================
    // SAVE & LOAD PROGRESSION
    // ========================================================================

    public void saveGame(File file) throws GameSaveException {
        GameState state = new GameState(
                this.player,
                this.inventory,
                this.rooms,
                this.currentRoomIndex,
                this.gameTimer.getSecondsRemaining(),
                this.gameTimer.getSecondsElapsed(),
                this.gameWon
        );
        saveLoadService.saveGame(state, file);
        soundEngine.playItemCollected();
        dispatchEvent(new GameEvent(GameEventType.GAME_LOADED, "Simulation progress saved successfully."));
    }

    public void loadGame(File file) throws GameSaveException {
        GameState state = saveLoadService.loadGame(file);

        if (this.gameTimer != null) {
            this.gameTimer.stop();
        }

        this.player = state.getPlayer();
        this.inventory = state.getInventory();
        this.rooms = state.getRooms();
        this.currentRoomIndex = state.getCurrentRoomIndex();
        this.gameWon = state.isGameCompleted();
        this.gameOver = false;

        this.gameTimer = new GameTimer(this);
        this.gameTimer.setSecondsRemaining(state.getSecondsRemaining());
        this.gameTimer.setSecondsElapsed(state.getSecondsElapsed());
        if (!gameWon) {
            this.gameTimer.start();
        }

        soundEngine.playDoorUnlocked();
        dispatchEvent(new GameEvent(
                GameEventType.GAME_LOADED,
                "Simulation progress restored! Current sector: " + getCurrentRoom().getName()
        ));
        dispatchEvent(new GameEvent(
                GameEventType.ROOM_CHANGED,
                "Restored to Sector 0" + getCurrentRoom().getRoomNumber() + ": " + getCurrentRoom().getName(),
                getCurrentRoom()
        ));
        dispatchEvent(new GameEvent(
                GameEventType.SCORE_UPDATED,
                "Score: " + player.getScore() + " pts",
                player.getScore()
        ));
    }

    // ========================================================================
    // TIMER CALLBACK IMPLEMENTATION
    // ========================================================================

    @Override
    public void onTick(int secondsRemaining, int secondsElapsed) {
        dispatchEvent(new GameEvent(
                GameEventType.TIMER_TICK,
                GameTimer.formatDigitalTime(secondsRemaining),
                secondsRemaining
        ));

        // Play warning alarm at 120 and 60 seconds
        if (secondsRemaining == 120 || secondsRemaining == 60) {
            soundEngine.playAlarm();
        }
    }

    @Override
    public void onTimeExpired() {
        this.gameOver = true;
        soundEngine.playPuzzleFailed();
        dispatchEvent(new GameEvent(
                GameEventType.TIME_EXPIRED,
                "CONTAINMENT FAILURE! Countdown reached zero. Facility locked down permanently."
        ));
    }

    // ========================================================================
    // OBSERVER DISPATCH MECHANISM
    // ========================================================================

    public void addListener(GameEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    private void dispatchEvent(GameEvent event) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onGameEvent(event);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }

    // ========================================================================
    // GETTERS & STATUS ACCESSORS
    // ========================================================================

    public Room getCurrentRoom() {
        return rooms.get(currentRoomIndex);
    }

    public int getCurrentRoomIndex() {
        return currentRoomIndex;
    }

    public int getTotalRoomsCount() {
        return rooms.size();
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public List<Room> getAllRooms() {
        return rooms;
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public HighScoreService getHighScoreService() {
        return highScoreService;
    }

    public SoundEngine getSoundEngine() {
        return soundEngine;
    }
}
