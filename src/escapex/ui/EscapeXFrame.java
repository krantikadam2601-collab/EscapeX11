package escapex.ui;

import escapex.exception.GameSaveException;
import escapex.exception.RoomLockedException;
import escapex.model.GameDifficulty;
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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
 *    Composed of `MainMenuPanel`, `RoomViewPanel`, `TerminalPanel`,
 *    `InventoryPanel`, `MissionObjectivesPanel`, and `NotificationBanner`.
 * 3. Observer Pattern:
 *    Implements `GameEventListener`. When the `GameEngine` ticks or fires events,
 *    `EscapeXFrame` receives them and updates the HUD timer, score, sector tracker,
 *    and notification overlays.
 * ============================================================================
 */
public class EscapeXFrame extends JFrame implements GameEventListener {

    private static final String CARD_MENU = "CARD_MENU";
    private static final String CARD_GAME = "CARD_GAME";

    private final GameEngine gameEngine;

    // Card Layout Container
    private CardLayout mainCardsLayout;
    private JPanel mainCardsContainer;
    private MainMenuPanel mainMenuPanel;
    private boolean inGameView = false;
    private int previousRoomNumber = 1;

    // View Components
    private RoomViewPanel roomViewPanel;
    private TerminalPanel terminalPanel;
    private InventoryPanel inventoryPanel;
    private MissionObjectivesPanel missionObjectivesPanel;
    private NotificationBanner notificationBanner;

    // HUD labels
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private JLabel sectorStatusLabel;
    private JLabel difficultyBadge;
    private JLabel puzzleProgressLabel;
    private JPanel sectorBreadcrumbPanel;

    // Quick puzzle buttons
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

        // Register this frame as an observer of game events
        gameEngine.addListener(this);

