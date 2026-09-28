# EscapeX Project Analysis

## 1. Project Overview

**EscapeX** is a desktop puzzle-adventure game built entirely in pure Java. The player assumes the role of an operative trapped in a high-security AI research facility placed under automated quarantine lockdown. To escape, the player must navigate through 5 successive sectors by exploring room fixtures, collecting tools and clues, inspecting them in an inventory system, and solving 3 diagnostic puzzles per sector (Easy, Medium, Hard) to disengage security blast door deadbolts.

### Main Gameplay Flow
1. **Explore**: The player inspects interactive room stations/fixtures on an interactive 2D canvas or via the exploration interface to discover clues, notes, and collectible physical items.
2. **Collect & Inspect**: Discovered tools and documents are added to a paginated inventory (up to 16 items capacity). The player opens an item analyzer dialog to inspect hidden inscriptions, frequencies, codes, and circuit notes.
3. **Solve**: The player engages 3 locks per sector (Easy, Medium, Hard). Puzzles encompass boolean logic, memory registers, ciphers, matrix transposition, neural network perceptron math, quantum gates, Bell entanglement, AI safety theory, and physical item-synergy mechanics requiring specific tools from the inventory.
4. **Disengage & Progress**: Each solved puzzle retracts a physical lock bolt on the bulkhead blast door. When all 3 locks are disengaged, the blast door unlocks, allowing advancement to the next sector. Clearing Sector 05 achieves final escape and facility evacuation.

### Main Features
* **5 Thematic Sectors**: Cybernetics Workshop, Cryptographic Vault, Neural Core, Quantum Nexus, and Singularity Mainframe.
* **15 Diverse Puzzles**: Code entry, multiple choice, pattern matrix transposition, and tool-synergy puzzles.
* **Paginated Inventory System**: 16-item capacity with an 8-slot paginated 2x4 visual rack, auto-page switching, and tool inspection dialogs.
* **Procedural Synthesizer Audio**: Pure Java software sound engine generating clicks, alarms, pickups, door unlocks, and victory chimes without external media files.
* **State Persistence (Save/Load)**: Full game state snapshot serialization using the Memento pattern.
* **Scoring & Real-Time Countdown**: Countdown timer with bonus time rewards, attempt penalties, hint deductions, and persistent leaderboard saved to CSV.
* **In-Game OOP Architecture Guide**: An interactive pedagogical viewer mapping object-oriented programming principles directly to game classes.

### Current Implementation Status
* **100% Implemented and Verified**: All 5 sectors, 15 room objects, 15 inventory items, and 15 puzzles are fully functional.
* **Headless Integration Test Passing**: Automated smoke tests (`SmokeTest.java`) verify 100% of room transitions, full 15-item inventory collection, puzzle solvers, and save/load serialization.

---

## 2. Technology Stack

| Technology | Category | Where Used | Description / Purpose |
| ---------- | -------- | ---------- | --------------------- |
| **Java 17+ (JDK 26)** | Programming Language | Entire Codebase | Core language. Utilizes modern Java features (records/pattern matching, enhanced switch, streams, optionals, serialization). |
| **Java Swing (`javax.swing.*`)** | GUI Framework | `escapex.ui.*`, `escapex.ui.components.*` | Desktop window rendering, dialogs, custom components (`RoomViewPanel`, `InventoryPanel`, `SciFiButton`). |
| **Java 2D (`java.awt.*`, `java.awt.Graphics2D`)** | Graphics & Rendering | `RoomViewPanel.java`, `Theme.java` | Procedural vector drawing of chamber walls, security blast doors, animated lock bolts, station cards, and layout management. |
| **Java Sound API (`javax.sound.sampled.*`)** | Audio Synthesis | `SoundEngine.java` | Pure software sound generator using dynamic byte buffers (`SourceDataLine`) to synthesize sine waves, sweeps, and melodic chords. |
| **Java Object Serialization (`java.io.*`)** | Persistence / Memento | `SaveLoadService.java`, `GameState.java` | Deep binary object serialization (`ObjectOutputStream` / `ObjectInputStream`) saving full game graphs to `.dat` files. |
| **Java NIO & File I/O (`java.io.*`, `java.nio.file.*`)** | Data Persistence | `HighScoreService.java` | CSV parsing and writing for leaderboard scores (`escapex_scores.csv`). |

