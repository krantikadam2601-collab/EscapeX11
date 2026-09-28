package escapex.ui;

import escapex.model.Item;
import escapex.ui.components.GlowPanel;
import escapex.ui.components.SciFiButton;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: MODAL DIALOGS & ENCAPSULATED VIEW HIERARCHY
 * ============================================================================
 * Why this matters in OOP:
 * `ClueInspectionDialog` extends {@link JDialog}.
 *
 * It demonstrates:
 * 1. Modality: By setting `setModal(true)`, it halts interaction with parent windows
 *    until the user finishes examining the item.
 * 2. Separation of Concerns: It only knows how to display an `Item` object's
 *    clue and lore without knowing about the game engine or timers.
 * ============================================================================
 */
public class ClueInspectionDialog extends JDialog {

    public ClueInspectionDialog(Window owner, Item item) {
        super(owner, "Hardware Diagnostic Analyzer - Inspecting Item", ModalityType.APPLICATION_MODAL);
        setSize(460, 320);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_DARK);

        JPanel mainPanel = new GlowPanel(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 12);
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header with Item Symbol & Name
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JLabel nameLabel = new JLabel(item.getIconSymbol() + "  " + item.getName());
        nameLabel.setFont(Theme.FONT_TITLE);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        topHeader.add(nameLabel, BorderLayout.WEST);

        JLabel idLabel = new JLabel("ITEM-ID: " + item.getId());
        idLabel.setFont(Theme.FONT_TERMINAL);
        idLabel.setForeground(Theme.TEXT_DIM);
        topHeader.add(idLabel, BorderLayout.EAST);

        mainPanel.add(topHeader, BorderLayout.NORTH);

        // Center Content Body
        JPanel centerBody = new JPanel();
        centerBody.setLayout(new BoxLayout(centerBody, BoxLayout.Y_AXIS));
        centerBody.setOpaque(false);

        // Description
        JLabel descHeader = new JLabel("PHYSICAL EXAMINATION:");
        descHeader.setFont(Theme.FONT_TERMINAL_BOLD);
        descHeader.setForeground(Theme.TEXT_MUTED);
        centerBody.add(descHeader);
        centerBody.add(Box.createVerticalStrut(4));

        JTextArea descArea = new JTextArea(item.getDescription());
        descArea.setFont(Theme.FONT_BODY);
        descArea.setForeground(Theme.TEXT_PRIMARY);
        descArea.setBackground(Theme.BG_PANEL_ALT);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        centerBody.add(descArea);

        centerBody.add(Box.createVerticalStrut(12));

        // Clue Box
        JLabel clueHeader = new JLabel("INSCRIBED CLUE / CIPHER DATA:");
        clueHeader.setFont(Theme.FONT_TERMINAL_BOLD);
        clueHeader.setForeground(Theme.EMERALD);
        centerBody.add(clueHeader);
        centerBody.add(Box.createVerticalStrut(4));

        JTextArea clueArea = new JTextArea(item.hasClue() ? item.getClueText() : "No diagnostic markings detected.");
        clueArea.setFont(Theme.FONT_TERMINAL_BOLD);
        clueArea.setForeground(Theme.TEXT_PRIMARY);
        clueArea.setBackground(Theme.BG_INPUT);
        clueArea.setEditable(false);
        clueArea.setLineWrap(true);
        clueArea.setWrapStyleWord(true);
        clueArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        centerBody.add(clueArea);

        mainPanel.add(centerBody, BorderLayout.CENTER);

        // Footer with Close Button
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        SciFiButton closeBtn = new SciFiButton("Close Inspection", SciFiButton.ButtonStyle.PRIMARY);
        closeBtn.setPreferredSize(new Dimension(140, 32));
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn, BorderLayout.EAST);

        mainPanel.add(footer, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }
}
