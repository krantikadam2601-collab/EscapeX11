package escapex.ui;

import escapex.model.GameDifficulty;
import escapex.model.Room;
import escapex.service.GameEngine;
import escapex.service.GameTimer;
import escapex.ui.components.GlowPanel;
import escapex.ui.components.SciFiButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: MODAL DIALOG & COMMAND PATTERN CALLBACKS
 * ============================================================================
 * Why this matters in OOP:
 * `PauseDialog` encapsulates the in-game pause state.
 *
 * 1. Encapsulated Pause Semantics:
 *    While open, the dialog freezes user input in the main frame and stops the
 *    clock ticks.
 *
 * 2. Loose Coupling via Callbacks:
 *    The dialog does not directly manipulate the parent frame's layout or cards;
 *    instead, it notifies through defined callbacks (`onResume`, `onRestart`,
 *    `onMainMenu`).
 * ============================================================================
 */
public class PauseDialog extends JDialog {

    public interface PauseListener {
        void onResume();
        void onRestart();
        void onMainMenu();
        void onHowToPlay();
    }

    private final GameEngine gameEngine;
    private final PauseListener listener;

    public PauseDialog(Frame owner, GameEngine gameEngine, PauseListener listener) {
        super(owner, "EscapeX - Simulation Suspended", true);
        this.gameEngine = gameEngine;
        this.listener = listener;

        setSize(460, 480);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_DARK);

        initUI();

        // Handle Escape or P key to resume
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_P) {
                    handleResume();
                }
            }
        });
        setFocusable(true);

        // Resume if user clicks the window X
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleResume();
            }
        });
    }

    private void initUI() {
        JPanel mainPanel = new GlowPanel(Theme.AMBER, Theme.BG_PANEL, 12);
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // 1. HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel pauseTitle = new JLabel("SIMULATION PAUSED", SwingConstants.CENTER);
        pauseTitle.setFont(new Font("Consolas", Font.BOLD, 24));
        pauseTitle.setForeground(Theme.AMBER);
        pauseTitle.setAlignmentX(CENTER_ALIGNMENT);
        headerPanel.add(pauseTitle);

        JLabel pauseSubtitle = new JLabel("Facility countdown clock & inputs suspended", SwingConstants.CENTER);
        pauseSubtitle.setFont(Theme.FONT_BODY);
        pauseSubtitle.setForeground(Theme.TEXT_MUTED);
        pauseSubtitle.setAlignmentX(CENTER_ALIGNMENT);
        headerPanel.add(pauseSubtitle);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. CENTER: STATUS SUMMARY CARD
        JPanel statusCard = new GlowPanel(Theme.BORDER_SUBTLE, Theme.BG_PANEL_ALT, 8);
        statusCard.setLayout(new GridLayout(4, 2, 8, 8));
        statusCard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        Room currentRoom = gameEngine.getCurrentRoom();
        GameDifficulty diff = gameEngine.getDifficulty();
        int secondsLeft = gameEngine.getGameTimer() != null ? gameEngine.getGameTimer().getSecondsRemaining() : 0;

        addStatRow(statusCard, "OPERATIVE:", gameEngine.getPlayer().getName());
        addStatRow(statusCard, "SECTOR:", "0" + currentRoom.getRoomNumber() + " - " + currentRoom.getName());
        addStatRow(statusCard, "DIFFICULTY:", diff.getLabel() + " (" + diff.getFormattedDuration() + ")");
        addStatRow(statusCard, "TIME REMAINING:", GameTimer.formatDigitalTime(secondsLeft));

        mainPanel.add(statusCard, BorderLayout.CENTER);

        // 3. ACTIONS PANEL
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setOpaque(false);

        // Resume
        SciFiButton resumeBtn = new SciFiButton("▶  RESUME SIMULATION [ESC]", SciFiButton.ButtonStyle.PRIMARY);
        resumeBtn.setPreferredSize(new Dimension(360, 36));
        resumeBtn.setMaximumSize(new Dimension(360, 36));
        resumeBtn.setAlignmentX(CENTER_ALIGNMENT);
        resumeBtn.addActionListener(e -> handleResume());
        actionsPanel.add(resumeBtn);

        actionsPanel.add(Box.createVerticalStrut(8));

        // Restart
        SciFiButton restartBtn = new SciFiButton("↺  RESTART SIMULATION", SciFiButton.ButtonStyle.WARNING);
        restartBtn.setPreferredSize(new Dimension(360, 34));
        restartBtn.setMaximumSize(new Dimension(360, 34));
        restartBtn.setAlignmentX(CENTER_ALIGNMENT);
        restartBtn.addActionListener(e -> handleRestart());
        actionsPanel.add(restartBtn);

        actionsPanel.add(Box.createVerticalStrut(8));

        // Instructions
        SciFiButton howToPlayBtn = new SciFiButton("?  HOW TO PLAY", SciFiButton.ButtonStyle.SECONDARY);
        howToPlayBtn.setPreferredSize(new Dimension(360, 34));
        howToPlayBtn.setMaximumSize(new Dimension(360, 34));
        howToPlayBtn.setAlignmentX(CENTER_ALIGNMENT);
        howToPlayBtn.addActionListener(e -> {
            if (listener != null) listener.onHowToPlay();
        });
        actionsPanel.add(howToPlayBtn);

        actionsPanel.add(Box.createVerticalStrut(8));

        // Main Menu
        SciFiButton mainMenuBtn = new SciFiButton("⌂  RETURN TO MAIN MENU", SciFiButton.ButtonStyle.SECONDARY);
        mainMenuBtn.setPreferredSize(new Dimension(360, 34));
        mainMenuBtn.setMaximumSize(new Dimension(360, 34));
        mainMenuBtn.setAlignmentX(CENTER_ALIGNMENT);
        mainMenuBtn.addActionListener(e -> handleMainMenu());
        actionsPanel.add(mainMenuBtn);

        actionsPanel.add(Box.createVerticalStrut(8));

        // Exit
        SciFiButton exitBtn = new SciFiButton("✕  EXIT APPLICATION", SciFiButton.ButtonStyle.DANGER);
        exitBtn.setPreferredSize(new Dimension(360, 34));
        exitBtn.setMaximumSize(new Dimension(360, 34));
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> handleExit());
        actionsPanel.add(exitBtn);

        mainPanel.add(actionsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void addStatRow(JPanel panel, String label, String value) {
        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_TERMINAL_BOLD);
        l.setForeground(Theme.TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(Theme.FONT_TERMINAL);
        v.setForeground(Theme.CYAN_NEON);

        panel.add(l);
        panel.add(v);
    }

    private void handleResume() {
        dispose();
        if (listener != null) {
            listener.onResume();
        }
    }

    private void handleRestart() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to restart the simulation from Sector 01?\nAll current session progress will be reset.",
                "Confirm Restart",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            if (listener != null) {
                listener.onRestart();
            }
        }
    }

    private void handleMainMenu() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Return to Main Menu?\nUnsaved simulation progress will be lost unless saved first.",
                "Return to Main Menu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            if (listener != null) {
                listener.onMainMenu();
            }
        }
    }

    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to terminate EscapeX and exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