*No third-party libraries, Maven, or Gradle dependencies are used.*

---

## 3. Project Structure

```
e:\EscapeX11\
├── .gitignore                          # Excludes build binaries, save files, and IDE metadata
├── README.md                           # Project manual and student OOP study guide
├── PROJECT_ANALYSIS.md                 # Technical project documentation and system analysis
├── compile.bat                         # Batch build script compiling Java sources to bin/
├── run.bat                             # Batch launcher executing escapex.Main
├── test.bat                            # Batch test runner executing escapex.SmokeTest
├── build_and_run.ps1                   # PowerShell alternative build and run script
├── escapex_scores.csv                  # Persisted leaderboard high scores
├── src\
│   └── escapex\
│       ├── Main.java                   # Application entry point configuring UI and EDT
│       ├── SmokeTest.java              # Headless end-to-end verification and integration test suite
│       ├── RenderSnapshot.java         # Test utility capturing off-screen GUI snapshots to PNG
│       ├── DialogTest.java             # Standalone test runner for dialog verification
│       ├── exception\
│       │   ├── EscapeXException.java   # Base domain checked exception
│       │   ├── RoomLockedException.java# Thrown when attempting transition with unsolved locks
│       │   └── GameSaveException.java  # Thrown on serialization or I/O failure during save/load
│       ├── model\
│       │   ├── Item.java               # Collectible tool/clue domain entity
│       │   ├── Inventory.java          # Aggregated item collection with capacity control
│       │   ├── Player.java             # Player identity, score tracking, and puzzle metrics
│       │   ├── Room.java               # Sector domain model managing puzzles and lock status
│       │   ├── RoomObject.java         # Interactive hotspot/fixture containing items and clues
│       │   ├── GameState.java          # Memento object capturing total session state
│       │   └── puzzle\
│       │       ├── Puzzle.java         # Abstract base puzzle defining template method pattern
│       │       ├── PuzzleDifficulty.java# Enum configuring score, time bonus, and penalty multipliers
│       │       ├── Hintable.java       # Interface for multi-tiered hint progression
│       │       ├── CodeInputPuzzle.java# Text/numeric passcode verification puzzle
│       │       ├── ChoicePuzzle.java   # Multiple-choice diagnostic puzzle
│       │       ├── PatternPuzzle.java  # Sequence and matrix transposition puzzle
│       │       └── ItemCombinationPuzzle.java # Synergy puzzle requiring inventory tool + code
│       ├── observer\
│       │   ├── GameEvent.java          # Immutable event carrier dispatched across components
│       │   ├── GameEventListener.java  # Observer callback interface
│       │   └── GameEventType.java      # Type-safe enum of all possible game events
│       ├── service\
│       │   ├── GameEngine.java         # Central Controller/Facade managing game loop and logic
│       │   ├── GameTimer.java          # Encapsulated countdown clock and tick delegate
│       │   ├── RoomFactory.java        # Factory constructing all 5 rooms, objects, and puzzles
│       │   ├── SaveLoadService.java    # Serialization service for binary game saves
│       │   ├── HighScoreService.java   # File service persisting high scores to CSV
│       │   └── SoundEngine.java        # Singleton procedural audio synthesizer
│       └── ui\
│           ├── Theme.java              # Design tokens (colors, typography, borders)
│           ├── EscapeXFrame.java       # Main JFrame orchestrating overall layout and HUD
│           ├── RoomViewPanel.java      # Custom 2D chamber canvas with interactive stations & door
│           ├── InventoryPanel.java     # Paginated 2x4 visual rack for item display and inspection
│           ├── TerminalPanel.java      # Terminal activity console logging game events
│           ├── PuzzleDialog.java       # Modal dialog for submitting puzzle solutions
│           ├── ClueInspectionDialog.java# Modal analyzer showing detailed item clues
│           ├── HighScoreDialog.java    # Modal leaderboard display dialog
│           ├── OopConceptsDialog.java  # Interactive educational OOP architecture reference
│           └── components\
│               ├── GlowPanel.java      # Reusable styled container panel with rounded borders
│               └── SciFiButton.java    # Custom styled JButton with hover transitions
```

