package escapex.ui;

import escapex.ui.components.SciFiButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: MASTER-DETAIL PATTERN & SELF-DOCUMENTATION
 * ============================================================================
 * Why this matters in OOP:
 * Designed specifically for 2nd-year AIML engineering students learning OOP.
 *
 * Uses a clean Master-Detail layout with structured content cards.
 * Ensures zero text overlapping, proper word wrapping, clean typography,
 * and high legibility.
 * ============================================================================
 */
public class OopConceptsDialog extends JDialog {

    private final JList<String> topicList;
    private final JPanel detailContainer;
    private final JScrollPane detailScrollPane;
    private final Map<String, JPanel> topicPanelMap;
    private final JSplitPane splitPane;

    public OopConceptsDialog(Window owner) {
        super(owner, "EscapeX - Object-Oriented Architecture & Student Study Guide", ModalityType.APPLICATION_MODAL);
        setSize(960, 680);
        setMinimumSize(new Dimension(820, 560));
        setLocationRelativeTo(owner);
        getContentPane().setBackground(Theme.BG_DARK);

        this.topicPanelMap = new LinkedHashMap<>();
        buildAllTopicPanels();

        JPanel rootPanel = new JPanel(new BorderLayout(0, 10));
        rootPanel.setBackground(Theme.BG_DARK);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // 1. TOP HEADER BAR
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Object-Oriented Programming (OOP) Architecture Guide");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel subtitleLabel = new JLabel("Reference Manual for 2nd-Year AIML Engineers");
        subtitleLabel.setFont(Theme.FONT_BODY);
        subtitleLabel.setForeground(Theme.TEXT_MUTED);
        headerPanel.add(subtitleLabel, BorderLayout.EAST);

        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. MASTER-DETAIL SPLIT VIEW
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String topic : topicPanelMap.keySet()) {
            listModel.addElement(topic);
        }

        topicList = new JList<>(listModel);
        topicList.setFont(Theme.FONT_BODY_BOLD);
        topicList.setBackground(Theme.BG_PANEL);
        topicList.setForeground(Theme.TEXT_PRIMARY);
        topicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        topicList.setFixedCellHeight(44);

