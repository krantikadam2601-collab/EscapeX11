package escapex.model.puzzle;

import escapex.model.Inventory;
import java.util.List;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: INHERITANCE & CROSS-OBJECT COLLABORATION
 * ============================================================================
 * Why this matters in OOP:
 * `ItemCombinationPuzzle` is a sophisticated puzzle subclass that requires
 * collaboration between two distinct domain objects:
 *   1. The `Inventory` (which holds collected `Item` objects).
 *   2. The `Puzzle` solution input.
 *
 * In real escape rooms, you often can't simply guess a code; you must first find
 * a specific physical tool (like a UV light, keycard, or optical magnifier) to
 * reveal or unlock the puzzle mechanism.
 *
 * This demonstrates how objects in OOP interact via well-defined interfaces
 * without exposing their private internal representations.
 * ============================================================================
 */
public class ItemCombinationPuzzle extends Puzzle {

    private static final long serialVersionUID = 1L;

    private final String requiredItemId;
    private final String requiredItemName;
    private final String solutionCode;
    private final List<String> acceptableSolutions;

    public ItemCombinationPuzzle(String id, String title, String description, String prompt,
                                 PuzzleDifficulty difficulty, List<String> hints,
                                 String requiredItemId, String requiredItemName,
                                 String solutionCode) {
        this(id, title, description, prompt, difficulty, hints, requiredItemId, requiredItemName, List.of(solutionCode));
    }

    public ItemCombinationPuzzle(String id, String title, String description, String prompt,
                                 PuzzleDifficulty difficulty, List<String> hints,
                                 String requiredItemId, String requiredItemName,
                                 List<String> acceptableSolutions) {
        super(id, title, description, prompt, difficulty, hints);
        this.requiredItemId = requiredItemId;
        this.requiredItemName = requiredItemName;
        this.acceptableSolutions = acceptableSolutions != null && !acceptableSolutions.isEmpty()
                ? acceptableSolutions : List.of("");
        this.solutionCode = this.acceptableSolutions.get(0);
    }

    /**
     * Checks if the player's inventory currently contains the mandatory tool/item.
     */
    public boolean hasRequiredItem(Inventory inventory) {
        if (inventory == null) return false;
        return inventory.hasItem(requiredItemId);
    }

    /**
     * Specialized validation that takes both the player's inventory and code input.
     *
     * @param inventory The player's active inventory.
     * @param input The passcode entered.
     * @return true if player possesses the item AND entered the correct code.
     */
    public boolean attemptWithInventory(Inventory inventory, String input) {
        if (!hasRequiredItem(inventory)) {
            return false;
        }
        return attemptSolution(input);
    }

    @Override
    protected boolean checkAnswer(String input) {
        if (input == null) return false;
        String trimmed = input.trim();
        for (String sol : acceptableSolutions) {
            if (trimmed.equalsIgnoreCase(sol.trim())) return true;
        }

        // Try numeric comparison if numeric (handles 0.001 vs .001 vs 1e-3, etc.)
        try {
            double inputVal = Double.parseDouble(trimmed);
            for (String sol : acceptableSolutions) {
                try {
                    double solVal = Double.parseDouble(sol.trim());
                    if (Math.abs(inputVal - solVal) < 1e-6) {
                        return true;
                    }
                } catch (NumberFormatException ignored) {}
            }
        } catch (NumberFormatException ignored) {}

        // Normalize alphanumeric characters (ignoring hyphens, spaces, degree symbols)
        String normInput = trimmed.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        for (String sol : acceptableSolutions) {
            String normSol = sol.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            if (!normSol.isEmpty() && normInput.equals(normSol)) return true;
        }
        return false;
    }

    public String getRequiredItemId() {
        return requiredItemId;
    }

    public String getRequiredItemName() {
        return requiredItemName;
    }

    public String getSolutionCode() {
        return solutionCode;
    }

    @Override
    public String getPuzzleTypeDescription() {
        return "Item Synergy (Requires " + requiredItemName + " + Code)";
    }
}