---

## 4. Architecture

### Main Components and Communication
* **Model-View-Controller (MVC)**:
  * **Model** (`escapex.model.*`): Encapsulates game state entities (`Room`, `Puzzle`, `Item`, `Inventory`, `Player`, `GameState`) without references to UI classes.
  * **Controller** (`escapex.service.GameEngine`): Coordinates domain actions (`exploreObject`, `solvePuzzle`, `moveToNextRoom`, `saveGame`).
  * **View** (`escapex.ui.*`): Presentation layer rendering the game state (`EscapeXFrame`, `RoomViewPanel`, `InventoryPanel`, `TerminalPanel`).
* **Observer Pattern**:
  * `GameEngine` maintains a list of `GameEventListener` subscribers.
  * When state transitions occur (e.g., `ITEM_COLLECTED`, `PUZZLE_SOLVED`, `DOOR_UNLOCKED`, `TIMER_TICK`), the engine broadcasts an immutable `GameEvent`.
  * Views (`EscapeXFrame`, `InventoryPanel`, `TerminalPanel`) react independently without tight coupling.

### Game State Management
* Runtime state is centralized in `GameEngine`.
* When saving, `GameEngine` bundles the active `Player`, `Inventory`, the 5 `Room` instances (retaining puzzle completion and fixture harvest states), current room index, and remaining timer seconds into an immutable `GameState` memento.
* `SaveLoadService` serializes the `GameState` object graph directly to disk. On load, the graph is deserialized and injected back into `GameEngine`.

### Puzzle Mechanics
* Base abstract class `Puzzle` defines standard attributes (`id`, `title`, `description`, `prompt`, `difficulty`, `hints`) and tracks attempts.
* Implements the **Template Method Pattern**: `attemptSolution(String input)` validates state, increments attempt counters, calculates score penalties, marks the puzzle solved on success, and delegates answer verification to the abstract method `checkAnswer(String input)`.
* Derived classes (`CodeInputPuzzle`, `ChoicePuzzle`, `PatternPuzzle`, `ItemCombinationPuzzle`) implement specialized verification.

### Inventory System
* `Inventory` holds an internal `List<Item>` with a fixed capacity of 16 items.
* Aggregation and encapsulation prevent external direct list mutation; external code interacts via `addItem()`, `hasItem()`, `findItem()`, and `getItems()` (which returns an unmodifiable defensive copy).
* UI presentation (`InventoryPanel`) displays a 2x4 grid (8 slots per page). Two pages (`◄ Page 1/2 ►`) accommodate all 16 items without shrinking or cluttering the interface.
* Exploration safe-harvest logic ensures room fixtures only mark items harvested when `inventory.addItem()` successfully stores them.

### Sector & Room Progression
* Each sector is represented by a `Room` containing 3 puzzles and several `RoomObject` hotspots.
* `Room.updateDoorStatus()` checks `areAllPuzzlesSolved()`. Only when all 3 puzzles are cleared does `doorUnlocked` become `true`.
* `GameEngine.moveToNextRoom()` checks `currentRoom.isDoorUnlocked()`. If locked, it throws `RoomLockedException`. If unlocked, it advances `currentRoomIndex`, awards room clear bonus points, and fires a `ROOM_CHANGED` event.

### Architectural Patterns Present
1. **Model-View-Controller (MVC)**: Clean separation between data models, Swing UI, and `GameEngine` controller.
2. **Observer Pattern**: `GameEngine` acts as Subject; UI panels act as Observers via `GameEventListener`.
3. **Factory Method Pattern**: `RoomFactory` encapsulates creation and wiring of all rooms, fixtures, items, and puzzles.
4. **Template Method Pattern**: `Puzzle.attemptSolution()` defines the invariant puzzle workflow; subclasses override `checkAnswer()`.
5. **Memento Pattern**: `GameState` captures session snapshots for persistence via `SaveLoadService`.
6. **Singleton Pattern**: `SoundEngine.getInstance()` provides synchronized, single-instance audio management.

---

