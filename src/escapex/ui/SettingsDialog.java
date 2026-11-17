package escapex.ui;

import escapex.model.GameDifficulty;
import escapex.service.GameEngine;
import escapex.service.SoundEngine;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: ENCAPSULATED PREFERENCES CONTROLLER & VIEW
 * ============================================================================
 * Why this matters in OOP:
 * `SettingsDialog` encapsulates the game configuration options (Difficulty,
 * Audio, Callsign).
 *
 * It decouples preference mutations from the main game loop, ensuring that
 * audio toggles and difficulty presets cleanly propagate to `GameEngine`
 * and `SoundEngine` singletons.
 * ============================================================================
 */
public class SettingsDialog extends JDialog {

    private final GameEngine gameEngine;
    private final SoundEngine soundEngine;

    private JTextField nameField;
    private GameDifficulty selectedDifficulty;
    private SciFiButton easyBtn;
    private SciFiButton normalBtn;
    private SciFiButton hardBtn;
    private SciFiButton audioToggleBtn;

    public SettingsDialog(Frame owner, GameEngine gameEngine) {
        super(owner, "EscapeX Simulation Settings & Diagnostics", true);
        this.gameEngine = gameEngine;
        this.soundEngine = gameEngine.getSoundEngine();
        this.selectedDifficulty = gameEngine.getDifficulty();

        setSize(500, 480);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_DARK);

