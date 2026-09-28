package escapex.ui;

import escapex.exception.GameSaveException;
import escapex.exception.RoomLockedException;
import escapex.model.Room;
import escapex.model.puzzle.Puzzle;
import escapex.model.puzzle.PuzzleDifficulty;
import escapex.observer.GameEvent;
import escapex.observer.GameEventListener;
import escapex.observer.GameEventType;
import escapex.service.GameEngine;
import escapex.service.GameTimer;
import escapex.ui.components.GlowPanel;
import escapex.ui.components.SciFiButton;

import java.util.List;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: VIEW IN MVC, COMPOSITION & OBSERVER PATTERN
 * ============================================================================
 * Why this matters in OOP:
 * `EscapeXFrame` is the primary top-level window (extending {@link JFrame}).
 *
 * It demonstrates:
 * 1. View in MVC: It displays state from the Model and delegates player actions
 *    directly to the Controller (`GameEngine`).
 * 2. Composition (Has-A):
 *    Composed of `RoomViewPanel`, `TerminalPanel`, `InventoryPanel`, and custom HUD panels.
 * 3. Observer Pattern:
 *    Implements `GameEventListener`. When the `GameEngine` ticks or fires events,
 *    `EscapeXFrame` receives them and updates the HUD timer, score, and sector tracker.
 * ============================================================================
 */
public class EscapeXFrame extends JFrame implements GameEventListener {

    private final GameEngine gameEngine;

    // View Components
    private RoomViewPanel roomViewPanel;
    private TerminalPanel terminalPanel;
    private InventoryPanel inventoryPanel;

    // HUD labels
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private JLabel sectorStatusLabel;
    private JLabel puzzleProgressLabel;
    private JPanel sectorBreadcrumbPanel;

    // Right quick puzzle buttons
    private SciFiButton easyPuzzleBtn;
    private SciFiButton medPuzzleBtn;
    private SciFiButton hardPuzzleBtn;
    private SciFiButton soundToggleBtn;