## 5. Important Code

| File | Class / Function | Purpose | Important Logic |
| ---- | ---------------- | ------- | --------------- |
| `GameEngine.java` | `GameEngine.exploreObject(String objectId)` | Processes hotspot exploration and item collection | Investigates object; checks `hasCollectibleItem()`; attempts `inventory.addItem()`; only calls `obj.harvestItem()` if add succeeds; awards 50 score; dispatches `ITEM_COLLECTED`. |
| `GameEngine.java` | `GameEngine.solvePuzzle(String puzzleId, String input)` | Evaluates player puzzle attempts | Polymorphically executes puzzle attempt; checks `hasRequiredItem()` for `ItemCombinationPuzzle`; updates score/timer bonus; calls `room.updateDoorStatus()`; fires events. |
| `GameEngine.java` | `GameEngine.moveToNextRoom()` | Handles progression between sectors | Validates `current.isDoorUnlocked()`; throws `RoomLockedException` if locked; advances room index; triggers `GAME_WON` when completing Sector 05. |
| `Puzzle.java` | `Puzzle.attemptSolution(String input)` | Template method for puzzle answering | Blocks re-solving if already solved; increments attempt count; calls abstract `checkAnswer(input)`; marks `solved = true` on success; returns result. |
| `ItemCombinationPuzzle.java` | `ItemCombinationPuzzle.checkAnswer(String input)` | Validates hardware synergy passcode | Compares input against acceptable solutions; applies numeric parsing with float tolerance (`Math.abs(val - sol) < 1e-6`); applies normalized alphanumeric fallback. |
| `Inventory.java` | `Inventory.addItem(Item item)` | Inserts an item into inventory | Validates non-null; checks `isFull()` against `maxCapacity` (16); prevents duplicate IDs; appends item to private list. |
| `InventoryPanel.java` | `InventoryPanel.refreshInventoryView()` | Renders paginated 8-slot visual rack | Computes total pages; renders 8 slots for current page (`currentPage * 8` to `+8`); adds item buttons or empty placeholder slots; updates page label and navigation buttons. |
| `Room.java` | `Room.updateDoorStatus()` | Disengages room blast door | Iterates through room puzzles; returns true and sets `doorUnlocked = true` if and only if all puzzles return `isSolved() == true`. |
| `RoomViewPanel.java` | `RoomViewPanel.paintComponent(Graphics g)` | Custom 2D rendering of room environment | Renders room boundary, security blast door with 3 lock bolt status indicators (LOCKED vs OPEN), and interactive station cards with examined status dots. |
| `SoundEngine.java` | `SoundEngine.playTone(double freq, int ms, double vol)` | Procedural sine wave sound generation | Allocates PCM byte buffer; calculates `Math.sin(...)` waveform at sample rate 44.1kHz; writes buffer directly to `SourceDataLine`. |
| `SaveLoadService.java` | `SaveLoadService.saveGame(GameState, File)` | Persists full session state | Opens `ObjectOutputStream`; writes `GameState` object graph; closes streams safely. |
| `HighScoreService.java` | `HighScoreService.loadScores()` | Reads and parses high score records | Reads `escapex_scores.csv` via `BufferedReader`; parses CSV lines into `ScoreRecord` objects; sorts descending by score using comparator. |

---

## 6. OOP & Data Structures

### Object-Oriented Programming Concepts

