package escapex.ui;

import escapex.model.Inventory;
import escapex.model.Room;
import escapex.model.puzzle.ChoicePuzzle;
import escapex.model.puzzle.ItemCombinationPuzzle;
import escapex.model.puzzle.PatternPuzzle;
import escapex.model.puzzle.Puzzle;
import escapex.model.puzzle.PuzzleDifficulty;
import escapex.service.GameEngine;
import escapex.ui.components.GlowPanel;
import escapex.ui.components.SciFiButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: POLYMORPHISM IN UI RENDERING & EVENT DISPATCHING
 * ============================================================================
 * Why this matters in OOP:
 * `PuzzleDialog` dynamically inspects the active `Puzzle` and alters its UI
 * representation based on the concrete puzzle subclass (Polymorphic rendering!):
 *
 * 1. If `ChoicePuzzle`: It dynamically generates radio buttons for options A, B, C, D.
 * 2. If `ItemCombinationPuzzle`: It checks the player's `Inventory` for the required
 *    tool and renders an inventory requirement badge.
 * 3. If `CodeInputPuzzle` or `PatternPuzzle`: It presents a normalized input terminal.
 *
 * This demonstrates how higher-level user interfaces use polymorphism (`instanceof`
 * pattern matching and method delegation) to adapt to varied domain models.
 * ============================================================================
 */
public class PuzzleDialog extends JDialog {

    private final GameEngine gameEngine;
    private final Window parentWindow;
    private Puzzle currentPuzzle;

    // UI Controls
    private final JPanel puzzleSelectorBar;
    private final JLabel titleLabel;
    private final JLabel typeLabel;
    private final JLabel difficultyLabel;
    private final JTextArea descArea;
    private final JTextArea promptArea;
    private final JPanel dynamicInputContainer;
    private final JLabel statusFeedbackLabel;
    private final JTextArea hintDisplayArea;
    private final SciFiButton hintButton;
    private final SciFiButton submitButton;

    // Dynamic inputs
    private JTextField textInputField;
    private ButtonGroup choiceButtonGroup;

    public PuzzleDialog(Window owner, GameEngine gameEngine, PuzzleDifficulty defaultDifficulty) {
        super(owner, "EscapeX Security Bypass Terminal - Sector Diagnostic Puzzles", ModalityType.APPLICATION_MODAL);
        this.ownerWindow = owner;
        this.gameEngine = gameEngine;
        this.parentWindow = owner;

        setSize(680, 580);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_DARK);

        JPanel mainCard = new GlowPanel(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 12);
        mainCard.setLayout(new BorderLayout(8, 8));
        mainCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // 1. TOP SELECTOR BAR: Easy, Medium, Hard Tabs
        puzzleSelectorBar = new JPanel(new GridLayout(1, 3, 10, 0));
        puzzleSelectorBar.setOpaque(false);
        mainCard.add(puzzleSelectorBar, BorderLayout.NORTH);

        // 2. CENTER CONTENT: Puzzle Details & Dynamic Inputs
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Header info
        JPanel metaRow = new JPanel(new BorderLayout());
        metaRow.setOpaque(false);

        titleLabel = new JLabel("PUZZLE TITLE");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        metaRow.add(titleLabel, BorderLayout.WEST);

        difficultyLabel = new JLabel("DIFFICULTY");
        difficultyLabel.setFont(Theme.FONT_TERMINAL_BOLD);
        difficultyLabel.setForeground(Theme.AMBER);
        metaRow.add(difficultyLabel, BorderLayout.EAST);
        centerPanel.add(metaRow);

        typeLabel = new JLabel("Mechanism: ...");
        typeLabel.setFont(Theme.FONT_TERMINAL);
        typeLabel.setForeground(Theme.TEXT_DIM);
        centerPanel.add(typeLabel);

        centerPanel.add(Box.createVerticalStrut(8));

        // Narrative Description
        descArea = new JTextArea(3, 40);
        descArea.setFont(Theme.FONT_BODY);
        descArea.setForeground(Theme.TEXT_PRIMARY);
        descArea.setBackground(Theme.BG_PANEL_ALT);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        centerPanel.add(descArea);

        centerPanel.add(Box.createVerticalStrut(8));

