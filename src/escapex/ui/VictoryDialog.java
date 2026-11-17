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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: VICTORY SCREEN VIEW & DOMAIN METRIC AGGREGATION
 * ============================================================================
 * Why this matters in OOP:
 * `VictoryDialog` encapsulates the celebratory debriefing screen shown when an
 * operative clears all 5 quarantine sectors.
 *
 * It pulls together domain statistics from `Player`, `GameEngine`, and
 * `GameTimer`:
 *   - Final Score
 *   - Total Completion Time
 *   - Puzzles Solved (15 / 15)
 *   - Sectors Cleared (5 / 5)
 *   - Operative Clearance Rank
 * ============================================================================
 */
public class VictoryDialog extends JDialog {

    public interface VictoryActionListener {
        void onPlayAgain();
        void onMainMenu();
        void onOpenLeaderboard();
        void onExit();
    }

    private final GameEngine gameEngine;
    private final VictoryActionListener listener;

    public VictoryDialog(Frame owner, GameEngine gameEngine, VictoryActionListener listener) {
        super(owner, "EscapeX - Facility Evacuated - Victory!", true);
        this.gameEngine = gameEngine;
        this.listener = listener;

        setSize(540, 560);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_DARK);

        initUI();
    }

    private void initUI() {
        JPanel mainCard = new GlowPanel(Theme.EMERALD, Theme.BG_PANEL, 14);
        mainCard.setLayout(new BorderLayout(12, 12));
        mainCard.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        Player player = gameEngine.getPlayer();
        GameTimer timer = gameEngine.getGameTimer();
        int elapsedSeconds = (timer != null) ? timer.getSecondsElapsed() : 0;
        int solvedCount = gameEngine.getTotalSolvedPuzzlesCount();
        int totalPuzzles = gameEngine.getTotalPuzzlesCount();

        // 1. CELEBRATORY HEADER
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel crownLabel = new JLabel("★ ★ ★  MISSION ACCOMPLISHED  ★ ★ ★", SwingConstants.CENTER);
        crownLabel.setFont(Theme.FONT_TERMINAL_BOLD);
        crownLabel.setForeground(Theme.EMERALD);
        crownLabel.setAlignmentX(CENTER_ALIGNMENT);
        header.add(crownLabel);

        header.add(Box.createVerticalStrut(4));

        JLabel title = new JLabel("FACILITY EVACUATED", SwingConstants.CENTER);
        title.setFont(new Font("Consolas", Font.BOLD, 30));
        title.setForeground(Theme.EMERALD);
        title.setAlignmentX(CENTER_ALIGNMENT);
        header.add(title);

        JLabel subtitle = new JLabel("All 5 Quarantine Sectors Breached & AI Safely Contained", SwingConstants.CENTER);
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
        addStat(statsPanel, "FINAL SCORE:", player.getScore() + " PTS", Theme.EMERALD);
        addStat(statsPanel, "COMPLETION TIME:", GameTimer.formatDigitalTime(elapsedSeconds), Theme.CYAN_NEON);
        addStat(statsPanel, "PUZZLES SOLVED:", solvedCount + " / " + totalPuzzles + " (100%)", Theme.EMERALD);
        addStat(statsPanel, "SECTORS CLEARED:", "5 / 5 (All Cleared)", Theme.CYAN_NEON);
        addStat(statsPanel, "DIFFICULTY:", gameEngine.getDifficulty().getLabel(), Theme.AMBER);

        mainCard.add(statsPanel, BorderLayout.CENTER);

        // 3. ACTIONS PANEL
        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setOpaque(false);

        SciFiButton playAgainBtn = new SciFiButton("▶  PLAY AGAIN", SciFiButton.ButtonStyle.SUCCESS);
        playAgainBtn.setPreferredSize(new Dimension(380, 36));
        playAgainBtn.setMaximumSize(new Dimension(380, 36));
        playAgainBtn.setAlignmentX(CENTER_ALIGNMENT);
        playAgainBtn.addActionListener(e -> {
            dispose();
            if (listener != null) listener.onPlayAgain();
        });
        actions.add(playAgainBtn);

        actions.add(Box.createVerticalStrut(8));

        SciFiButton hallOfFameBtn = new SciFiButton("★  VIEW HALL OF FAME", SciFiButton.ButtonStyle.PRIMARY);
        hallOfFameBtn.setPreferredSize(new Dimension(380, 34));
        hallOfFameBtn.setMaximumSize(new Dimension(380, 34));
        hallOfFameBtn.setAlignmentX(CENTER_ALIGNMENT);
        hallOfFameBtn.addActionListener(e -> {
            if (listener != null) listener.onOpenLeaderboard();
        });
        actions.add(hallOfFameBtn);

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
