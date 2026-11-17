package escapex.ui;

import escapex.model.GameDifficulty;
import escapex.service.GameEngine;
import escapex.ui.components.GlowPanel;
import escapex.ui.components.SciFiButton;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: VIEW COMPONENT & EVENT DELEGATION
 * ============================================================================
 * Why this matters in OOP:
 * `MainMenuPanel` encapsulates the futuristic welcome screen of EscapeX.
 *
 * 1. Single Responsibility Principle (SRP):
 *    Responsible solely for presenting the welcome screen, capturing player
 *    credentials and game configuration (difficulty, name), and delegating
 *    launch commands to the main controller (`GameEngine`) and parent frame.
 *
 * 2. Visual Polish & Cyberpunk Aesthetics:
 *    Renders custom gridlines, glowing title banners, and interactive difficulty
 *    selectors maintaining strict cohesion with the centralized `Theme`.
 * ============================================================================
 */
public class MainMenuPanel extends JPanel {

    public interface MenuActionListener {
        void onNewGame(String playerName, GameDifficulty difficulty);
        void onContinueGame();
        void onHowToPlay();
        void onOpenSettings();
        void onOpenLeaderboard();
        void onExit();
    }

    private final GameEngine gameEngine;
    private final MenuActionListener listener;

    private JTextField nameInputField;
    private GameDifficulty selectedDifficulty;
    private SciFiButton easyDiffBtn;
    private SciFiButton normalDiffBtn;
    private SciFiButton hardDiffBtn;
    private JLabel diffDescLabel;
    private SciFiButton continueBtn;

    public MainMenuPanel(GameEngine gameEngine, MenuActionListener listener) {
        this.gameEngine = gameEngine;
        this.listener = listener;
        this.selectedDifficulty = gameEngine.getDifficulty();

        setLayout(new BorderLayout());
        setBackground(Theme.BG_DARK);

        initUI();
    }

