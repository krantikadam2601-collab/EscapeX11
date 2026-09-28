package escapex.model;

import java.io.Serializable;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: ENCAPSULATION & STATE MUTATION
 * ============================================================================
 * Why this matters in OOP:
 * A `RoomObject` represents an interactive fixture or hotspot inside an escape room
 * (for example, a server rack, a wall safe, or a diagnostic bench).
 *
 * It demonstrates:
 * 1. Encapsulation: Coordinates, descriptions, and contained items are protected.
 * 2. State Mutation: When a player investigates the object, its internal state
 *    transitions (`isExplored` becomes true, `hasCollectedItem` becomes true).
 * 3. Aggregation: A `RoomObject` can hold a hidden `Item`, which is transferred
 *    to the player's inventory upon exploration.
 * ============================================================================
 */
public class RoomObject implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String description;
    private final String investigationResult;
    private final double relativeX; // Normalized X coordinate on screen (0.0 to 1.0)
    private final double relativeY; // Normalized Y coordinate on screen (0.0 to 1.0)
    private final String iconType;  // e.g. "TERMINAL", "SAFE", "CONSOLE", "SERVER", "DOOR"

    private Item hiddenItem;
    private boolean explored;
    private boolean itemHarvested;

    /**
     * Constructs a new RoomObject hotspot.
     */
    public RoomObject(String id, String name, String description, String investigationResult,
                      double relativeX, double relativeY, String iconType, Item hiddenItem) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.investigationResult = investigationResult;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.iconType = iconType;
        this.hiddenItem = hiddenItem;
        this.explored = false;
        this.itemHarvested = false;
    }

    public RoomObject(String id, String name, String description, String investigationResult,
                      double relativeX, double relativeY, String iconType) {
        this(id, name, description, investigationResult, relativeX, relativeY, iconType, null);
    }

    // --- State Methods ---

    /**
     * Investigates the object, marking it as explored.
     * @return Exploration description or clue.
     */
    public String investigate() {
        this.explored = true;
        return investigationResult;
    }

    /**
     * Checks if this object contains an item that hasn't been collected yet.
     */
    public boolean hasCollectibleItem() {
        return hiddenItem != null && !itemHarvested;
    }

    /**
     * Harvests the hidden item if present.
     * @return The item found, or null if none remains.
     */
    public Item harvestItem() {
        if (hasCollectibleItem()) {
            this.itemHarvested = true;
            Item found = this.hiddenItem;
            return found;
        }
        return null;
    }

    // --- Getters ---

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getInvestigationResult() {
        return investigationResult;
    }

    public double getRelativeX() {
        return relativeX;
    }

    public double getRelativeY() {
        return relativeY;
    }

    public String getIconType() {
        return iconType;
    }

    public Item getHiddenItem() {
        return hiddenItem;
    }

    public boolean isExplored() {
        return explored;
    }

    public boolean isItemHarvested() {
        return itemHarvested;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    public void setItemHarvested(boolean itemHarvested) {
        this.itemHarvested = itemHarvested;
    }
}