        // Question Prompt Box
        promptArea = new JTextArea(3, 40);
        promptArea.setFont(Theme.FONT_TERMINAL_BOLD);
        promptArea.setForeground(Theme.EMERALD);
        promptArea.setBackground(Theme.BG_INPUT);
        promptArea.setEditable(false);
        promptArea.setLineWrap(true);
        promptArea.setWrapStyleWord(true);
        promptArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        centerPanel.add(promptArea);

        centerPanel.add(Box.createVerticalStrut(10));

        // Dynamic Interactive Input Area (Changes based on Puzzle type!)
        dynamicInputContainer = new JPanel(new BorderLayout());
        dynamicInputContainer.setOpaque(false);
        centerPanel.add(dynamicInputContainer);

        centerPanel.add(Box.createVerticalStrut(10));

        // Status Feedback line
        statusFeedbackLabel = new JLabel(" ");
        statusFeedbackLabel.setFont(Theme.FONT_TERMINAL_BOLD);
        centerPanel.add(statusFeedbackLabel);

        centerPanel.add(Box.createVerticalStrut(6));

        // Hints Area
        hintDisplayArea = new JTextArea(2, 40);
        hintDisplayArea.setFont(Theme.FONT_TERMINAL);
        hintDisplayArea.setForeground(Theme.AMBER);
        hintDisplayArea.setBackground(Theme.BG_INPUT);
        hintDisplayArea.setEditable(false);
        hintDisplayArea.setLineWrap(true);
        hintDisplayArea.setWrapStyleWord(true);
        hintDisplayArea.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        centerPanel.add(hintDisplayArea);

        mainCard.add(centerPanel, BorderLayout.CENTER);

        // 3. BOTTOM ACTION BAR: Submit, Hint, Close
        JPanel bottomBar = new JPanel(new BorderLayout(10, 0));
        bottomBar.setOpaque(false);

