package escapex.ui;

import escapex.model.Item;
import escapex.ui.components.GlowPanel;
import escapex.ui.components.SciFiButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: DOMAIN MODAL INVESTIGATION DIALOG
 * ============================================================================
 * Why this matters in OOP:
 * `FixtureDiscoveryDialog` encapsulates the lore and discovery presentation
 * when an operative investigates a chamber fixture.
 *
 * It replaces standard raw JOptionPane popups with a polished, cyberpunk-themed
 * inspection dialog displaying:
 *   - Fixture Name & Icon
 *   - Environmental Lore
 *   - Harvested Item Badge & Discovery Score Bonus
 * ============================================================================
 */
public class FixtureDiscoveryDialog extends JDialog {

    public FixtureDiscoveryDialog(Frame owner, String fixtureName, String discoveryText, Item discoveredItem) {
        super(owner, "EscapeX - Chamber Investigation", true);
        setSize(480, 360);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_DARK);

        initUI(fixtureName, discoveryText, discoveredItem);
    }

    private void initUI(String fixtureName, String discoveryText, Item discoveredItem) {
        JPanel mainCard = new GlowPanel(Theme.CYAN_NEON, Theme.BG_PANEL, 12);
        mainCard.setLayout(new BorderLayout(10, 10));
        mainCard.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // 1. HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titleLabel = new JLabel("🔍  " + fixtureName.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        header.add(titleLabel, BorderLayout.WEST);

        JLabel tagLabel = new JLabel("FIXTURE LOGGED");
        tagLabel.setFont(Theme.FONT_TERMINAL);
        tagLabel.setForeground(Theme.TEXT_DIM);
        header.add(tagLabel, BorderLayout.EAST);

        mainCard.add(header, BorderLayout.NORTH);

        // 2. DISCOVERY TEXT BODY
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        JTextArea descArea = new JTextArea(discoveryText);
        descArea.setFont(Theme.FONT_BODY);
        descArea.setForeground(Theme.TEXT_PRIMARY);
        descArea.setBackground(Theme.BG_INPUT);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        body.add(descArea);

        // Discovered Item Banner
        if (discoveredItem != null) {
            body.add(Box.createVerticalStrut(10));

            JPanel itemCard = new GlowPanel(Theme.EMERALD, Theme.BG_PANEL_ALT, 8);
            itemCard.setLayout(new BorderLayout(8, 0));
            itemCard.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            JLabel itemIcon = new JLabel(discoveredItem.getIconSymbol());
            itemIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
            itemIcon.setForeground(Theme.EMERALD);
            itemCard.add(itemIcon, BorderLayout.WEST);

            JPanel itemInfo = new JPanel(new GridLayout(2, 1, 0, 2));
            itemInfo.setOpaque(false);

            JLabel itemName = new JLabel("ITEM ACQUIRED: " + discoveredItem.getName());
            itemName.setFont(Theme.FONT_BODY_BOLD);
            itemName.setForeground(Theme.EMERALD);
            itemInfo.add(itemName);

            JLabel itemBonus = new JLabel("+50 PTS Added to Inventory Rack (Page " + (discoveredItem.getName().length() > 0 ? "Active" : "") + ")");
            itemBonus.setFont(Theme.FONT_TERMINAL);
            itemBonus.setForeground(Theme.TEXT_MUTED);
            itemInfo.add(itemBonus);

            itemCard.add(itemInfo, BorderLayout.CENTER);
            body.add(itemCard);
        }

        mainCard.add(body, BorderLayout.CENTER);

        // 3. CLOSE ACTION
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomBar.setOpaque(false);

        SciFiButton okBtn = new SciFiButton("Continue Investigation", SciFiButton.ButtonStyle.PRIMARY);
        okBtn.setPreferredSize(new Dimension(180, 32));
        okBtn.addActionListener(e -> dispose());
        bottomBar.add(okBtn);

        mainCard.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(mainCard);
    }
}