        initUI();
    }

    private void initUI() {
        JPanel mainCard = new GlowPanel(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 12);
        mainCard.setLayout(new BorderLayout(12, 12));
        mainCard.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        // 1. HEADER
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("SIMULATION CONFIGURATION", SwingConstants.CENTER);
        title.setFont(new Font("Consolas", Font.BOLD, 22));
        title.setForeground(Theme.CYAN_NEON);
        title.setAlignmentX(CENTER_ALIGNMENT);
        header.add(title);

        JLabel subtitle = new JLabel("Operative Identity, Simulation Difficulty & Audio Subsystem", SwingConstants.CENTER);
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_MUTED);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        header.add(subtitle);

        mainCard.add(header, BorderLayout.NORTH);

        // 2. FORM BODY
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        // Callsign Setting
        JLabel nameLabel = new JLabel("OPERATIVE CALLSIGN:");
        nameLabel.setFont(Theme.FONT_TERMINAL_BOLD);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        form.add(nameLabel);
        form.add(Box.createVerticalStrut(4));

        nameField = new JTextField(gameEngine.getPlayer().getName());
        nameField.setFont(new Font("Consolas", Font.BOLD, 13));
        nameField.setForeground(Theme.CYAN_NEON);
        nameField.setBackground(Theme.BG_INPUT);
        nameField.setCaretColor(Color.WHITE);
        nameField.setPreferredSize(new Dimension(420, 32));
        nameField.setMaximumSize(new Dimension(420, 32));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        form.add(nameField);

        form.add(Box.createVerticalStrut(14));

        // Difficulty Setting
        JLabel diffLabel = new JLabel("SIMULATION DIFFICULTY:");
        diffLabel.setFont(Theme.FONT_TERMINAL_BOLD);
        diffLabel.setForeground(Theme.TEXT_PRIMARY);
        form.add(diffLabel);
        form.add(Box.createVerticalStrut(6));

        JPanel diffPanel = new JPanel(new GridLayout(1, 3, 6, 0));
        diffPanel.setOpaque(false);
        diffPanel.setPreferredSize(new Dimension(420, 32));
        diffPanel.setMaximumSize(new Dimension(420, 32));

        easyBtn = new SciFiButton("EASY (45m)", SciFiButton.ButtonStyle.SECONDARY);
        normalBtn = new SciFiButton("NORMAL (30m)", SciFiButton.ButtonStyle.SECONDARY);
        hardBtn = new SciFiButton("HARD (20m)", SciFiButton.ButtonStyle.SECONDARY);

        easyBtn.addActionListener(e -> updateDifficulty(GameDifficulty.EASY));
        normalBtn.addActionListener(e -> updateDifficulty(GameDifficulty.NORMAL));
        hardBtn.addActionListener(e -> updateDifficulty(GameDifficulty.HARD));

        diffPanel.add(easyBtn);
        diffPanel.add(normalBtn);
        diffPanel.add(hardBtn);
        form.add(diffPanel);

        updateDifficultyButtons();

        form.add(Box.createVerticalStrut(14));

        // Audio Subsystem Setting
        JLabel audioLabel = new JLabel("AUDIO HARDWARE SUBSYSTEM:");
        audioLabel.setFont(Theme.FONT_TERMINAL_BOLD);
        audioLabel.setForeground(Theme.TEXT_PRIMARY);
        form.add(audioLabel);
        form.add(Box.createVerticalStrut(6));

        boolean soundOn = soundEngine.isSoundEnabled();
        audioToggleBtn = new SciFiButton(
                soundOn ? "AUDIO: ENABLED (SYNTHESIZER ONLINE)" : "AUDIO: MUTED (HARDWARE SILENT)",
                soundOn ? SciFiButton.ButtonStyle.SUCCESS : SciFiButton.ButtonStyle.SECONDARY
        );
        audioToggleBtn.setPreferredSize(new Dimension(420, 32));
        audioToggleBtn.setMaximumSize(new Dimension(420, 32));
        audioToggleBtn.addActionListener(e -> {
            soundEngine.toggleSound();
            boolean current = soundEngine.isSoundEnabled();
            audioToggleBtn.setText(current ? "AUDIO: ENABLED (SYNTHESIZER ONLINE)" : "AUDIO: MUTED (HARDWARE SILENT)");
            audioToggleBtn.setStyle(current ? SciFiButton.ButtonStyle.SUCCESS : SciFiButton.ButtonStyle.SECONDARY);
        });
        form.add(audioToggleBtn);

        form.add(Box.createVerticalStrut(14));

        // Educational & Hall of Fame Quick Actions
        JPanel quickActions = new JPanel(new GridLayout(1, 2, 8, 0));
        quickActions.setOpaque(false);
        quickActions.setPreferredSize(new Dimension(420, 32));
        quickActions.setMaximumSize(new Dimension(420, 32));

        SciFiButton hallOfFame = new SciFiButton("Leaderboard", SciFiButton.ButtonStyle.SECONDARY);
        hallOfFame.addActionListener(e -> new HighScoreDialog(this, gameEngine.getHighScoreService()).setVisible(true));
        quickActions.add(hallOfFame);

        SciFiButton oopBtn = new SciFiButton("OOP Guide", SciFiButton.ButtonStyle.PRIMARY);
        oopBtn.addActionListener(e -> new OopConceptsDialog(this).setVisible(true));
        quickActions.add(oopBtn);

        form.add(quickActions);

        mainCard.add(form, BorderLayout.CENTER);

        // 3. BOTTOM BUTTONS
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottomBar.setOpaque(false);

        SciFiButton cancelBtn = new SciFiButton("Cancel", SciFiButton.ButtonStyle.SECONDARY);
        cancelBtn.setPreferredSize(new Dimension(100, 32));
        cancelBtn.addActionListener(e -> dispose());
        bottomBar.add(cancelBtn);

        SciFiButton saveBtn = new SciFiButton("Save Settings", SciFiButton.ButtonStyle.PRIMARY);
        saveBtn.setPreferredSize(new Dimension(120, 32));
        saveBtn.addActionListener(e -> applySettings());
        bottomBar.add(saveBtn);

        mainCard.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(mainCard);
    }

    private void updateDifficulty(GameDifficulty diff) {
        this.selectedDifficulty = diff;
        updateDifficultyButtons();
    }

    private void updateDifficultyButtons() {
        easyBtn.setStyle(selectedDifficulty == GameDifficulty.EASY ? SciFiButton.ButtonStyle.PRIMARY : SciFiButton.ButtonStyle.SECONDARY);
        normalBtn.setStyle(selectedDifficulty == GameDifficulty.NORMAL ? SciFiButton.ButtonStyle.PRIMARY : SciFiButton.ButtonStyle.SECONDARY);
        hardBtn.setStyle(selectedDifficulty == GameDifficulty.HARD ? SciFiButton.ButtonStyle.PRIMARY : SciFiButton.ButtonStyle.SECONDARY);
    }

    private void applySettings() {
        String name = nameField.getText().trim();
        if (!name.isEmpty()) {
            gameEngine.getPlayer().setName(name);
        }
        gameEngine.setDifficulty(selectedDifficulty);
        dispose();
    }
}