    public EscapeXFrame(GameEngine gameEngine) {
        super("EscapeX: Intelligent Escape Room Adventure");
        this.gameEngine = gameEngine;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 800);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_DARK);

        // Register this frame as an observer of game events!
        gameEngine.addListener(this);

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout(0, 4));
        rootPanel.setBackground(Theme.BG_DARK);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        // 1. TOP HUD & SECTOR TRACKER
        rootPanel.add(buildTopHUD(), BorderLayout.NORTH);

        // 2. CENTER: Split between 2D Room Viewport and Right Sidebar
        rootPanel.add(buildCenterSplit(), BorderLayout.CENTER);

        // 3. BOTTOM CONTROL BAR
        rootPanel.add(buildBottomControlBar(), BorderLayout.SOUTH);

        setContentPane(rootPanel);

        // Register sub-panels as observers
        gameEngine.addListener(terminalPanel);
        gameEngine.addListener(inventoryPanel);

        updateHUD();
    }

    // ========================================================================
    // TOP HUD COMPONENT BUILDER
    // ========================================================================
    private JPanel buildTopHUD() {
        JPanel hudPanel = new GlowPanel(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 8);
        hudPanel.setLayout(new BorderLayout(10, 0));
        hudPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        // Left: Game Title & Subtitle
        JPanel leftMeta = new JPanel();
        leftMeta.setLayout(new BoxLayout(leftMeta, BoxLayout.Y_AXIS));
        leftMeta.setOpaque(false);

        JLabel gameTitle = new JLabel("EscapeX");
        gameTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gameTitle.setForeground(Theme.TEXT_PRIMARY);
        leftMeta.add(gameTitle);

        sectorStatusLabel = new JLabel("Player: " + gameEngine.getPlayer().getName());
        sectorStatusLabel.setFont(Theme.FONT_BODY);
        sectorStatusLabel.setForeground(Theme.TEXT_MUTED);
        leftMeta.add(sectorStatusLabel);

        hudPanel.add(leftMeta, BorderLayout.WEST);

        // Center: 5-Sector Progression Breadcrumb Tracker (Single horizontal row)
        sectorBreadcrumbPanel = new JPanel();
        sectorBreadcrumbPanel.setLayout(new BoxLayout(sectorBreadcrumbPanel, BoxLayout.X_AXIS));
        sectorBreadcrumbPanel.setOpaque(false);
        buildSectorBreadcrumbs();

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        centerWrapper.setOpaque(false);
        centerWrapper.add(sectorBreadcrumbPanel);
        hudPanel.add(centerWrapper, BorderLayout.CENTER);

        // Right: Digital Timer, Score, and Sound Toggle
        JPanel rightHUD = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHUD.setOpaque(false);

        // Diagnostics counter
        puzzleProgressLabel = new JLabel("Puzzles: 0/3");
        puzzleProgressLabel.setFont(Theme.FONT_BODY_BOLD);
        puzzleProgressLabel.setForeground(Theme.AMBER);
        rightHUD.add(puzzleProgressLabel);

        // Score
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(Theme.FONT_BODY_BOLD);
        scoreLabel.setForeground(Theme.EMERALD);
        rightHUD.add(scoreLabel);

        // Timer
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(Theme.FONT_TIMER);
        timerLabel.setForeground(Theme.ACCENT_BLUE);
        rightHUD.add(timerLabel);

        // Sound Toggle
        soundToggleBtn = new SciFiButton("Audio: On", SciFiButton.ButtonStyle.SECONDARY);
        soundToggleBtn.setPreferredSize(new Dimension(85, 26));
        soundToggleBtn.setFont(Theme.FONT_BODY);
        soundToggleBtn.addActionListener(e -> toggleSound());
        rightHUD.add(soundToggleBtn);

        hudPanel.add(rightHUD, BorderLayout.EAST);

        return hudPanel;
    }

    private void buildSectorBreadcrumbs() {
        sectorBreadcrumbPanel.removeAll();
        String[] shortNames = {"1. Workshop", "2. Vault", "3. Neural", "4. Quantum", "5. Mainframe"};

        for (int i = 0; i < gameEngine.getTotalRoomsCount(); i++) {
            Room r = gameEngine.getAllRooms().get(i);
            int currentIdx = gameEngine.getCurrentRoomIndex();

            String label = (i < shortNames.length) ? shortNames[i] : "S" + (i + 1);

            JLabel badge = new JLabel(" " + label + " ");
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            badge.setOpaque(true);
            if (i == currentIdx) {
                badge.setBackground(Theme.ACCENT_BLUE);
                badge.setForeground(Color.WHITE);
            } else if (i < currentIdx || r.isDoorUnlocked()) {
                badge.setBackground(new Color(22, 101, 52)); // Dark green
                badge.setForeground(Color.WHITE);
            } else {
                badge.setBackground(Theme.BG_PANEL_ALT);
                badge.setForeground(Theme.TEXT_MUTED);
            }
            badge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER_SUBTLE),
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)
            ));
            badge.setToolTipText("Sector 0" + (i + 1) + ": " + r.getName());
            sectorBreadcrumbPanel.add(badge);

            if (i < gameEngine.getTotalRoomsCount() - 1) {
                sectorBreadcrumbPanel.add(Box.createHorizontalStrut(3));
                JLabel arrow = new JLabel("→");
                arrow.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                arrow.setForeground(Theme.TEXT_DIM);
                sectorBreadcrumbPanel.add(arrow);
                sectorBreadcrumbPanel.add(Box.createHorizontalStrut(3));
            }
        }
        sectorBreadcrumbPanel.revalidate();
        sectorBreadcrumbPanel.repaint();
    }

    // ========================================================================
    // CENTER SPLIT: 2D ROOM VIEWPORT + RIGHT SIDEBAR
    // ========================================================================
    private JSplitPane buildCenterSplit() {
        // Left: 2D Interactive Room Canvas
        roomViewPanel = new RoomViewPanel(gameEngine);

        // Right Sidebar: Puzzles quick launch + Inventory + Terminal
        JPanel rightSidebar = new JPanel(new BorderLayout(0, 6));
        rightSidebar.setOpaque(false);
        rightSidebar.setPreferredSize(new Dimension(380, 500));

        // Diagnostics Launch Box
        JPanel puzzleBox = new GlowPanel(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 8);
        puzzleBox.setLayout(new BorderLayout(5, 5));
        puzzleBox.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel pzTitle = new JLabel("Sector Puzzles");
        pzTitle.setFont(Theme.FONT_HEADER);
        pzTitle.setForeground(Theme.TEXT_PRIMARY);
        puzzleBox.add(pzTitle, BorderLayout.NORTH);

        JPanel pzButtons = new JPanel(new GridLayout(3, 1, 0, 6));
        pzButtons.setOpaque(false);

        easyPuzzleBtn = new SciFiButton("Easy Puzzle", SciFiButton.ButtonStyle.SECONDARY);
        easyPuzzleBtn.addActionListener(e -> openPuzzleDialog(PuzzleDifficulty.EASY));
        pzButtons.add(easyPuzzleBtn);

        medPuzzleBtn = new SciFiButton("Medium Puzzle", SciFiButton.ButtonStyle.SECONDARY);
        medPuzzleBtn.addActionListener(e -> openPuzzleDialog(PuzzleDifficulty.MEDIUM));
        pzButtons.add(medPuzzleBtn);

        hardPuzzleBtn = new SciFiButton("Hard Puzzle", SciFiButton.ButtonStyle.SECONDARY);
        hardPuzzleBtn.addActionListener(e -> openPuzzleDialog(PuzzleDifficulty.HARD));
        pzButtons.add(hardPuzzleBtn);

        puzzleBox.add(pzButtons, BorderLayout.CENTER);
        rightSidebar.add(puzzleBox, BorderLayout.NORTH);

        // Middle: Inventory Rack
        inventoryPanel = new InventoryPanel(gameEngine);
        rightSidebar.add(inventoryPanel, BorderLayout.CENTER);

        // Bottom: Terminal Console
        terminalPanel = new TerminalPanel();
        rightSidebar.add(terminalPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, roomViewPanel, rightSidebar);
        splitPane.setResizeWeight(0.68);
        splitPane.setDividerSize(4);
        splitPane.setBorder(null);
        splitPane.setBackground(Theme.BG_DARK);

        return splitPane;
    }

    // ========================================================================
    // BOTTOM CONTROL & ACTION BAR
    // ========================================================================
    private JPanel buildBottomControlBar() {
        JPanel bottomBar = new GlowPanel(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 8);
        bottomBar.setLayout(new BorderLayout(10, 0));
        bottomBar.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // Left Controls: Save, Load, Leaderboard, OOP Inspector
        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftActions.setOpaque(false);

        SciFiButton saveBtn = new SciFiButton("Save Game", SciFiButton.ButtonStyle.SECONDARY);
        saveBtn.addActionListener(e -> handleSaveGame());
        leftActions.add(saveBtn);

        SciFiButton loadBtn = new SciFiButton("Load Game", SciFiButton.ButtonStyle.SECONDARY);
        loadBtn.addActionListener(e -> handleLoadGame());
        leftActions.add(loadBtn);

        SciFiButton oopBtn = new SciFiButton("OOP Architecture Guide", SciFiButton.ButtonStyle.PRIMARY);
        oopBtn.addActionListener(e -> openOopConceptsGuide());
        leftActions.add(oopBtn);

        SciFiButton hallOfFameBtn = new SciFiButton("Hall of Fame", SciFiButton.ButtonStyle.SECONDARY);
        hallOfFameBtn.addActionListener(e -> openLeaderboard());
        leftActions.add(hallOfFameBtn);

        bottomBar.add(leftActions, BorderLayout.WEST);

        // Right Controls: Explore, Solve, Advance
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);

        SciFiButton exploreBtn = new SciFiButton("Explore Fixtures", SciFiButton.ButtonStyle.SECONDARY);
        exploreBtn.addActionListener(e -> showFixtureSelectorDialog());
        rightActions.add(exploreBtn);

        SciFiButton solveBtn = new SciFiButton("Solve Puzzles", SciFiButton.ButtonStyle.WARNING);
        solveBtn.addActionListener(e -> openPuzzleDialog(PuzzleDifficulty.EASY));
        rightActions.add(solveBtn);

        SciFiButton advanceBtn = new SciFiButton("Advance to Next Room →", SciFiButton.ButtonStyle.SUCCESS);
        advanceBtn.addActionListener(e -> handleAdvanceSector());
        rightActions.add(advanceBtn);

        bottomBar.add(rightActions, BorderLayout.EAST);

        return bottomBar;
    }

    // ========================================================================
    // ACTION HANDLERS
    // ========================================================================

    private void openPuzzleDialog(PuzzleDifficulty difficulty) {
        new PuzzleDialog(this, gameEngine, difficulty).setVisible(true);
    }

    private void openOopConceptsGuide() {
        new OopConceptsDialog(this).setVisible(true);
    }

    private void openLeaderboard() {
        new HighScoreDialog(this, gameEngine.getHighScoreService()).setVisible(true);
    }

    private void toggleSound() {
        gameEngine.getSoundEngine().toggleSound();
        boolean enabled = gameEngine.getSoundEngine().isSoundEnabled();
        soundToggleBtn.setText(enabled ? "AUDIO: ON" : "AUDIO: MUTED");
    }

    private void handleAdvanceSector() {
        try {
            gameEngine.moveToNextRoom();
        } catch (RoomLockedException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Blast Door Sealed",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void handleSaveGame() {
        try {
            gameEngine.saveGame(new File("escapex_savegame.dat"));
            JOptionPane.showMessageDialog(
                    this,
                    "Simulation state saved successfully to 'escapex_savegame.dat'!",
                    "Game Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (GameSaveException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Save Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleLoadGame() {
        try {
            gameEngine.loadGame(new File("escapex_savegame.dat"));
            JOptionPane.showMessageDialog(
                    this,
                    "Game restored successfully!",
                    "Game Loaded",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (GameSaveException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Load Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Fallback menu for exploring room objects via dialog if mouse clicking on canvas is inconvenient.
     */
    private void showFixtureSelectorDialog() {
        Room current = gameEngine.getCurrentRoom();
        String[] options = current.getRoomObjects().stream()
                .filter(obj -> !"DOOR".equalsIgnoreCase(obj.getIconType()) && !"AIRLOCK".equalsIgnoreCase(obj.getIconType()))
                .map(obj -> obj.getName() + (obj.isItemHarvested() ? " [Harvested]" : ""))
                .toArray(String[]::new);

        if (options.length == 0) {
            JOptionPane.showMessageDialog(this, "No interactive fixtures left in this sector.");
            return;
        }

        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Select a fixture to investigate:",
                "Explore Sector " + current.getRoomNumber(),
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice != null) {
            for (var obj : current.getRoomObjects()) {
                if (choice.startsWith(obj.getName())) {
                    String result = gameEngine.exploreObject(obj.getId());
                    JOptionPane.showMessageDialog(this, result, obj.getName(), JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
        }
    }

    // ========================================================================
    // OBSERVER PATTERN: REACT TO GAME EVENTS
    // ========================================================================

    @Override
    public void onGameEvent(GameEvent event) {
        SwingUtilities.invokeLater(() -> {
            updateHUD();

            switch (event.getType()) {
                case TIMER_TICK -> {
                    int secondsRemaining = (int) event.getPayload();
                    timerLabel.setText(GameTimer.formatDigitalTime(secondsRemaining));
                    if (secondsRemaining < 180) {
                        timerLabel.setForeground(Theme.ROSE);
                    } else if (secondsRemaining < 600) {
                        timerLabel.setForeground(Theme.AMBER);
                    } else {
                        timerLabel.setForeground(Theme.CYAN_NEON);
                    }
                }
                case ROOM_CHANGED, PUZZLE_SOLVED, DOOR_UNLOCKED, GAME_LOADED -> {
                    updateHUD();
                    buildSectorBreadcrumbs();
                    roomViewPanel.repaint();
                }
                case TIME_EXPIRED -> {
                    JOptionPane.showMessageDialog(
                            this,
                            "CONTAINMENT FAILURE!\n\nThe facility countdown reached zero.\nQuarantine doors have sealed permanently.",
                            "Mission Terminated",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                case GAME_WON -> {
                    int choice = JOptionPane.showConfirmDialog(
                            this,
                            "CONGRATULATIONS, OPERATIVE!\n\nYou successfully breached all 5 containment sectors and escaped!\n" +
                            "Final Score: " + gameEngine.getPlayer().getScore() + " pts\n\nWould you like to play again?",
                            "Facility Evacuated - Victory!",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        gameEngine.startNewGame(gameEngine.getPlayer().getName());
                    }
                }
                default -> {}
            }
        });
    }

    private void updateHUD() {
        Room room = gameEngine.getCurrentRoom();
        sectorStatusLabel.setText("Player: " + gameEngine.getPlayer().getName() + " | Sector " + room.getRoomNumber() + ": " + room.getName());
        scoreLabel.setText("Score: " + gameEngine.getPlayer().getScore() + " pts");

        int solved = room.getSolvedPuzzlesCount();
        if (room.isDoorUnlocked()) {
            puzzleProgressLabel.setText("Door: Unlocked ✓");
            puzzleProgressLabel.setForeground(Theme.EMERALD);
        } else {
            puzzleProgressLabel.setText("Door: Locked (" + solved + "/3)");
            puzzleProgressLabel.setForeground(Theme.AMBER);
        }

        // Update puzzle quick buttons
        List<Puzzle> puzzles = room.getPuzzles();
        if (puzzles.size() >= 3) {
            updateQuickButton(easyPuzzleBtn, puzzles.get(0));
            updateQuickButton(medPuzzleBtn, puzzles.get(1));
            updateQuickButton(hardPuzzleBtn, puzzles.get(2));
        }
    }

    private void updateQuickButton(SciFiButton btn, Puzzle puzzle) {
        String diff = puzzle.getDifficulty().name().charAt(0) + puzzle.getDifficulty().name().substring(1).toLowerCase();
        String status = puzzle.isSolved() ? " ✓" : "";
        btn.setText(diff + ": " + puzzle.getTitle() + status);
        btn.setStyle(puzzle.isSolved() ? SciFiButton.ButtonStyle.SUCCESS : SciFiButton.ButtonStyle.SECONDARY);
    }

    public InventoryPanel getInventoryPanel() {
        return inventoryPanel;
    }
}
