package escapex.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: AGGREGATION & INFORMATION HIDING (Encapsulation)
 * ============================================================================
 * Why this matters in OOP:
 * The `Inventory` class maintains a collection of `Item` objects.
 *
 * 1. Aggregation (Has-A relationship): An Inventory "has-a" collection of Items.
 *    The Items exist independently, but the Inventory manages access to them.
 *
 * 2. Information Hiding:
 *    The internal `ArrayList<Item>` is private.
 *    External code cannot directly call `items.clear()` or manipulate the list.
 *    Instead, external code must call domain methods like `addItem()` or `hasItem()`.
 *
 * 3. Defensive Copying:
 *    `getItems()` returns an unmodifiable view using `Collections.unmodifiableList()`.
 *    This prevents outside code from tampering with the internal state of the inventory!
 * ============================================================================
 */
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_MAX_CAPACITY = 16;

    private final int maxCapacity;
    private final List<Item> items;

    public Inventory(int maxCapacity) {
        this.maxCapacity = Math.max(1, maxCapacity);
        this.items = new ArrayList<>();
    }

    public Inventory() {
        this(DEFAULT_MAX_CAPACITY);
    }

    /**
     * Attempts to add an item to the inventory.
     * Demonstrates data validation and invariant protection.
     *
     * @param item The item to collect.
     * @return true if added successfully, false if inventory is full or item already held.
     */
    public boolean addItem(Item item) {
        if (item == null) {
            return false;
        }
        if (isFull()) {
            return false;
        }
        if (hasItem(item.getId())) {
            // Already holding this unique item
            return false;
        }
        return items.add(item);
    }

    /**
     * Removes an item by its unique ID.
     */
    public boolean removeItem(String itemId) {
        if (itemId == null) return false;
        return items.removeIf(item -> item.getId().equalsIgnoreCase(itemId));
    }

    /**
     * Checks if the player possesses an item with the given ID.
     */
    public boolean hasItem(String itemId) {
        if (itemId == null) return false;
        return items.stream().anyMatch(item -> item.getId().equalsIgnoreCase(itemId));
    }

    /**
     * Finds and returns an item wrapped in an Optional.
     * Optional prevents NullPointerException bugs.
     */
    public Optional<Item> findItem(String itemId) {
        if (itemId == null) return Optional.empty();
        return items.stream()
                .filter(item -> item.getId().equalsIgnoreCase(itemId))
                .findFirst();
    }

    /**
     * Returns an unmodifiable list of items (Defensive Copying).
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int size() {
        return items.size();
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public boolean isFull() {
        return items.size() >= maxCapacity;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }
}