        initUI();
        initGlobalKeybindings();
    }

    private void initUI() {
        mainCardsLayout = new CardLayout();
        mainCardsContainer = new JPanel(mainCardsLayout);
        mainCardsContainer.setBackground(Theme.BG_DARK);

        // 1. Futuristic Main Menu Card
        mainMenuPanel = new MainMenuPanel(gameEngine, new MainMenuPanel.MenuActionListener() {
            @Override
            public void onNewGame(String playerName, GameDifficulty difficulty) {
                gameEngine.startNewGame(playerName, difficulty);
                previousRoomNumber = 1;
                showGameView();
            }

            @Override
            public void onContinueGame() {
                handleLoadGame();
                showGameView();
            }

            @Override
            public void onHowToPlay() {
                openHowToPlay();
            }

            @Override
            public void onOpenSettings() {
                new SettingsDialog(EscapeXFrame.this, gameEngine).setVisible(true);
            }

            @Override
            public void onOpenLeaderboard() {
                openLeaderboard();
            }

            @Override
            public void onExit() {
                handleExit();
            }
        });
        mainCardsContainer.add(mainMenuPanel, CARD_MENU);

        // 2. In-Game Active Simulation Card
        JPanel gameViewPanel = buildGameViewPanel();
        mainCardsContainer.add(gameViewPanel, CARD_GAME);

        setContentPane(mainCardsContainer);

        // Initially show Main Menu
        showMenuView();
    }

    private JPanel buildGameViewPanel() {
        JPanel rootPanel = new JPanel(new BorderLayout(0, 4));
        rootPanel.setBackground(Theme.BG_DARK);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        // Top Container: HUD + Notification Banner
        JPanel topContainer = new JPanel(new BorderLayout(0, 3));
        topContainer.setOpaque(false);
        topContainer.add(buildTopHUD(), BorderLayout.NORTH);

        notificationBanner = new NotificationBanner();
        topContainer.add(notificationBanner, BorderLayout.SOUTH);
        rootPanel.add(topContainer, BorderLayout.NORTH);

        // Center Split: 2D Chamber Canvas + Right Sidebar
        rootPanel.add(buildCenterSplit(), BorderLayout.CENTER);

        // Bottom Control & Action Bar
        rootPanel.add(buildBottomControlBar(), BorderLayout.SOUTH);

        // Register observers
        gameEngine.addListener(terminalPanel);
        gameEngine.addListener(inventoryPanel);
        gameEngine.addListener(missionObjectivesPanel);

        return rootPanel;
    }

    // ========================================================================
    // TOP HUD COMPONENT BUILDER
    // ========================================================================
    private JPanel buildTopHUD() {
        JPanel hudPanel = new GlowPanel(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 8);
        hudPanel.setLayout(new BorderLayout(10, 0));
        hudPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        // Left: Game Title, Subtitle & Difficulty Badge
        JPanel leftMeta = new JPanel();
        leftMeta.setLayout(new BoxLayout(leftMeta, BoxLayout.Y_AXIS));
        leftMeta.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleRow.setOpaque(false);

        JLabel gameTitle = new JLabel("EscapeX");
        gameTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gameTitle.setForeground(Theme.TEXT_PRIMARY);
        titleRow.add(gameTitle);

        difficultyBadge = new JLabel(" " + gameEngine.getDifficulty().getLabel().toUpperCase() + " ");
        difficultyBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        difficultyBadge.setOpaque(true);
        difficultyBadge.setBackground(Theme.BG_PANEL_ALT);
        difficultyBadge.setForeground(Theme.CYAN_NEON);
        difficultyBadge.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE));
        titleRow.add(difficultyBadge);

        leftMeta.add(titleRow);

        sectorStatusLabel = new JLabel("Player: " + gameEngine.getPlayer().getName());
        sectorStatusLabel.setFont(Theme.FONT_BODY);
        sectorStatusLabel.setForeground(Theme.TEXT_MUTED);
        leftMeta.add(sectorStatusLabel);

        hudPanel.add(leftMeta, BorderLayout.WEST);

        // Center: 5-Sector Progression Breadcrumb Tracker
        sectorBreadcrumbPanel = new JPanel();
        sectorBreadcrumbPanel.setLayout(new BoxLayout(sectorBreadcrumbPanel, BoxLayout.X_AXIS));
        sectorBreadcrumbPanel.setOpaque(false);
        buildSectorBreadcrumbs();

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        centerWrapper.setOpaque(false);
        centerWrapper.add(sectorBreadcrumbPanel);
        hudPanel.add(centerWrapper, BorderLayout.CENTER);

        // Right: Digital Timer, Score, Pause & Sound Toggle
        JPanel rightHUD = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightHUD.setOpaque(false);

        puzzleProgressLabel = new JLabel("Puzzles: 0/3");
        puzzleProgressLabel.setFont(Theme.FONT_BODY_BOLD);
        puzzleProgressLabel.setForeground(Theme.AMBER);
        rightHUD.add(puzzleProgressLabel);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(Theme.FONT_BODY_BOLD);
        scoreLabel.setForeground(Theme.EMERALD);
        rightHUD.add(scoreLabel);

        timerLabel = new JLabel("00:00");
        timerLabel.setFont(Theme.FONT_TIMER);
        timerLabel.setForeground(Theme.ACCENT_BLUE);
        rightHUD.add(timerLabel);

        SciFiButton pauseQuickBtn = new SciFiButton("Pause", SciFiButton.ButtonStyle.SECONDARY);
        pauseQuickBtn.setPreferredSize(new Dimension(68, 26));
        pauseQuickBtn.setFont(Theme.FONT_BODY);
        pauseQuickBtn.addActionListener(e -> openPauseMenu());
        rightHUD.add(pauseQuickBtn);

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
        roomViewPanel = new RoomViewPanel(gameEngine);

        JPanel rightSidebar = new JPanel(new BorderLayout(0, 6));
        rightSidebar.setOpaque(false);
        rightSidebar.setPreferredSize(new Dimension(380, 500));

        // Mission Objectives Panel
        missionObjectivesPanel = new MissionObjectivesPanel(gameEngine);

        // Diagnostics Launch Box
        JPanel puzzleBox = new GlowPanel(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 8);
        puzzleBox.setLayout(new BorderLayout(5, 5));
        puzzleBox.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JLabel pzTitle = new JLabel("Sector Puzzles");
        pzTitle.setFont(Theme.FONT_HEADER);
        pzTitle.setForeground(Theme.TEXT_PRIMARY);
        puzzleBox.add(pzTitle, BorderLayout.NORTH);

        JPanel pzButtons = new JPanel(new GridLayout(3, 1, 0, 5));
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

        // Top Sidebar Section (Objectives + Puzzles)
        JPanel topSidebar = new JPanel(new BorderLayout(0, 5));
        topSidebar.setOpaque(false);
        topSidebar.add(missionObjectivesPanel, BorderLayout.NORTH);
        topSidebar.add(puzzleBox, BorderLayout.CENTER);
        rightSidebar.add(topSidebar, BorderLayout.NORTH);

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

        // Left Controls: Save, Load, OOP Guide, Hall of Fame, How to Play
        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftActions.setOpaque(false);

        SciFiButton saveBtn = new SciFiButton("Save", SciFiButton.ButtonStyle.SECONDARY);
        saveBtn.addActionListener(e -> handleSaveGame());
        leftActions.add(saveBtn);

        SciFiButton loadBtn = new SciFiButton("Load", SciFiButton.ButtonStyle.SECONDARY);
        loadBtn.addActionListener(e -> handleLoadGame());
        leftActions.add(loadBtn);

        SciFiButton howToPlayBtn = new SciFiButton("How to Play", SciFiButton.ButtonStyle.SECONDARY);
        howToPlayBtn.addActionListener(e -> openHowToPlay());
        leftActions.add(howToPlayBtn);

        SciFiButton oopBtn = new SciFiButton("OOP Guide", SciFiButton.ButtonStyle.PRIMARY);
        oopBtn.addActionListener(e -> openOopConceptsGuide());
        leftActions.add(oopBtn);

        SciFiButton hallOfFameBtn = new SciFiButton("Hall of Fame", SciFiButton.ButtonStyle.SECONDARY);
        hallOfFameBtn.addActionListener(e -> openLeaderboard());
        leftActions.add(hallOfFameBtn);

        bottomBar.add(leftActions, BorderLayout.WEST);

        // Right Controls: Pause, Explore, Solve, Advance
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightActions.setOpaque(false);

        SciFiButton pauseBtn = new SciFiButton("Pause [Esc]", SciFiButton.ButtonStyle.SECONDARY);
        pauseBtn.addActionListener(e -> openPauseMenu());
        rightActions.add(pauseBtn);

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
    // KEYBOARD SHORTCUTS & NAVIGATION
    // ========================================================================
    private void initGlobalKeybindings() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_P) {
                        if (inGameView && !gameEngine.isPaused() && !gameEngine.isGameOver() && !gameEngine.isGameWon()) {
                            openPauseMenu();
                            return true;
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_H && inGameView && !gameEngine.isPaused()) {
                        openHowToPlay();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void showGameView() {
        this.inGameView = true;
        mainCardsLayout.show(mainCardsContainer, CARD_GAME);
        this.previousRoomNumber = gameEngine.getCurrentRoom().getRoomNumber();
        updateHUD();
        buildSectorBreadcrumbs();
        roomViewPanel.repaint();
    }

    public void showMenuView() {
        this.inGameView = false;
        if (gameEngine.getGameTimer() != null) {
            gameEngine.getGameTimer().stop();
        }
        mainMenuPanel.refreshContinueButtonState();
        mainCardsLayout.show(mainCardsContainer, CARD_MENU);
    }

    // ========================================================================
    // ACTION HANDLERS
    // ========================================================================

    public void openPauseMenu() {
        if (gameEngine.isGameOver() || gameEngine.isGameWon()) return;

        gameEngine.pauseGame();
        PauseDialog dialog = new PauseDialog(this, gameEngine, new PauseDialog.PauseListener() {
            @Override
            public void onResume() {
                gameEngine.resumeGame();
            }

            @Override
            public void onRestart() {
                gameEngine.startNewGame(gameEngine.getPlayer().getName(), gameEngine.getDifficulty());
                previousRoomNumber = 1;
                showGameView();
            }

            @Override
            public void onMainMenu() {
                showMenuView();
            }

            @Override
            public void onHowToPlay() {
                openHowToPlay();
            }
        });
        dialog.setVisible(true);
    }

    private void openHowToPlay() {
        new HowToPlayDialog(this).setVisible(true);
    }

    private void openPuzzleDialog(PuzzleDifficulty difficulty) {
        if (gameEngine.isPaused()) return;
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
        if (gameEngine.isPaused()) return;

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

    public void handleSaveGame() {
        try {
            gameEngine.saveGame(new File("escapex_savegame.dat"));
            if (notificationBanner != null) {
                notificationBanner.showNotification(
                        NotificationBanner.NotificationType.ITEM_COLLECTED,
                        "Simulation checkpoint saved to disk ('escapex_savegame.dat')!"
                );
            }
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

    public void handleLoadGame() {
        try {
            gameEngine.loadGame(new File("escapex_savegame.dat"));
            if (notificationBanner != null) {
                notificationBanner.showNotification(
                        NotificationBanner.NotificationType.ITEM_COLLECTED,
                        "Simulation checkpoint restored successfully!"
                );
            }
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

    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit EscapeX?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void showFixtureSelectorDialog() {
        if (gameEngine.isPaused()) return;

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
                    var itemDiscovered = (!obj.isItemHarvested() && obj.hasCollectibleItem())
                            ? obj.getHiddenItem()
                            : null;
                    String result = gameEngine.exploreObject(obj.getId());
                    new FixtureDiscoveryDialog(this, obj.getName(), result, itemDiscovered).setVisible(true);
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
                case ROOM_CHANGED -> {
                    updateHUD();
                    buildSectorBreadcrumbs();
                    roomViewPanel.repaint();

                    Room current = gameEngine.getCurrentRoom();
                    if (current != null && current.getRoomNumber() > previousRoomNumber && previousRoomNumber > 0 && inGameView) {
                        new SectorTransitionDialog(this, current, previousRoomNumber).setVisible(true);
                    }
                    if (current != null) {
                        previousRoomNumber = current.getRoomNumber();
                    }
                }
                case PUZZLE_SOLVED -> {
                    updateHUD();
                    buildSectorBreadcrumbs();
                    roomViewPanel.repaint();
                    if (notificationBanner != null) {
                        notificationBanner.showNotification(
                                NotificationBanner.NotificationType.PUZZLE_SOLVED,
                                event.getMessage()
                        );
                    }
                }
                case ITEM_COLLECTED -> {
                    if (notificationBanner != null) {
                        notificationBanner.showNotification(
                                NotificationBanner.NotificationType.ITEM_COLLECTED,
                                event.getMessage()
                        );
                    }
                }
                case DOOR_UNLOCKED -> {
                    updateHUD();
                    buildSectorBreadcrumbs();
                    roomViewPanel.repaint();
                    if (notificationBanner != null) {
                        notificationBanner.showNotification(
                                NotificationBanner.NotificationType.DOOR_UNLOCKED,
                                event.getMessage()
                        );
                    }
                }
                case PUZZLE_FAILED -> {
                    if (notificationBanner != null) {
                        notificationBanner.showNotification(
                                NotificationBanner.NotificationType.DANGER,
                                event.getMessage()
                        );
                    }
                }
                case GAME_LOADED, DIFFICULTY_CHANGED -> {
                    updateHUD();
                    buildSectorBreadcrumbs();
                    roomViewPanel.repaint();
                }
                case TIME_EXPIRED -> {
                    GameOverDialog gameOverDialog = new GameOverDialog(this, gameEngine, new GameOverDialog.GameOverActionListener() {
                        @Override
                        public void onTryAgain() {
                            gameEngine.startNewGame(gameEngine.getPlayer().getName(), gameEngine.getDifficulty());
                            previousRoomNumber = 1;
                            showGameView();
                        }

                        @Override
                        public void onLoadCheckpoint() {
                            handleLoadGame();
                            showGameView();
                        }

                        @Override
                        public void onMainMenu() {
                            showMenuView();
                        }

                        @Override
                        public void onExit() {
                            handleExit();
                        }
                    });
                    gameOverDialog.setVisible(true);
                }
                case GAME_WON -> {
                    VictoryDialog victoryDialog = new VictoryDialog(this, gameEngine, new VictoryDialog.VictoryActionListener() {
                        @Override
                        public void onPlayAgain() {
                            gameEngine.startNewGame(gameEngine.getPlayer().getName(), gameEngine.getDifficulty());
                            previousRoomNumber = 1;
                            showGameView();
                        }

                        @Override
                        public void onMainMenu() {
                            showMenuView();
                        }

                        @Override
                        public void onOpenLeaderboard() {
                            openLeaderboard();
                        }

                        @Override
                        public void onExit() {
                            handleExit();
                        }
                    });
                    victoryDialog.setVisible(true);
                }
                default -> {}
            }
        });
    }

    private void updateHUD() {
        Room room = gameEngine.getCurrentRoom();
        sectorStatusLabel.setText("Player: " + gameEngine.getPlayer().getName() + " | Sector " + room.getRoomNumber() + ": " + room.getName());
        scoreLabel.setText("Score: " + gameEngine.getPlayer().getScore() + " pts");
        difficultyBadge.setText(" " + gameEngine.getDifficulty().getLabel().toUpperCase() + " [" + gameEngine.getDifficulty().getFormattedDuration() + "] ");

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