    private void initUI() {
        // Centered Main Content Card
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        centerWrapper.setOpaque(false);

        JPanel mainCard = new GlowPanel(Theme.BORDER_GLOW, Theme.BG_PANEL, 16);
        mainCard.setLayout(new BoxLayout(mainCard, BoxLayout.Y_AXIS));
        mainCard.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));
        mainCard.setPreferredSize(new Dimension(580, 680));

        // 1. GAME TITLE BANNER
        JLabel titleLabel = new JLabel("ESCAPEX", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 46));
        titleLabel.setForeground(Theme.CYAN_NEON);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        mainCard.add(titleLabel);

        mainCard.add(Box.createVerticalStrut(4));

        JLabel subtitleLabel = new JLabel("INTELLIGENT ESCAPE ROOM ADVENTURE", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        subtitleLabel.setForeground(Theme.TEXT_MUTED);
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        mainCard.add(subtitleLabel);

        JLabel tagLabel = new JLabel("AUTOMATED QUARANTINE CONTAINMENT // PROTOCOL v2.4", SwingConstants.CENTER);
        tagLabel.setFont(Theme.FONT_TERMINAL);
        tagLabel.setForeground(Theme.TEXT_DIM);
        tagLabel.setAlignmentX(CENTER_ALIGNMENT);
        mainCard.add(tagLabel);

        mainCard.add(Box.createVerticalStrut(20));

        // 2. OPERATIVE CALLSIGN INPUT SECTION
        JPanel callsignPanel = new JPanel();
        callsignPanel.setLayout(new BoxLayout(callsignPanel, BoxLayout.Y_AXIS));
        callsignPanel.setOpaque(false);
        callsignPanel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel callsignLabel = new JLabel("OPERATIVE CALLSIGN / NAME:");
        callsignLabel.setFont(Theme.FONT_TERMINAL_BOLD);
        callsignLabel.setForeground(Theme.TEXT_PRIMARY);
        callsignLabel.setAlignmentX(CENTER_ALIGNMENT);
        callsignPanel.add(callsignLabel);

        callsignPanel.add(Box.createVerticalStrut(6));

        nameInputField = new JTextField("Cadet Operative");
        nameInputField.setFont(new Font("Consolas", Font.BOLD, 14));
        nameInputField.setForeground(Theme.CYAN_NEON);
        nameInputField.setBackground(Theme.BG_INPUT);
        nameInputField.setCaretColor(Color.WHITE);
        nameInputField.setHorizontalAlignment(JTextField.CENTER);
        nameInputField.setPreferredSize(new Dimension(340, 36));
        nameInputField.setMaximumSize(new Dimension(340, 36));
        nameInputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT_BLUE, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        callsignPanel.add(nameInputField);

        mainCard.add(callsignPanel);

        mainCard.add(Box.createVerticalStrut(18));

        // 3. DIFFICULTY SELECTOR
        JLabel diffHeader = new JLabel("SIMULATION DIFFICULTY:");
        diffHeader.setFont(Theme.FONT_TERMINAL_BOLD);
        diffHeader.setForeground(Theme.TEXT_PRIMARY);
        diffHeader.setAlignmentX(CENTER_ALIGNMENT);
        mainCard.add(diffHeader);

        mainCard.add(Box.createVerticalStrut(6));

        JPanel diffBtnPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        diffBtnPanel.setOpaque(false);
        diffBtnPanel.setMaximumSize(new Dimension(440, 34));
        diffBtnPanel.setAlignmentX(CENTER_ALIGNMENT);

        easyDiffBtn = new SciFiButton("EASY [45m]", SciFiButton.ButtonStyle.SECONDARY);
        normalDiffBtn = new SciFiButton("NORMAL [30m]", SciFiButton.ButtonStyle.PRIMARY);
        hardDiffBtn = new SciFiButton("HARD [20m]", SciFiButton.ButtonStyle.SECONDARY);

        easyDiffBtn.addActionListener(e -> selectDifficulty(GameDifficulty.EASY));
        normalDiffBtn.addActionListener(e -> selectDifficulty(GameDifficulty.NORMAL));
        hardDiffBtn.addActionListener(e -> selectDifficulty(GameDifficulty.HARD));

        diffBtnPanel.add(easyDiffBtn);
        diffBtnPanel.add(normalDiffBtn);
        diffBtnPanel.add(hardDiffBtn);
        mainCard.add(diffBtnPanel);

        mainCard.add(Box.createVerticalStrut(6));

        diffDescLabel = new JLabel(selectedDifficulty.getDescription(), SwingConstants.CENTER);
        diffDescLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        diffDescLabel.setForeground(Theme.TEXT_MUTED);
        diffDescLabel.setAlignmentX(CENTER_ALIGNMENT);
        mainCard.add(diffDescLabel);

        mainCard.add(Box.createVerticalStrut(24));

        // 4. ACTION BUTTONS
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setOpaque(false);
        actionsPanel.setAlignmentX(CENTER_ALIGNMENT);

        // New Game
        SciFiButton newGameBtn = new SciFiButton("▶  NEW GAME", SciFiButton.ButtonStyle.PRIMARY);
        newGameBtn.setPreferredSize(new Dimension(380, 40));
        newGameBtn.setMaximumSize(new Dimension(380, 40));
        newGameBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        newGameBtn.setAlignmentX(CENTER_ALIGNMENT);
        newGameBtn.addActionListener(e -> handleNewGame());
        actionsPanel.add(newGameBtn);

        actionsPanel.add(Box.createVerticalStrut(10));

        // Continue
        boolean saveExists = new File("escapex_savegame.dat").exists();
        continueBtn = new SciFiButton(
                saveExists ? "↺  CONTINUE CHECKPOINT" : "↺  CONTINUE (NO SAVE FOUND)",
                saveExists ? SciFiButton.ButtonStyle.SUCCESS : SciFiButton.ButtonStyle.SECONDARY
        );
        continueBtn.setEnabled(saveExists);
        continueBtn.setPreferredSize(new Dimension(380, 36));
        continueBtn.setMaximumSize(new Dimension(380, 36));
        continueBtn.setAlignmentX(CENTER_ALIGNMENT);
        continueBtn.addActionListener(e -> {
            if (listener != null) listener.onContinueGame();
        });
        actionsPanel.add(continueBtn);

        actionsPanel.add(Box.createVerticalStrut(10));

        // How to Play
        SciFiButton howToPlayBtn = new SciFiButton("?  HOW TO PLAY (INSTRUCTIONS)", SciFiButton.ButtonStyle.SECONDARY);
        howToPlayBtn.setPreferredSize(new Dimension(380, 36));
        howToPlayBtn.setMaximumSize(new Dimension(380, 36));
        howToPlayBtn.setAlignmentX(CENTER_ALIGNMENT);
        howToPlayBtn.addActionListener(e -> {
            if (listener != null) listener.onHowToPlay();
        });
        actionsPanel.add(howToPlayBtn);

        actionsPanel.add(Box.createVerticalStrut(10));

        // Settings & Hall of Fame in two-column row
        JPanel utilityRow = new JPanel(new GridLayout(1, 2, 8, 0));
        utilityRow.setOpaque(false);
        utilityRow.setPreferredSize(new Dimension(380, 34));
        utilityRow.setMaximumSize(new Dimension(380, 34));
        utilityRow.setAlignmentX(CENTER_ALIGNMENT);

        SciFiButton settingsBtn = new SciFiButton("⚙  SETTINGS", SciFiButton.ButtonStyle.SECONDARY);
        settingsBtn.addActionListener(e -> {
            if (listener != null) listener.onOpenSettings();
        });
        utilityRow.add(settingsBtn);

        SciFiButton hallOfFameBtn = new SciFiButton("★  HALL OF FAME", SciFiButton.ButtonStyle.SECONDARY);
        hallOfFameBtn.addActionListener(e -> {
            if (listener != null) listener.onOpenLeaderboard();
        });
        utilityRow.add(hallOfFameBtn);

        actionsPanel.add(utilityRow);

        actionsPanel.add(Box.createVerticalStrut(10));

        // Exit
        SciFiButton exitBtn = new SciFiButton("✕  EXIT SIMULATION", SciFiButton.ButtonStyle.DANGER);
        exitBtn.setPreferredSize(new Dimension(380, 34));
        exitBtn.setMaximumSize(new Dimension(380, 34));
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> {
            if (listener != null) listener.onExit();
        });
        actionsPanel.add(exitBtn);

        mainCard.add(actionsPanel);

        mainCard.add(Box.createVerticalGlue());

        // 5. SYSTEM STATUS FOOTER
        JLabel statusLabel = new JLabel("SYSTEM STATUS: 5 SECTORS ENCRYPTED // 15 DIAGNOSTIC LOCKS", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_TERMINAL);
        statusLabel.setForeground(Theme.TEXT_DIM);
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);
        mainCard.add(statusLabel);

        centerWrapper.add(mainCard);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void selectDifficulty(GameDifficulty diff) {
        this.selectedDifficulty = diff;
        gameEngine.setDifficulty(diff);

        easyDiffBtn.setStyle(diff == GameDifficulty.EASY ? SciFiButton.ButtonStyle.PRIMARY : SciFiButton.ButtonStyle.SECONDARY);
        normalDiffBtn.setStyle(diff == GameDifficulty.NORMAL ? SciFiButton.ButtonStyle.PRIMARY : SciFiButton.ButtonStyle.SECONDARY);
        hardDiffBtn.setStyle(diff == GameDifficulty.HARD ? SciFiButton.ButtonStyle.PRIMARY : SciFiButton.ButtonStyle.SECONDARY);

        diffDescLabel.setText(diff.getDescription());
    }

    private void handleNewGame() {
        String name = nameInputField.getText().trim();
        if (name.isEmpty()) {
            name = "Cadet Operative";
        }
        if (listener != null) {
            listener.onNewGame(name, selectedDifficulty);
        }
    }

    public void refreshContinueButtonState() {
        boolean saveExists = new File("escapex_savegame.dat").exists();
        continueBtn.setEnabled(saveExists);
        continueBtn.setText(saveExists ? "↺  CONTINUE CHECKPOINT" : "↺  CONTINUE (NO SAVE FOUND)");
        continueBtn.setStyle(saveExists ? SciFiButton.ButtonStyle.SUCCESS : SciFiButton.ButtonStyle.SECONDARY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Draw subtle sci-fi grid lines
        g2.setColor(new Color(59, 130, 246, 14));
        g2.setStroke(new BasicStroke(1.0f));
        int gridSize = 32;
        for (int x = 0; x < w; x += gridSize) {
            g2.drawLine(x, 0, x, h);
        }
        for (int y = 0; y < h; y += gridSize) {
            g2.drawLine(0, y, w, y);
        }

        // 2. Corner decorative brackets
        g2.setColor(Theme.ACCENT_BLUE);
        int bracketSize = 24;
        int pad = 12;

        // Top-Left
        g2.drawLine(pad, pad, pad + bracketSize, pad);
        g2.drawLine(pad, pad, pad, pad + bracketSize);

        // Top-Right
        g2.drawLine(w - pad, pad, w - pad - bracketSize, pad);
        g2.drawLine(w - pad, pad, w - pad, pad + bracketSize);

        // Bottom-Left
        g2.drawLine(pad, h - pad, pad + bracketSize, h - pad);
        g2.drawLine(pad, h - pad, pad, h - pad - bracketSize);

        // Bottom-Right
        g2.drawLine(w - pad, h - pad, w - pad - bracketSize, h - pad);
        g2.drawLine(w - pad, h - pad, w - pad, h - pad - bracketSize);

        g2.dispose();
    }
}