        hintButton = new SciFiButton("Request Hint (-15 pts)", SciFiButton.ButtonStyle.WARNING);
        hintButton.setPreferredSize(new Dimension(180, 34));
        hintButton.addActionListener(e -> requestHint());
        bottomBar.add(hintButton, BorderLayout.WEST);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);

        submitButton = new SciFiButton("Submit Answer", SciFiButton.ButtonStyle.PRIMARY);
        submitButton.setPreferredSize(new Dimension(140, 34));
        submitButton.addActionListener(e -> submitCurrentAnswer());
        rightActions.add(submitButton);

        SciFiButton closeBtn = new SciFiButton("Close", SciFiButton.ButtonStyle.SECONDARY);
        closeBtn.setPreferredSize(new Dimension(100, 34));
        closeBtn.addActionListener(e -> dispose());
        rightActions.add(closeBtn);

        bottomBar.add(rightActions, BorderLayout.EAST);
        mainCard.add(bottomBar, BorderLayout.SOUTH);

        getContentPane().add(mainCard);

        // Select initial puzzle
        Room currentRoom = gameEngine.getCurrentRoom();
        Puzzle initial = currentRoom.getPuzzleByDifficulty(defaultDifficulty)
                .orElse(currentRoom.getPuzzles().get(0));
        selectPuzzle(initial);
    }

    private final Window ownerWindow;

    private void rebuildSelectorBar() {
        puzzleSelectorBar.removeAll();
        Room room = gameEngine.getCurrentRoom();

        for (Puzzle p : room.getPuzzles()) {
            String diff = p.getDifficulty().name().charAt(0) + p.getDifficulty().name().substring(1).toLowerCase();
            String status = p.isSolved() ? " ✓" : "";
            SciFiButton tabBtn = new SciFiButton(
                    diff + status,
                    p.isSolved() ? SciFiButton.ButtonStyle.SUCCESS :
                            (p == currentPuzzle ? SciFiButton.ButtonStyle.PRIMARY : SciFiButton.ButtonStyle.SECONDARY)
            );
            tabBtn.setFont(Theme.FONT_BODY_BOLD);
            tabBtn.addActionListener(e -> selectPuzzle(p));
            puzzleSelectorBar.add(tabBtn);
        }
        puzzleSelectorBar.revalidate();
        puzzleSelectorBar.repaint();
    }

    private void selectPuzzle(Puzzle puzzle) {
        this.currentPuzzle = puzzle;
        rebuildSelectorBar();

        titleLabel.setText(puzzle.getTitle());
        typeLabel.setText("Mechanism: " + puzzle.getPuzzleTypeDescription());
        difficultyLabel.setText(puzzle.getDifficulty().getLabel() + " (+" + puzzle.calculateScore() + " pts)");

        descArea.setText(puzzle.getDescription());
        promptArea.setText(puzzle.getPrompt());

        updateHintDisplay();

        // Build dynamic input controls based on Puzzle subtype (Polymorphism!)
        buildInputUI(puzzle);

        if (puzzle.isSolved()) {
            statusFeedbackLabel.setText("STATUS: SOLVED & CLEARED (+Points Awarded)");
            statusFeedbackLabel.setForeground(Theme.EMERALD);
            submitButton.setEnabled(false);
            hintButton.setEnabled(false);
        } else {
            statusFeedbackLabel.setText("STATUS: PENDING VERIFICATION (" + puzzle.getAttemptsCount() + " attempts)");
            statusFeedbackLabel.setForeground(Theme.TEXT_MUTED);
            submitButton.setEnabled(true);
            hintButton.setEnabled(puzzle.hasMoreHints());
        }
    }

    private void buildInputUI(Puzzle puzzle) {
        dynamicInputContainer.removeAll();

        if (puzzle instanceof ChoicePuzzle choicePuzzle) {
            // Render Radio Buttons for multiple choices
            JPanel choiceBox = new JPanel();
            choiceBox.setLayout(new BoxLayout(choiceBox, BoxLayout.Y_AXIS));
            choiceBox.setOpaque(false);

            choiceButtonGroup = new ButtonGroup();
            List<String> options = choicePuzzle.getOptions();

            for (int i = 0; i < options.size(); i++) {
                char letter = (char) ('A' + i);
                String optionText = letter + ")  " + options.get(i);
                JRadioButton radio = new JRadioButton(optionText);
                radio.setFont(Theme.FONT_BODY_BOLD);
                radio.setForeground(Theme.TEXT_PRIMARY);
                radio.setOpaque(false);
                radio.setActionCommand(String.valueOf(letter));
                radio.setEnabled(!puzzle.isSolved());

                choiceButtonGroup.add(radio);
                choiceBox.add(radio);
                choiceBox.add(Box.createVerticalStrut(4));
            }
            dynamicInputContainer.add(choiceBox, BorderLayout.CENTER);

        } else if (puzzle instanceof ItemCombinationPuzzle itemPuzzle) {
            // Render Required Item status + Code Input
            JPanel itemContainer = new JPanel(new BorderLayout(5, 5));
            itemContainer.setOpaque(false);

            Inventory inv = gameEngine.getInventory();
            boolean hasItem = itemPuzzle.hasRequiredItem(inv);

            JLabel reqBadge = new JLabel(
                    hasItem ? "[TOOL READY] In Inventory: [" + itemPuzzle.getRequiredItemName() + "] (Inspect item to view code)"
                            : "[TOOL MISSING] Required: [" + itemPuzzle.getRequiredItemName() + "] — Search room fixtures first!"
            );
            reqBadge.setFont(Theme.FONT_BODY_BOLD);
            reqBadge.setForeground(hasItem ? Theme.EMERALD : Theme.ROSE);
            itemContainer.add(reqBadge, BorderLayout.NORTH);

            JPanel inputRow = new JPanel(new BorderLayout(8, 0));
            inputRow.setOpaque(false);
            JLabel promptLbl = new JLabel("ENTER CODE:");
            promptLbl.setFont(Theme.FONT_TERMINAL_BOLD);
            promptLbl.setForeground(Theme.TEXT_PRIMARY);
            inputRow.add(promptLbl, BorderLayout.WEST);

            textInputField = new JTextField();
            textInputField.setFont(Theme.FONT_TERMINAL_BOLD);
            textInputField.setBackground(Theme.BG_INPUT);
            textInputField.setForeground(Theme.TEXT_PRIMARY);
            textInputField.setCaretColor(Theme.TEXT_PRIMARY);
            textInputField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(hasItem ? Theme.ACCENT_BLUE : Theme.BORDER_SUBTLE),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            textInputField.setEnabled(hasItem && !puzzle.isSolved());
            textInputField.addActionListener(e -> submitCurrentAnswer());
            inputRow.add(textInputField, BorderLayout.CENTER);

            itemContainer.add(inputRow, BorderLayout.CENTER);
            dynamicInputContainer.add(itemContainer, BorderLayout.CENTER);

        } else {
            // Standard CodeInputPuzzle or PatternPuzzle
            JPanel inputRow = new JPanel(new BorderLayout(8, 0));
            inputRow.setOpaque(false);

            JLabel promptLbl = new JLabel("ENTER CODE / SEQUENCE:");
            promptLbl.setFont(Theme.FONT_TERMINAL_BOLD);
            promptLbl.setForeground(Theme.TEXT_PRIMARY);
            inputRow.add(promptLbl, BorderLayout.WEST);

            textInputField = new JTextField();
            textInputField.setFont(Theme.FONT_TERMINAL_BOLD);
            textInputField.setBackground(Theme.BG_INPUT);
            textInputField.setForeground(Theme.TEXT_PRIMARY);
            textInputField.setCaretColor(Theme.TEXT_PRIMARY);
            textInputField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER_SUBTLE),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            textInputField.setEnabled(!puzzle.isSolved());
            textInputField.addActionListener(e -> submitCurrentAnswer());
            inputRow.add(textInputField, BorderLayout.CENTER);

            dynamicInputContainer.add(inputRow, BorderLayout.CENTER);
        }

        dynamicInputContainer.revalidate();
        dynamicInputContainer.repaint();
    }

    private void updateHintDisplay() {
        if (currentPuzzle.getHintsRevealedCount() == 0) {
            hintDisplayArea.setText("No hints requested yet. (Hints cost -15 score points)");
        } else {
            StringBuilder sb = new StringBuilder();
            List<String> allHints = currentPuzzle.getAllHintsDefensive();
            for (int i = 0; i < currentPuzzle.getHintsRevealedCount() && i < allHints.size(); i++) {
                sb.append(allHints.get(i)).append("\n");
            }
            hintDisplayArea.setText(sb.toString().trim());
        }
    }

    private void requestHint() {
        if (currentPuzzle == null || !currentPuzzle.hasMoreHints()) {
            return;
        }
        gameEngine.requestHint(currentPuzzle.getId());
        updateHintDisplay();
        hintButton.setEnabled(currentPuzzle.hasMoreHints());
    }

    private void submitCurrentAnswer() {
        if (currentPuzzle == null || currentPuzzle.isSolved()) return;

        String answer = null;
        if (currentPuzzle instanceof ChoicePuzzle) {
            if (choiceButtonGroup != null) {
                for (Enumeration<AbstractButton> buttons = choiceButtonGroup.getElements(); buttons.hasMoreElements();) {
                    AbstractButton button = buttons.nextElement();
                    if (button.isSelected()) {
                        answer = button.getActionCommand();
                        break;
                    }
                }
            }
            if (answer == null) {
                statusFeedbackLabel.setText("Please select one of the options (A, B, C, D)!");
                statusFeedbackLabel.setForeground(Theme.AMBER);
                return;
            }
        } else {
            if (textInputField != null) {
                answer = textInputField.getText();
            }
            if (answer == null || answer.trim().isEmpty()) {
                statusFeedbackLabel.setText("Please enter an answer into the terminal!");
                statusFeedbackLabel.setForeground(Theme.AMBER);
                return;
            }
        }

        boolean success = gameEngine.solvePuzzle(currentPuzzle.getId(), answer);
        if (success) {
            statusFeedbackLabel.setText("CORRECT! Diagnostic verified. Security bypass accepted!");
            statusFeedbackLabel.setForeground(Theme.EMERALD);
            selectPuzzle(currentPuzzle); // Refresh view to show solved state

            // Check if all 3 puzzles are cleared
            Room room = gameEngine.getCurrentRoom();
            if (room.isDoorUnlocked()) {
                JOptionPane.showMessageDialog(
                        this,
                        "ALL 3 SECTOR DIAGNOSTICS RESOLVED!\n\nThe Blast Door is now UNLOCKED.\nClick the Blast Door or 'Advance to Next Sector' to escape!",
                        "Sector Breach Complete",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } else {
            statusFeedbackLabel.setText("DENIED! Verification failed. Re-evaluate telemetry and retry.");
            statusFeedbackLabel.setForeground(Theme.ROSE);
        }
    }
}
