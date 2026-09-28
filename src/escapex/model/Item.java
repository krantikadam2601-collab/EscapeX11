package escapex.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: ENCAPSULATION & SERIALIZATION (Value Object)
 * ============================================================================
 * Why this matters in OOP:
 * 1. Encapsulation: All state fields (`id`, `name`, `description`, `clueText`, `iconSymbol`)
 *    are `private`. Access is granted only via public getters.
 * 2. Immutability: Notice there are NO setters! Once an Item is created,
 *    its properties cannot be accidentally corrupted by other parts of the code.
 * 3. Serializable Interface: Implementing `java.io.Serializable` allows the Java runtime
 *    to convert an Item into a byte stream for saving game progress to disk.
 * 4. Value Equality: Overriding `equals()` and `hashCode()` ensures two Item objects
 *    representing the same item can be reliably compared in collections (e.g., ArrayList, HashSet).
 * ============================================================================
 */
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String description;
    private final String clueText;
    private final String iconSymbol; // Text or unicode icon representation (e.g. "[KEY]", "[CHIP]")

    /**
     * Constructs a new immutable Item.
     *
     * @param id Unique identifier used internally by puzzles and game logic.
     * @param name Display name shown in UI inventory slots.
     * @param description Narrative description when the player inspects the item.
     * @param clueText Specific hint or code inscribed on the item.
     * @param iconSymbol A short visual tag or emoji for the UI.
     */
    public Item(String id, String name, String description, String clueText, String iconSymbol) {
        this.id = Objects.requireNonNull(id, "Item ID cannot be null");
        this.name = Objects.requireNonNull(name, "Item name cannot be null");
        this.description = description != null ? description : "";
        this.clueText = clueText != null ? clueText : "";
        this.iconSymbol = iconSymbol != null ? iconSymbol : "[ITEM]";
    }

    // --- GETTERS (No Setters = Immutability!) ---

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getClueText() {
        return clueText;
    }

    public String getIconSymbol() {
        return iconSymbol;
    }

    /**
     * Checks if this item has an inspectable clue attached.
     */
    public boolean hasClue() {
        return !clueText.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return id.equalsIgnoreCase(item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.toLowerCase());
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
