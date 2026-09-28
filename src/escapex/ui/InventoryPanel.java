package escapex.ui;

import escapex.model.Inventory;
import escapex.model.Item;
import escapex.observer.GameEvent;
import escapex.observer.GameEventListener;
import escapex.service.GameEngine;
import escapex.ui.components.GlowPanel;
import escapex.ui.components.SciFiButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: OBSERVER PATTERN, COMPOSITION & UI RE-RENDERING
 * ============================================================================
 * Why this matters in OOP:
 * `InventoryPanel` observes game events and updates the visual inventory rack.
 *
 * 1. Event Synchronization:
 *    When `ITEM_COLLECTED` or `GAME_LOADED` events fire, the inventory UI automatically
 *    rebuilds its item slots to reflect the new state of `player.getInventory()`.
 *
 * 2. Encapsulated Item Inspection:
 *    Clicking on an inventory slot creates a `ClueInspectionDialog` displaying
 *    the item's hidden clues, demonstrating how UI objects collaborate seamlessly.
 *
 * 3. Paginated Display:
 *    Maintains the clean 2x4 (8-slot) visual rack while supporting multi-page
 *    inventory capacity so players can view and inspect all facility items.
 * ============================================================================
 */
public class InventoryPanel extends GlowPanel implements GameEventListener {

    private static final int SLOTS_PER_PAGE = 8;

    private final GameEngine gameEngine;
    private final JPanel slotsContainer;
    private final JLabel headerLabel;
    private final JLabel pageLabel;
    private final JButton prevBtn;
    private final JButton nextBtn;
    private int currentPage = 0;

    public InventoryPanel(GameEngine gameEngine) {
        super(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 8);
        this.gameEngine = gameEngine;
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(320, 180));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 4, 10));

        headerLabel = new JLabel("Inventory (0/16)");
        headerLabel.setFont(Theme.FONT_HEADER);
        headerLabel.setForeground(Theme.TEXT_PRIMARY);
        headerPanel.add(headerLabel, BorderLayout.WEST);

        // Pagination controls
        prevBtn = new JButton("◄");
        prevBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        prevBtn.setForeground(Theme.TEXT_PRIMARY);
        prevBtn.setBackground(Theme.BG_INPUT);
        prevBtn.setFocusPainted(false);
        prevBtn.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE));
        prevBtn.setPreferredSize(new Dimension(24, 20));
        prevBtn.setToolTipText("Previous inventory page (Slots 1-8)");
        prevBtn.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                refreshInventoryView();
            }
        });

        nextBtn = new JButton("►");
        nextBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        nextBtn.setForeground(Theme.TEXT_PRIMARY);
        nextBtn.setBackground(Theme.BG_INPUT);
        nextBtn.setFocusPainted(false);
        nextBtn.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE));
        nextBtn.setPreferredSize(new Dimension(24, 20));
        nextBtn.setToolTipText("Next inventory page (Slots 9-16)");
        nextBtn.addActionListener(e -> {
            if (currentPage < getTotalPages() - 1) {
                currentPage++;
                refreshInventoryView();
            }
        });

        pageLabel = new JLabel("Page 1/2");
        pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pageLabel.setForeground(Theme.TEXT_MUTED);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        navPanel.setOpaque(false);
        navPanel.add(prevBtn);
        navPanel.add(pageLabel);
        navPanel.add(nextBtn);

        headerPanel.add(navPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Slots grid container (2 rows x 4 columns = 8 visual slots)
        slotsContainer = new JPanel(new GridLayout(2, 4, 6, 6));
        slotsContainer.setOpaque(false);
        slotsContainer.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));

        add(slotsContainer, BorderLayout.CENTER);

        refreshInventoryView();
    }

    /**
     * Rebuilds the visual inventory grid from the player's active inventory page.
     */
    public void refreshInventoryView() {
        slotsContainer.removeAll();
        Inventory inv = gameEngine.getInventory();
        List<Item> items = inv.getItems();

        int totalPages = getTotalPages();
        if (currentPage >= totalPages) currentPage = totalPages - 1;
        if (currentPage < 0) currentPage = 0;

        headerLabel.setText("Inventory (" + items.size() + "/" + inv.getMaxCapacity() + ")");
        pageLabel.setText("Page " + (currentPage + 1) + "/" + totalPages);
        prevBtn.setEnabled(currentPage > 0);
        nextBtn.setEnabled(currentPage < totalPages - 1);

        int startIndex = currentPage * SLOTS_PER_PAGE;

        // Render filled and empty slots for current page
        for (int i = startIndex; i < startIndex + SLOTS_PER_PAGE; i++) {
            if (i < items.size()) {
                Item item = items.get(i);
                SciFiButton itemBtn = new SciFiButton(
                        item.getName(),
                        SciFiButton.ButtonStyle.SECONDARY
                );
                itemBtn.setFont(Theme.FONT_BODY_BOLD);
                itemBtn.setToolTipText("Click to inspect: " + item.getName());
                itemBtn.addActionListener(e -> showItemInspection(item));
                slotsContainer.add(itemBtn);
            } else if (i < inv.getMaxCapacity()) {
                JPanel emptyPanel = new JPanel();
                emptyPanel.setBackground(Theme.BG_INPUT);
                emptyPanel.setBorder(BorderFactory.createDashedBorder(Theme.BORDER_SUBTLE, 2, 2));
                JLabel emptyLabel = new JLabel("—");
                emptyLabel.setFont(Theme.FONT_TERMINAL);
                emptyLabel.setForeground(Theme.TEXT_DIM);
                emptyPanel.add(emptyLabel);
                slotsContainer.add(emptyPanel);
            }
        }

        slotsContainer.revalidate();
        slotsContainer.repaint();
    }

    private int getTotalPages() {
        Inventory inv = gameEngine.getInventory();
        return Math.max(1, (inv.getMaxCapacity() + SLOTS_PER_PAGE - 1) / SLOTS_PER_PAGE);
    }

    private void showItemInspection(Item item) {
        new ClueInspectionDialog(SwingUtilities.getWindowAncestor(this), item).setVisible(true);
    }

    @Override
    public void onGameEvent(GameEvent event) {
        switch (event.getType()) {
            case ITEM_COLLECTED -> {
                // Auto-advance to the page containing the newly collected item
                Inventory inv = gameEngine.getInventory();
                int targetPage = Math.max(0, (inv.size() - 1) / SLOTS_PER_PAGE);
                currentPage = targetPage;
                SwingUtilities.invokeLater(this::refreshInventoryView);
            }
            case ROOM_CHANGED, GAME_LOADED -> SwingUtilities.invokeLater(this::refreshInventoryView);
            default -> {}
        }
    }
}