| Concept | Location in Codebase | One-Line Explanation |
| ------- | -------------------- | -------------------- |
| **Encapsulation** | `Inventory.java`, `Player.java`, `RoomObject.java` | Keeps internal data structures private, exposing mutation only through controlled domain methods (`addItem`, `addScore`). |
| **Inheritance** | `CodeInputPuzzle.java`, `ChoicePuzzle.java`, `PatternPuzzle.java`, `ItemCombinationPuzzle.java` extends `Puzzle.java` | Reuses core puzzle state, hints, and scoring logic while specializing answer verification. |
| **Polymorphism** | `GameEngine.java` line 188 (`puzzle.attemptSolution(input)`) | Treats all 4 puzzle types uniformly through the abstract `Puzzle` interface at runtime. |
| **Abstraction** | `Puzzle.java` (abstract class), `Hintable.java`, `GameEventListener.java` (interfaces) | Defines contracts for solution verification, hint retrieval, and event observation without coupling to implementation details. |
| **Aggregation (Has-A)** | `Inventory.java` has a `List<Item>`; `Room.java` has a `List<Puzzle>` | Entities exist independently but are managed as a cohesive unit by a container class. |
| **Information Hiding** | `Inventory.getItems()` | Returns `Collections.unmodifiableList(items)` (defensive copy) so external callers cannot mutate internal storage directly. |
| **Type-Safe Enumeration**| `PuzzleDifficulty.java`, `GameEventType.java` | Replaces error-prone raw integer constants with strongly-typed, self-documenting enum constants carrying custom methods. |
| **Custom Exceptions** | `RoomLockedException.java`, `GameSaveException.java` | Extends `Exception` to represent specific domain failure states cleanly separated from standard runtime errors. |

### Data Structures

| Data Structure | Location in Codebase | One-Line Explanation |
| -------------- | -------------------- | -------------------- |
| **`ArrayList<T>`** | `Inventory.java`, `Room.java`, `RoomFactory.java` | Provides dynamic, indexed in-memory storage for items, puzzles, and interactive objects with O(1) random access. |
| **`Collections.unmodifiableList`** | `Inventory.java`, `Room.java` | Wraps internal lists to provide read-only views for external UI components without leaking internal references. |
| **`Optional<T>`** | `Inventory.findItem()`, `Room.findObject()` | Expresses potential absence of entities cleanly without risking `NullPointerException`. |
| **`List<T>` (Interface)** | Parameter types across models and services | Follows the "program to an interface, not an implementation" principle for maximum flexibility. |
| **`byte[]` (Primitive Array)** | `SoundEngine.java` | Holds raw audio PCM samples generated dynamically for real-time sound playback via the Java Sound line. |

---

## 7. Game & Puzzle Flow

### End-to-End Progression Flow
```
[Player Action: Clicks Hotspot / Station Card]
       │
       ▼
[Interaction: RoomViewPanel / Toolbar triggers GameEngine.exploreObject(id)]
       │
       ▼
[Item/State Change: RoomObject investigated]
       ├─► Has Item? ──► inventory.addItem(item) ──► Success: obj.harvestItem() (Item stored)
       │                                         └── Full: Item remains on fixture safely
       └─► Dispatches ITEM_COLLECTED & SCORE_UPDATED (+50 pts) ──► InventoryPanel auto-refreshes
       │
       ▼
[Player Action: Opens Puzzle from Door Bolt or Sidebar]
       │
       ▼
[Puzzle Modal Dialog: PuzzleDialog displays prompt, difficulty, hints, tool badge]
       ├─► If ItemCombinationPuzzle: checks itemPuzzle.hasRequiredItem(inventory)
       │         ├─► Item Missing: Shows [TOOL MISSING] badge, disables input
       │         └─► Item Held:    Shows [TOOL READY] badge, enables code entry
       │
       ▼
[Player Submits Code / Answer: GameEngine.solvePuzzle(puzzleId, input)]
       │
       ▼
[Puzzle Validation: Puzzle.attemptSolution(input) -> checkAnswer(input)]
       ├─► Incorrect: Play error sound, increment attempts count, fire PUZZLE_FAILED
       │
       ▼ Correct:
[Completion: Marks puzzle solved, awards score & time bonus, fires PUZZLE_SOLVED]
       │
       ▼
[Door Update: room.updateDoorStatus() checks areAllPuzzlesSolved()]
       ├─► 1/3 or 2/3 Solved: Bolt updates to [OPEN] on RoomViewPanel canvas
       │
       ▼ All 3/3 Solved:
[Bulkhead Blast Door Disengages: Door transitions to UNLOCKED, fires DOOR_UNLOCKED]
       │
       ▼
[Player Action: Clicks Unlocked Blast Door or "Advance to Next Room"]
       │
       ▼
[Progression: GameEngine.moveToNextRoom()]
       ├─► Validates room.isDoorUnlocked()
       ├─► Awards room clear bonus (+200 pts)
       ├─► Increments currentRoomIndex
       ├─► If Sector < 5: Dispatches ROOM_CHANGED ──► Loads Next Chamber
       └─► If Sector == 5: Dispatches GAME_WON ──► Victory Screen & Leaderboard Record
```

