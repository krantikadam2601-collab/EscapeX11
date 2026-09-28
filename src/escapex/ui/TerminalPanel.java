package escapex.ui;

import escapex.observer.GameEvent;
import escapex.observer.GameEventListener;
import escapex.ui.components.GlowPanel;
import escapex.ui.components.SciFiButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: OBSERVER PATTERN IMPLEMENTATION & EVENT CONSUMPTION
 * ============================================================================
 * Why this matters in OOP:
 * `TerminalPanel` implements the {@link GameEventListener} interface.
 *
 * 1. Observer Pattern:
 *    When game events occur (puzzles solved, items found, doors unlocked),
 *    the `GameEngine` calls `onGameEvent(event)`.
 *    The engine doesn't care that `TerminalPanel` is an AWT/Swing styled text pane;
 *    it only knows it implements `GameEventListener`.
 *
 * 2. Loose Coupling:
 *    If we ever replaced Swing with a web frontend or JavaFX, the engine wouldn't
 *    need a single line of modification.
 * ============================================================================
 */
public class TerminalPanel extends GlowPanel implements GameEventListener {

    private final JTextPane textPane;
    private final StyledDocument doc;
    private final DateTimeFormatter timeFormat;

    private Style styleNormal;
    private Style styleSuccess;
    private Style styleWarning;
    private Style styleDanger;
    private Style styleTimestamp;

    public TerminalPanel() {
        super(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 8);
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(320, 200));

        this.timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 4, 10));

        javax.swing.JLabel titleLabel = new javax.swing.JLabel("System Log & Activity");
        titleLabel.setFont(Theme.FONT_HEADER);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        SciFiButton clearBtn = new SciFiButton("Clear", SciFiButton.ButtonStyle.SECONDARY);
        clearBtn.setPreferredSize(new Dimension(60, 22));
        clearBtn.setFont(Theme.FONT_BODY);
        clearBtn.addActionListener(e -> clearConsole());
        headerPanel.add(clearBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Terminal Output Pane
        this.textPane = new JTextPane();
        this.textPane.setEditable(false);
        this.textPane.setBackground(Theme.BG_INPUT);
        this.textPane.setFont(Theme.FONT_TERMINAL);
        this.textPane.setMargin(new java.awt.Insets(6, 8, 6, 8));
        this.doc = textPane.getStyledDocument();

        setupStyles();

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        logInitialGreeting();
    }

    private void setupStyles() {
        styleNormal = doc.addStyle("Normal", null);
        StyleConstants.setForeground(styleNormal, Theme.TEXT_PRIMARY);
        StyleConstants.setFontFamily(styleNormal, "Consolas");

        styleSuccess = doc.addStyle("Success", null);
        StyleConstants.setForeground(styleSuccess, Theme.EMERALD);
        StyleConstants.setFontFamily(styleSuccess, "Consolas");

        styleWarning = doc.addStyle("Warning", null);
        StyleConstants.setForeground(styleWarning, Theme.AMBER);
        StyleConstants.setFontFamily(styleWarning, "Consolas");

        styleDanger = doc.addStyle("Danger", null);
        StyleConstants.setForeground(styleDanger, Theme.ROSE);
        StyleConstants.setBold(styleDanger, true);
        StyleConstants.setFontFamily(styleDanger, "Consolas");

        styleTimestamp = doc.addStyle("Timestamp", null);
        StyleConstants.setForeground(styleTimestamp, Theme.TEXT_DIM);
        StyleConstants.setFontFamily(styleTimestamp, "Consolas");
    }

    private void logInitialGreeting() {
        appendEntry("System online. Search fixtures for clues & tools. Crack 3 locks to escape.", styleNormal);
    }

    @Override
    public void onGameEvent(GameEvent event) {
        // Dispatched from any thread -> guarantee execution on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            Style targetStyle = switch (event.getType()) {
                case PUZZLE_SOLVED, DOOR_UNLOCKED, GAME_WON -> styleSuccess;
                case PUZZLE_FAILED, TIME_EXPIRED -> styleDanger;
                case ITEM_COLLECTED, HINT_REQUESTED -> styleWarning;
                default -> styleNormal;
            };

            // Filter out continuous timer ticks to avoid flooding console text
            if (event.getType() != escapex.observer.GameEventType.TIMER_TICK) {
                appendEntry(event.getMessage(), targetStyle);
            }
        });
    }

    public void appendEntry(String text, Style style) {
        try {
            String timestamp = "[" + LocalTime.now().format(timeFormat) + "] ";
            doc.insertString(doc.getLength(), timestamp, styleTimestamp);
            doc.insertString(doc.getLength(), text + "\n", style);
            textPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException ignored) {
        }
    }

    public void clearConsole() {
        try {
            doc.remove(0, doc.getLength());
            logInitialGreeting();
        } catch (BadLocationException ignored) {
        }
    }
}