        topicList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 10));
                if (isSelected) {
                    label.setBackground(Theme.ACCENT_BLUE);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Theme.BG_PANEL);
                    label.setForeground(Theme.TEXT_PRIMARY);
                }
                return label;
            }
        });

        JScrollPane sidebarScroll = new JScrollPane(topicList);
        sidebarScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE));
        sidebarScroll.setPreferredSize(new Dimension(270, 400));
        sidebarScroll.setMinimumSize(new Dimension(240, 200));

        // Detail Container on Right
        detailContainer = new JPanel(new BorderLayout());
        detailContainer.setBackground(Theme.BG_DARK);

        detailScrollPane = new JScrollPane(detailContainer);
        detailScrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE));
        detailScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        detailScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarScroll, detailScrollPane);
        splitPane.setDividerLocation(260);
        splitPane.setDividerSize(4);
        splitPane.setResizeWeight(0.26);
        splitPane.setBorder(null);

        rootPanel.add(splitPane, BorderLayout.CENTER);

        // 3. BOTTOM FOOTER
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JLabel tipLabel = new JLabel("Tip: Every class in src/ contains educational header annotations explaining its OOP role.");
        tipLabel.setFont(Theme.FONT_BODY);
        tipLabel.setForeground(Theme.TEXT_MUTED);
        footerPanel.add(tipLabel, BorderLayout.WEST);

        SciFiButton closeBtn = new SciFiButton("Close Guide", SciFiButton.ButtonStyle.PRIMARY);
        closeBtn.setPreferredSize(new Dimension(130, 32));
        closeBtn.addActionListener(e -> dispose());
        footerPanel.add(closeBtn, BorderLayout.EAST);

        rootPanel.add(footerPanel, BorderLayout.SOUTH);

        // Selection Listener
        topicList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = topicList.getSelectedValue();
                if (selected != null && topicPanelMap.containsKey(selected)) {
                    displayTopic(selected);
                }
            }
        });

        getContentPane().add(rootPanel);

        // Select first topic by default
        topicList.setSelectedIndex(0);

        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(260));
    }

    public void selectTopic(int index) {
        if (index >= 0 && index < topicList.getModel().getSize()) {
            topicList.setSelectedIndex(index);
        }
    }

    private void displayTopic(String topicKey) {
        detailContainer.removeAll();
        JPanel topicPanel = topicPanelMap.get(topicKey);
        if (topicPanel != null) {
            detailContainer.add(topicPanel, BorderLayout.CENTER);
        }
        detailContainer.revalidate();
        detailContainer.repaint();
        detailScrollPane.getVerticalScrollBar().setValue(0);
    }

    // ========================================================================
    // CONTENT BUILDERS FOR ALL 7 OOP TOPICS
    // ========================================================================

    private void buildAllTopicPanels() {
        topicPanelMap.put("1. Encapsulation", buildEncapsulationPanel());
        topicPanelMap.put("2. Inheritance", buildInheritancePanel());
        topicPanelMap.put("3. Polymorphism", buildPolymorphismPanel());
        topicPanelMap.put("4. Abstraction & Interfaces", buildAbstractionPanel());
        topicPanelMap.put("5. Composition vs Aggregation", buildCompositionPanel());
        topicPanelMap.put("6. Design Patterns Catalog", buildDesignPatternsPanel());
        topicPanelMap.put("7. File I/O & Exceptions", buildIoExceptionsPanel());
    }

    private static class ScrollablePanel extends JPanel implements javax.swing.Scrollable {
        public ScrollablePanel() {
            super();
        }
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }
        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }
        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 64;
        }
        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }
        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private JPanel createBaseContentContainer(String title, String categoryBadge) {
        ScrollablePanel panel = new ScrollablePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 20, 20));

        // Header Block (Vertical Stack prevents any horizontal collision)
        JPanel headerBlock = new JPanel();
        headerBlock.setLayout(new BoxLayout(headerBlock, BoxLayout.Y_AXIS));
        headerBlock.setOpaque(false);
        headerBlock.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLbl.setForeground(Theme.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerBlock.add(titleLbl);
        headerBlock.add(Box.createVerticalStrut(5));

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel badgeLbl = new JLabel(" " + categoryBadge.toUpperCase() + " ");
        badgeLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeLbl.setForeground(Theme.ACCENT_BLUE);
        badgeLbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(59, 130, 246, 140), 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        badgeRow.add(badgeLbl);
        headerBlock.add(badgeRow);

        panel.add(headerBlock);
        panel.add(Box.createVerticalStrut(14));
        return panel;
    }

    private JPanel createCard(String cardTitle, Color accentColor, String bodyText) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Theme.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.BORDER_SUBTLE, 1),
                        BorderFactory.createEmptyBorder(12, 16, 14, 16)
                )
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(32767, 2000));

        if (cardTitle != null) {
            JLabel titleLbl = new JLabel(cardTitle);
            titleLbl.setFont(Theme.FONT_HEADER);
            titleLbl.setForeground(accentColor);
            card.add(titleLbl, BorderLayout.NORTH);
        }

        JTextArea textArea = new JTextArea(bodyText) {
            @Override
            public Dimension getPreferredSize() {
                int w = getWidth();
                if (w <= 0) {
                    Container parent = getParent();
                    if (parent != null && parent.getWidth() > 40) {
                        w = parent.getWidth() - 40;
                    } else {
                        w = 460;
                    }
                }
                setSize(new Dimension(w, 10000));
                Dimension d = getUI().getPreferredSize(this);
                return new Dimension(w, d.height + 10);
            }
        };
        textArea.setFont(Theme.FONT_BODY);
        textArea.setForeground(Theme.TEXT_PRIMARY);
        textArea.setBackground(Theme.BG_PANEL);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new java.awt.Insets(0, 0, 0, 6));
        card.add(textArea, BorderLayout.CENTER);

        return card;
    }

    private JPanel createCodeCard(String cardTitle, String codeSnippet) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Theme.BG_INPUT);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(10, 14, 12, 14)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(32767, 500));

        if (cardTitle != null) {
            JLabel titleLbl = new JLabel(cardTitle);
            titleLbl.setFont(Theme.FONT_BODY_BOLD);
            titleLbl.setForeground(Theme.TEXT_MUTED);
            card.add(titleLbl, BorderLayout.NORTH);
        }

        JTextArea codeArea = new JTextArea(codeSnippet);
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        codeArea.setForeground(new Color(147, 197, 253)); // Soft readable blue
        codeArea.setBackground(Theme.BG_INPUT);
        codeArea.setEditable(false);
        codeArea.setLineWrap(false);
        codeArea.setMargin(new java.awt.Insets(2, 4, 4, 6));
        card.add(codeArea, BorderLayout.CENTER);

        return card;
    }

    private static String escapeAndFormat(String plainText) {
        if (plainText == null) return "";
        return plainText
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n\n", "<br/><br/>")
                .replace("\n", "<br/>")
                .replace("•", "&bull;")
                .replaceAll("([A-Za-z0-9_]+\\.java)", "<b>$1</b>");
    }

    // 1. Encapsulation Panel
    private JPanel buildEncapsulationPanel() {
        JPanel p = createBaseContentContainer("1. Encapsulation & Information Hiding", "Core OOP Principle");

        p.add(createCard(
                "Core Principle",
                Theme.ACCENT_BLUE,
                "Encapsulation is bundling internal state (data fields) and domain behavior (methods) together within a single class, " +
                "while restricting direct outside access using the private access modifier. This protects objects from illegal state corruption " +
                "and ensures all mutations pass through validated business rules."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCard(
                "Where It Is Applied in EscapeX",
                Theme.EMERALD,
                "• Player.java: Fields like score and hintsUsedTotal are private. Methods like addScore(points) and deductScore(penalty) " +
                "enforce business invariants so the score can never drop below zero.\n\n" +
                "• Item.java (Immutable Value Object): All fields are declared final, with public getters and zero setters. " +
                "Once an item is instantiated, its identity, lore, and clues cannot be mutated by outside code.\n\n" +
                "• Inventory.java (Defensive Copying): The internal ArrayList<Item> is strictly private. When external code calls getItems(), " +
                "it receives Collections.unmodifiableList(items), preventing external callers from bypassing capacity checks."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCodeCard(
                "Defensive Copying in Inventory.java",
                "// Returns an unmodifiable view to protect the internal collection\n" +
                "public List<Item> getItems() {\n" +
                "    return Collections.unmodifiableList(this.items);\n" +
                "}"
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCard(
                "Why AIML Students Need This",
                Theme.AMBER,
                "In AI libraries like PyTorch and HuggingFace, model parameters and layer tensors are encapsulated. " +
                "You interact with deep networks via the forward() method, preventing external scripts from corrupting gradients or weights during backpropagation."
        ));
        return p;
    }

    // 2. Inheritance Panel
    private JPanel buildInheritancePanel() {
        JPanel p = createBaseContentContainer("2. Inheritance & Class Hierarchies", "Is-A Relationships");

        p.add(createCard(
                "Core Principle",
                Theme.ACCENT_BLUE,
                "Inheritance allows a specialized derived class to inherit fields and methods from a superclass using extends. " +
                "This promotes code reuse, avoids duplicate logic, and formalizes an 'Is-A' relationship in software design."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCard(
                "Where It Is Applied in EscapeX",
                Theme.EMERALD,
                "• Puzzle Hierarchy: All 15 puzzles inherit shared attributes (id, title, difficulty, hints, attempts) " +
                "from the abstract Puzzle superclass:\n" +
                "  - CodeInputPuzzle: Direct alphanumeric code entry.\n" +
                "  - ChoicePuzzle: Multiple-choice dilemmas (options A, B, C, D).\n" +
                "  - PatternPuzzle: Sequence and matrix transposition matching.\n" +
                "  - ItemCombinationPuzzle: Synergistic puzzles requiring an inventory tool.\n\n" +
                "• Custom Exceptions: RoomLockedException and GameSaveException inherit from EscapeXException.\n\n" +
                "• GUI Components: SciFiButton extends JButton, inheriting mouse listeners while overriding paintComponent()."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCodeCard(
                "Class Hierarchy Diagram",
                "Puzzle (Abstract Superclass)\n" +
                "  ├── CodeInputPuzzle\n" +
                "  ├── ChoicePuzzle\n" +
                "  ├── PatternPuzzle\n" +
                "  └── ItemCombinationPuzzle"
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCard(
                "Why AIML Students Need This",
                Theme.AMBER,
                "In PyTorch, every neural network architecture is written as a subclass inheriting from nn.Module " +
                "(class Transformer(nn.Module):), calling super().__init__() to inherit GPU device allocation, parameter tracking, and hooks."
        ));
        return p;
    }

    // 3. Polymorphism Panel
    private JPanel buildPolymorphismPanel() {
        JPanel p = createBaseContentContainer("3. Polymorphism & Dynamic Dispatch", "Dynamic Binding");

        p.add(createCard(
                "Core Principle",
                Theme.ACCENT_BLUE,
                "Polymorphism allows objects of different concrete subclasses to be referenced through a uniform superclass type. " +
                "At runtime, the Java Virtual Machine (JVM) dynamically dispatches calls to the specific subclass implementation."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCard(
                "Where It Is Applied in EscapeX",
                Theme.EMERALD,
                "• Uniform Puzzle Verification: In GameEngine.java, all 15 puzzles across all 5 sectors are stored inside a single " +
                "collection: List<Puzzle>.\n" +
                "When the player submits an answer, GameEngine executes:\n" +
                "    boolean solved = puzzle.attemptSolution(input);\n" +
                "The JVM dynamically invokes the correct checkAnswer() implementation whether it is a Caesar cipher, matrix transposition, or perceptron math!\n\n" +
                "• Dynamic GUI Rendering: Swing's rendering system calls paintComponent(Graphics g) polymorphically across all panels and buttons."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCodeCard(
                "Polymorphic Execution in GameEngine.java",
                "// Handled identically regardless of concrete puzzle subtype!\n" +
                "for (Puzzle puzzle : currentRoom.getPuzzles()) {\n" +
                "    if (puzzle.isSolved()) {\n" +
                "        score += puzzle.calculateScore();\n" +
                "    }\n" +
                "}"
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCard(
                "Why AIML Students Need This",
                Theme.AMBER,
                "In Scikit-Learn, all models implement uniform fit(X, y) and predict(X) methods. A machine learning pipeline " +
                "can evaluate LogisticRegression, RandomForestClassifier, or XGBoost polymorphically without changing a single line of testing code."
        ));
        return p;
    }

    // 4. Abstraction Panel
    private JPanel buildAbstractionPanel() {
        JPanel p = createBaseContentContainer("4. Abstraction & Interface Contracts", "Separation of Concerns");

        p.add(createCard(
                "Core Principle",
                Theme.ACCENT_BLUE,
                "Abstraction hides complex implementation mechanics and exposes only clear, essential contracts. " +
                "Abstract classes provide shared partial implementations, while interfaces define pure behavioral contracts."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCard(
                "Where It Is Applied in EscapeX",
                Theme.EMERALD,
                "• Abstract Class Puzzle.java: Declares the abstract method protected abstract boolean checkAnswer(String input). " +
                "It implements the Template Method Pattern: attemptSolution() manages attempt counts, hints, and solved flags, " +
                "delegating the verification algorithm to subclasses.\n\n" +
                "• Interface Hintable.java: Demonstrates the Interface Segregation Principle (SOLID). Only entities that support layered hints " +
                "implement hasMoreHints(), getNextHint(), and getHintsRevealedCount().\n\n" +
                "• Interface GameEventListener.java: Defines the Observer contract: void onGameEvent(GameEvent event). " +
                "Decouples the core game engine completely from the Swing GUI!"
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCodeCard(
                "Template Method Pattern in Puzzle.java",
                "public final boolean attemptSolution(String input) {\n" +
                "    attemptsCount++;\n" +
                "    boolean correct = checkAnswer(input); // Abstract call!\n" +
                "    if (correct) this.solved = true;\n" +
                "    return correct;\n" +
                "}"
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCard(
                "Why AIML Students Need This",
                Theme.AMBER,
                "Reinforcement Learning frameworks like Gymnasium (OpenAI Gym) define abstract methods reset() and step(action). " +
                "Reinforcement learning agents (like PPO or DQN) train purely against this abstraction, regardless of whether the environment is a robot arm, a chess game, or an autonomous vehicle."
        ));
        return p;
    }

    // 5. Composition Panel
    private JPanel buildCompositionPanel() {
        JPanel p = createBaseContentContainer("5. Composition vs Aggregation", "Has-A Relationships");

        p.add(createCard(
                "Core Principle",
                Theme.ACCENT_BLUE,
                "'Favor object composition over class inheritance.' Instead of forcing classes into deep, rigid inheritance trees, " +
                "classes should contain references to other objects and delegate tasks to them."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCard(
                "Where It Is Applied in EscapeX",
                Theme.EMERALD,
                "• Room.java (Composition): A Room HAS-A collection of Puzzle objects and HAS-A collection of RoomObject hotspots. " +
                "The Room does not inherit from Puzzle or Item; it delegates door unlock checks to its composed puzzles.\n\n" +
                "• Inventory.java (Aggregation): An Inventory HAS-A collection of Item objects. Items exist independently as standalone " +
                "domain assets, while Inventory coordinates capacity and retrieval.\n\n" +
                "• GameEngine.java (Facade Composition): Composes Player, Inventory, Rooms, GameTimer, and SoundEngine into a cohesive system."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCodeCard(
                "Composition & Delegation in Room.java",
                "public boolean areAllPuzzlesSolved() {\n" +
                "    for (Puzzle p : this.puzzles) {\n" +
                "        if (!p.isSolved()) return false;\n" +
                "    }\n" +
                "    return true;\n" +
                "}"
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCard(
                "Why AIML Students Need This",
                Theme.AMBER,
                "A computer vision pipeline HAS-A preprocessor, HAS-A convolutional feature extractor, and HAS-A softmax classifier. " +
                "It doesn't inherit from them—it composes them in a modular, pluggable pipeline."
        ));
        return p;
    }

    // 6. Design Patterns Panel
    private JPanel buildDesignPatternsPanel() {
        JPanel p = createBaseContentContainer("6. Design Patterns Catalog", "Architectural Patterns");

        p.add(createCard(
                "Overview",
                Theme.ACCENT_BLUE,
                "Design patterns represent proven, industry-standard architectural solutions to recurring software engineering challenges. " +
                "EscapeX implements 5 classic GoF (Gang of Four) patterns:"
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCard(
                "Patterns Applied in EscapeX",
                Theme.EMERALD,
                "• 1. Model-View-Controller (MVC):\n" +
                "  - Model: Room, Puzzle, Player, Inventory (Domain state)\n" +
                "  - View: EscapeXFrame, RoomViewPanel, InventoryPanel, TerminalPanel (GUI)\n" +
                "  - Controller: GameEngine (Coordinates state changes and updates views)\n\n" +
                "• 2. Observer Pattern:\n" +
                "  GameEngine dispatches GameEvent notifications. Observers implementing GameEventListener react automatically without tight coupling.\n\n" +
                "• 3. Factory Pattern:\n" +
                "  RoomFactory encapsulates the complex construction of all 5 sectors, 15 puzzles, clues, and items.\n\n" +
                "• 4. Singleton Pattern:\n" +
                "  SoundEngine ensures exactly one centralized coordinator manages audio hardware safely.\n\n" +
                "• 5. Memento Pattern:\n" +
                "  GameState bundles the entire active simulation snapshot for save/load serialization."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCodeCard(
                "Observer Pattern Subscription in EscapeX",
                "// GameEngine is completely decoupled from Swing GUI!\n" +
                "gameEngine.addListener(terminalPanel);\n" +
                "gameEngine.addListener(inventoryPanel);\n" +
                "gameEngine.addListener(escapeXFrame);"
        ));
        return p;
    }

    // 7. File IO Panel
    private JPanel buildIoExceptionsPanel() {
        JPanel p = createBaseContentContainer("7. File I/O & Exception Handling", "Persistence & Robustness");

        p.add(createCard(
                "Core Principle",
                Theme.ACCENT_BLUE,
                "Writing robust software requires defensive resource management, safe state persistence, " +
                "and translating low-level operating system errors into meaningful domain exceptions."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCard(
                "Where It Is Applied in EscapeX",
                Theme.EMERALD,
                "• Java Object Serialization: GameState, Player, Inventory, and Room implement Serializable. " +
                "SaveLoadService writes the complete in-memory object graph to disk with ObjectOutputStream and restores it on demand.\n\n" +
                "• Try-With-Resources: HighScoreService uses try (BufferedReader reader = new BufferedReader(...)) " +
                "to guarantee operating system file descriptors are automatically closed, preventing resource leaks.\n\n" +
                "• Exception Translation: Low-level IOException is caught inside SaveLoadService and re-thrown as GameSaveException, " +
                "shielding the UI layer from low-level storage details.\n\n" +
                "• Custom Domain Exceptions: RoomLockedException informs the user interface when a player attempts to exit an uncleared sector."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createCodeCard(
                "Safe Resource Management in HighScoreService.java",
                "// Guaranteed to close streams automatically, preventing memory leaks!\n" +
                "try (FileReader fr = new FileReader(file);\n" +
                "     BufferedReader reader = new BufferedReader(fr)) {\n" +
                "    String line;\n" +
                "    while ((line = reader.readLine()) != null) {\n" +
                "        // Parse leaderboard records safely\n" +
                "    }\n" +
                "}"
        ));
        return p;
    }
}