---

### Complete Catalog of Implemented Puzzles

| Sector | Puzzle ID | Puzzle Title | Type / Difficulty | Required Item / Hotspot Source | Solution / Accepted Inputs |
| ------ | --------- | ------------ | ----------------- | ------------------------------ | -------------------------- |
| **01: Cybernetics Workshop** | `p1_1` | Diagnostic Gate Bus | Code Input (Easy) | None (Foundational Logic) | `1`, `HIGH`, `TRUE` (`(1 XOR 0) AND (0 OR 1) = 1`) |
| | `p1_2` | Memory Register Override | Code Input (Medium) | None (Binary Conversion) | `75`, `K` (`01001011` binary = 75 decimal / ASCII 'K') |
| | `p1_3` | Oscillator Frequency Calibration | Item Synergy (Hard) | `magnifier` (Optical Magnifier from Circuit Workbench) | `4096`, `4096 HZ`, `4096HZ` (Etched frequency: 4096 Hz) |
| **02: Cryptographic Vault** | `p2_1` | Caesar Shift Decryption | Code Input (Easy) | None / `dataslate` (Encrypted Data Slate) | `ESCAPE`, `ECPAET` (`HVFDSH` with shift -3 = ESCAPE) |
| | `p2_2` | Matrix Transposition Cipher | Pattern (Medium) | None / `cipher_disc` (Caesar Cipher Disc) | `CHNYEEPRT` (3x3 grid read column-by-column) |
| | `p2_3` | Photonic Optical Sensor Lock | Item Synergy (Hard) | `laser_pointer` (Laser Pointer from Magnetic Tape Drives) | `MIRROR-45`, `MIRROR 45`, `MIRROR45` (Laser housing inscription) |
| **03: Neural Core** | `p3_1` | Perceptron Output Calculation | Code Input (Easy) | None / `activation_chart` (Activation Function Chart) | `3`, `3.0` (`ReLU((4*0.5)+(2*1.0)-1.0) = ReLU(3) = 3`) |
| | `p3_2` | Vanishing Gradient Dilemma | Multiple Choice (Medium)| None (Neural Network Theory) | Option Index `1` (`ReLU (Rectified Linear Unit)`) |
| | `p3_3` | Neural Convergence Injection | Item Synergy (Hard) | `weight_usb` (Synaptic Weight USB from Synaptic Column) | `0.001`, `1e-3`, `1E-3`, `.001`, `0.0010` (Target loss threshold) |
| **04: Quantum Nexus** | `p4_1` | Pauli-X Quantum Gate Transformation | Multiple Choice (Easy) | None (Quantum Computing Theory) | Option Index `1` (`\|1> (Flipped basis state)`) |
| | `p4_2` | Entangled Bell State Correlation | Code Input (Medium) | `entangle_log` (Entanglement Log from Telemetry Desk) | `-1`, `DOWN`, `-1.0` (Anti-correlated Bell state: A=+1 -> B=-1) |
| | `p4_3` | Superposition Phase Resonance | Item Synergy (Hard) | `polarizer` (Qubit Polarizer from Dilution Refrigerator) | `45`, `45 DEGREES`, `45 DEG`, `45°`, `45DEG` (Bezel angle: 45°) |
| **05: Singularity Mainframe** | `p5_1` | AI Alignment Core Principle | Multiple Choice (Easy) | None / `asimov_key` (Asimov Directive Chip) | Option Index `1` (`Ensure AI goals align with human intent & safety`) |
| | `p5_2` | Adversarial Attack Classification | Code Input (Medium) | `quarantine_doc` (Quarantine Report from Safety Audit Archive) | `Prompt Injection`, `Prompt-Injection`, `PromptInjection` |
| | `p5_3` | The Final Singularity Handshake | Item Synergy (Hard) | `master_token` (Master Override Token from Command Pedestal) | `ESCAPE-X`, `ESCAPE X`, `ESCAPEX` (Final escape handshake code) |
