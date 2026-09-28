package escapex;

import escapex.exception.EscapeXException;
import escapex.model.Item;
import escapex.model.Room;
import escapex.model.puzzle.Puzzle;
import escapex.service.GameEngine;

import java.io.File;

/**
 * Headless Automated Verification & Smoke Test for EscapeX.
 * Verifies that all 5 rooms, 15 puzzles, inventory interactions,
 * serialization, and game completion work with 100% precision.
 */
public class SmokeTest {

    public static void main(String[] args) {
        System.out.println("=== STARTING ESCAPEX HEADLESS SMOKE TEST ===");

        GameEngine engine = new GameEngine();
        engine.startNewGame("Test Operative");

        // --- TEST SECTOR 01 ---
        Room r1 = engine.getCurrentRoom();
        assertEq(r1.getRoomNumber(), 1, "Initial room is Sector 01");
        assertEq(r1.getPuzzles().size(), 3, "Room 1 has 3 puzzles");

        // Explore all fixtures in Sector 01
        engine.exploreObject("obj_bench"); // Magnifier
        engine.exploreObject("obj_breaker"); // Keycard
        engine.exploreObject("obj_shelf"); // Logic manual
        assertTrue(engine.getInventory().hasItem("magnifier"), "Magnifier collected into inventory");
        assertTrue(engine.getInventory().hasItem("card_sec1"), "Keycard collected into inventory");
        assertTrue(engine.getInventory().hasItem("logic_manual"), "Logic manual collected into inventory");
        assertEq(engine.getInventory().size(), 3, "Inventory holds 3 items in Sector 01");

        // Solve Room 1 Puzzle 1 (Logic gate)
        boolean p1_1 = engine.solvePuzzle("p1_1", "1");
        assertTrue(p1_1, "Room 1 Puzzle 1 solved");

        // Solve Room 1 Puzzle 2 (Binary 01001011 -> 75 or K)
        boolean p1_2 = engine.solvePuzzle("p1_2", "75");
        assertTrue(p1_2, "Room 1 Puzzle 2 solved");

        // Solve Room 1 Puzzle 3 (Frequency calibration with magnifier -> 4096)
        boolean p1_3 = engine.solvePuzzle("p1_3", "4096");
        assertTrue(p1_3, "Room 1 Puzzle 3 solved");

        assertTrue(r1.isDoorUnlocked(), "Room 1 Blast Door is unlocked after solving 3 puzzles");

        // Test Serialization: Save and Load
        try {
            File testSave = new File("test_savegame.dat");
            engine.saveGame(testSave);
            assertTrue(testSave.exists(), "Save file exists on disk");

            engine.loadGame(testSave);
            assertEq(engine.getCurrentRoom().getRoomNumber(), 1, "Loaded back into Room 1");
            assertTrue(engine.getCurrentRoom().isDoorUnlocked(), "Loaded room maintains unlocked door state");
            assertTrue(engine.getInventory().hasItem("magnifier"), "Loaded inventory maintains magnifier");
            testSave.delete();
        } catch (EscapeXException e) {
            fail("Save/load error: " + e.getMessage());
        }

        // Advance to Sector 02
        try {
            engine.moveToNextRoom();
            assertEq(engine.getCurrentRoom().getRoomNumber(), 2, "Moved to Sector 02");
        } catch (EscapeXException e) {
            fail("Could not advance to Room 2: " + e.getMessage());
        }

        // --- TEST SECTOR 02 ---
        engine.exploreObject("obj_tape"); // Laser pointer
        engine.exploreObject("obj_pedestal"); // Cipher disc
        engine.exploreObject("obj_safe"); // Data slate
        assertTrue(engine.getInventory().hasItem("laser_pointer"), "Laser pointer collected");
        assertTrue(engine.getInventory().hasItem("cipher_disc"), "Cipher disc collected");
        assertTrue(engine.getInventory().hasItem("dataslate"), "Data slate collected");
        assertEq(engine.getInventory().size(), 6, "Inventory holds 6 items in Sector 02");

        assertTrue(engine.solvePuzzle("p2_1", "ESCAPE"), "Room 2 Caesar cipher solved");
        assertTrue(engine.solvePuzzle("p2_2", "CHNYEEPRT"), "Room 2 Matrix transposition solved");
        assertTrue(engine.solvePuzzle("p2_3", "MIRROR-45"), "Room 2 Laser code solved");
        assertTrue(engine.getCurrentRoom().isDoorUnlocked(), "Room 2 Door unlocked");

        // Advance to Sector 03
        try {
            engine.moveToNextRoom();
            assertEq(engine.getCurrentRoom().getRoomNumber(), 3, "Moved to Sector 03");
        } catch (EscapeXException e) {
            fail("Could not advance to Room 3: " + e.getMessage());
        }

        // --- TEST SECTOR 03 (Neural Core & Convergence) ---
        // Test exploration order: explore other fixtures first, reaching 8 items
        engine.exploreObject("obj_monitor"); // Activation chart (7th item)
        engine.exploreObject("obj_tensor_rack"); // Logic probe (8th item - page 1 full)
        assertEq(engine.getInventory().size(), 8, "Inventory reached 8 items (page 1 full)");

        // Now explore the Synaptic Column to retrieve the 9th item (Synaptic Weight USB)
        engine.exploreObject("obj_synapse"); // Weight USB (9th item - page 2)
        assertTrue(engine.getInventory().hasItem("weight_usb"), "Synaptic Weight USB collected successfully past 8-item capacity");
        assertEq(engine.getInventory().size(), 9, "Inventory now holds 9 items (page 2 populated)");

        assertTrue(engine.solvePuzzle("p3_1", "3"), "Room 3 Perceptron output solved");
        assertTrue(engine.solvePuzzle("p3_2", "B"), "Room 3 ReLU choice solved");

        // Neural Convergence puzzle: requires weight_usb hardware + threshold 0.001
        assertTrue(engine.solvePuzzle("p3_3", "0.001"), "Room 3 Weight convergence solved with 0.001");
        assertTrue(engine.getCurrentRoom().isDoorUnlocked(), "Room 3 Door unlocked");

        // Advance to Sector 04
        try {
            engine.moveToNextRoom();
            assertEq(engine.getCurrentRoom().getRoomNumber(), 4, "Moved to Sector 04");
        } catch (EscapeXException e) {
            fail("Could not advance to Room 4: " + e.getMessage());
        }

        // --- TEST SECTOR 04 ---
        engine.exploreObject("obj_cryo"); // Polarizer
        engine.exploreObject("obj_console4"); // Entanglement log
        engine.exploreObject("obj_coil_box"); // Superconducting solenoid
        assertTrue(engine.getInventory().hasItem("polarizer"), "Polarizer collected");
        assertTrue(engine.getInventory().hasItem("entangle_log"), "Entanglement log collected");
        assertTrue(engine.getInventory().hasItem("supercoil"), "Superconducting solenoid collected");
        assertEq(engine.getInventory().size(), 12, "Inventory holds 12 items in Sector 04");

        assertTrue(engine.solvePuzzle("p4_1", "B"), "Room 4 Pauli-X gate solved");
        assertTrue(engine.solvePuzzle("p4_2", "-1"), "Room 4 Entanglement spin solved");
        assertTrue(engine.solvePuzzle("p4_3", "45"), "Room 4 Superposition angle solved");
        assertTrue(engine.getCurrentRoom().isDoorUnlocked(), "Room 4 Door unlocked");

        // Advance to Sector 05
        try {
            engine.moveToNextRoom();
            assertEq(engine.getCurrentRoom().getRoomNumber(), 5, "Moved to Sector 05");
        } catch (EscapeXException e) {
            fail("Could not advance to Room 5: " + e.getMessage());
        }

        // --- TEST SECTOR 05 ---
        engine.exploreObject("obj_monolith"); // Asimov Directive
        engine.exploreObject("obj_pedestal5"); // Master token
        engine.exploreObject("obj_archive"); // Quarantine incident report
        assertTrue(engine.getInventory().hasItem("asimov_key"), "Asimov key collected");
        assertTrue(engine.getInventory().hasItem("master_token"), "Master token collected");
        assertTrue(engine.getInventory().hasItem("quarantine_doc"), "Quarantine doc collected");
        assertEq(engine.getInventory().size(), 15, "Inventory holds all 15 items in Sector 05");

        // Test saving and loading with full 15 items
        try {
            File testSave15 = new File("test_save_full_inv.dat");
            engine.saveGame(testSave15);
            engine.loadGame(testSave15);
            assertEq(engine.getInventory().size(), 15, "Loaded inventory contains all 15 items");
            assertTrue(engine.getInventory().hasItem("master_token"), "Loaded inventory has master token");
            testSave15.delete();
        } catch (EscapeXException e) {
            fail("Full inventory save/load error: " + e.getMessage());
        }

        assertTrue(engine.solvePuzzle("p5_1", "B"), "Room 5 Alignment choice solved");
        assertTrue(engine.solvePuzzle("p5_2", "Prompt Injection"), "Room 5 Prompt injection solved");
        assertTrue(engine.solvePuzzle("p5_3", "ESCAPE-X"), "Room 5 Final handshake solved");
        assertTrue(engine.getCurrentRoom().isDoorUnlocked(), "Room 5 Airlock unlocked");

        // Escape final room -> Victory!
        try {
            engine.moveToNextRoom();
            assertTrue(engine.isGameWon(), "Game marked as WON after Sector 05 escape");
            assertTrue(engine.getPlayer().getScore() > 1000, "High score awarded: " + engine.getPlayer().getScore());
        } catch (EscapeXException e) {
            fail("Could not escape Room 5: " + e.getMessage());
        }

        // Verify High Score record
        var scores = engine.getHighScoreService().loadScores();
        assertTrue(!scores.isEmpty(), "Leaderboard has recorded score");

        System.out.println("=== ALL SMOKE TESTS PASSED PERFECTLY! ===");
        System.exit(0);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            System.err.println("ASSERTION FAILED: " + message);
            System.exit(1);
        } else {
            System.out.println("  [PASS] " + message);
        }
    }

    private static void assertEq(Object actual, Object expected, String message) {
        if (!actual.equals(expected)) {
            System.err.println("ASSERTION FAILED: " + message + " (Expected " + expected + ", Got " + actual + ")");
            System.exit(1);
        } else {
            System.out.println("  [PASS] " + message);
        }
    }

    private static void fail(String message) {
        System.err.println("TEST ERROR: " + message);
        System.exit(1);
    }
}
