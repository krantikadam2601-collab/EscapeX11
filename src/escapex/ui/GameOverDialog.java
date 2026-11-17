package escapex.ui;

import escapex.model.Player;
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
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: GAME OVER SCREEN VIEW & DOMAIN METRIC AGGREGATION
 * ============================================================================
 * Why this matters in OOP:
 * `GameOverDialog` encapsulates the mission failure debriefing screen when
 * the countdown timer expires.
 *
 * It pulls together domain statistics:
 *   - Final Score
 *   - Total Time Elapsed
 *   - Puzzles Solved
 *   - Sectors Completed before quarantine
 * ============================================================================
 */
public class GameOverDialog extends JDialog {

    public interface GameOverActionListener {
        void onTryAgain();
        void onLoadCheckpoint();
        void onMainMenu();
        void onExit();
    }

    private final GameEngine gameEngine;
    private final GameOverActionListener listener;

    public GameOverDialog(Frame owner, GameEngine gameEngine, GameOverActionListener listener) {
        super(owner, "EscapeX - Containment Failure", true);
        this.gameEngine = gameEngine;
        this.listener = listener;

        setSize(540, 560);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_DARK);

        initUI();
    }

    private void initUI() {
        JPanel mainCard = new GlowPanel(Theme.ROSE, Theme.BG_PANEL, 14);
        mainCard.setLayout(new BorderLayout(12, 12));
        mainCard.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        Player player = gameEngine.getPlayer();
        GameTimer timer = gameEngine.getGameTimer();
        int elapsedSeconds = (timer != null) ? timer.getSecondsElapsed() : 0;
        int solvedCount = gameEngine.getTotalSolvedPuzzlesCount();
        int totalPuzzles = gameEngine.getTotalPuzzlesCount();
        int currentRoomIdx = gameEngine.getCurrentRoomIndex();

        // 1. ALERT HEADER
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel alertTag = new JLabel("⚠ ⚠ ⚠  FACILITY CONTAINMENT BREACH  ⚠ ⚠ ⚠", SwingConstants.CENTER);
        alertTag.setFont(Theme.FONT_TERMINAL_BOLD);
        alertTag.setForeground(Theme.ROSE);
        alertTag.setAlignmentX(CENTER_ALIGNMENT);
        header.add(alertTag);

        header.add(Box.createVerticalStrut(4));

        JLabel title = new JLabel("CONTAINMENT FAILURE", SwingConstants.CENTER);
        title.setFont(new Font("Consolas", Font.BOLD, 28));
        title.setForeground(Theme.ROSE);
        title.setAlignmentX(CENTER_ALIGNMENT);
        header.add(title);

        JLabel subtitle = new JLabel("Countdown expired. Blast doors sealed permanently.", SwingConstants.CENTER);
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_MUTED);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        header.add(subtitle);

        mainCard.add(header, BorderLayout.NORTH);

        // 2. STATS GRID
        JPanel statsPanel = new GlowPanel(Theme.BORDER_SUBTLE, Theme.BG_PANEL_ALT, 10);
        statsPanel.setLayout(new GridLayout(6, 2, 8, 8));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        addStat(statsPanel, "OPERATIVE:", player.getName(), Theme.TEXT_PRIMARY);
        addStat(statsPanel, "FINAL SCORE:", player.getScore() + " PTS", Theme.ROSE);
        addStat(statsPanel, "TIME ELAPSED:", GameTimer.formatDigitalTime(elapsedSeconds), Theme.CYAN_NEON);
        addStat(statsPanel, "PUZZLES SOLVED:", solvedCount + " / " + totalPuzzles, Theme.AMBER);
        addStat(statsPanel, "SECTORS CLEARED:", currentRoomIdx + " / 5 Cleared", Theme.TEXT_PRIMARY);
        addStat(statsPanel, "STATUS:", "PERMANENTLY SEALED", Theme.ROSE);

        mainCard.add(statsPanel, BorderLayout.CENTER);

        // 3. ACTIONS PANEL
        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setOpaque(false);

        SciFiButton tryAgainBtn = new SciFiButton("↺  TRY AGAIN (RESTART)", SciFiButton.ButtonStyle.WARNING);
        tryAgainBtn.setPreferredSize(new Dimension(380, 36));
        tryAgainBtn.setMaximumSize(new Dimension(380, 36));
        tryAgainBtn.setAlignmentX(CENTER_ALIGNMENT);
        tryAgainBtn.addActionListener(e -> {
            dispose();
            if (listener != null) listener.onTryAgain();
        });
        actions.add(tryAgainBtn);

        actions.add(Box.createVerticalStrut(8));

        boolean saveExists = new File("escapex_savegame.dat").exists();
        SciFiButton loadBtn = new SciFiButton("↺  RESTORE SAVED CHECKPOINT", SciFiButton.ButtonStyle.PRIMARY);
        loadBtn.setEnabled(saveExists);
        loadBtn.setPreferredSize(new Dimension(380, 34));
        loadBtn.setMaximumSize(new Dimension(380, 34));
        loadBtn.setAlignmentX(CENTER_ALIGNMENT);
        loadBtn.addActionListener(e -> {
            dispose();
            if (listener != null) listener.onLoadCheckpoint();
        });
        actions.add(loadBtn);

        actions.add(Box.createVerticalStrut(8));

        SciFiButton menuBtn = new SciFiButton("⌂  RETURN TO MAIN MENU", SciFiButton.ButtonStyle.SECONDARY);
        menuBtn.setPreferredSize(new Dimension(380, 34));
        menuBtn.setMaximumSize(new Dimension(380, 34));
        menuBtn.setAlignmentX(CENTER_ALIGNMENT);
        menuBtn.addActionListener(e -> {
            dispose();
            if (listener != null) listener.onMainMenu();
        });
        actions.add(menuBtn);

        actions.add(Box.createVerticalStrut(8));

        SciFiButton exitBtn = new SciFiButton("✕  EXIT APPLICATION", SciFiButton.ButtonStyle.DANGER);
        exitBtn.setPreferredSize(new Dimension(380, 32));
        exitBtn.setMaximumSize(new Dimension(380, 32));
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> {
            dispose();
            if (listener != null) listener.onExit();
        });
        actions.add(exitBtn);

        mainCard.add(actions, BorderLayout.SOUTH);

        setContentPane(mainCard);
    }

    private void addStat(JPanel panel, String label, String value, Color valColor) {
        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_TERMINAL_BOLD);
        l.setForeground(Theme.TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Consolas", Font.BOLD, 13));
        v.setForeground(valColor);

        panel.add(l);
        panel.add(v);
    }
}
