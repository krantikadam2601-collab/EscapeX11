package escapex.ui;

import escapex.ui.components.GlowPanel;
import escapex.ui.components.SciFiButton;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: MODULAR CARD-BASED DOCUMENTATION VIEWER
 * ============================================================================
 * Why this matters in OOP:
 * `HowToPlayDialog` encapsulates the game instruction manual.
 *
 * It uses a `CardLayout` to organize detailed game mechanics into 4 distinct,
 * cohesive tabs:
 *   1. Controls & Directives
 *   2. Inventory System
 *   3. Puzzles & Sectors
 *   4. Scoring & Difficulties
 *
 * Demonstrates high cohesion and clean separation between documentation
 * presentation and runtime game state.
 * ============================================================================
 */
public class HowToPlayDialog extends JDialog {

    private final CardLayout cardLayout;
    private final JPanel contentCards;
    private SciFiButton tab1Btn;
    private SciFiButton tab2Btn;
    private SciFiButton tab3Btn;
    private SciFiButton tab4Btn;

    public HowToPlayDialog(Frame owner) {
        super(owner, "EscapeX Operative Field Manual - How to Play", true);
        setSize(720, 600);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_DARK);

        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setOpaque(false);

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new GlowPanel(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 12);
        rootPanel.setLayout(new BorderLayout(10, 10));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // 1. TOP HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("OPERATIVE FIELD MANUAL // HOW TO PLAY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 20));
        titleLabel.setForeground(Theme.CYAN_NEON);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Standard Operating Procedures for Quarantined AI Containment Facilities", SwingConstants.CENTER);
        subtitleLabel.setFont(Theme.FONT_BODY);
        subtitleLabel.setForeground(Theme.TEXT_MUTED);
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        headerPanel.add(subtitleLabel);

        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. TAB SWITCHER BAR
        JPanel tabButtonBar = new JPanel(new GridLayout(1, 4, 8, 0));
        tabButtonBar.setOpaque(false);
        tabButtonBar.setPreferredSize(new Dimension(680, 32));

        tab1Btn = new SciFiButton("1. Controls", SciFiButton.ButtonStyle.PRIMARY);
        tab2Btn = new SciFiButton("2. Inventory", SciFiButton.ButtonStyle.SECONDARY);
        tab3Btn = new SciFiButton("3. Puzzles", SciFiButton.ButtonStyle.SECONDARY);
        tab4Btn = new SciFiButton("4. Scoring", SciFiButton.ButtonStyle.SECONDARY);

        tab1Btn.addActionListener(e -> switchTab("TAB_CONTROLS", tab1Btn));
        tab2Btn.addActionListener(e -> switchTab("TAB_INVENTORY", tab2Btn));
        tab3Btn.addActionListener(e -> switchTab("TAB_PUZZLES", tab3Btn));
        tab4Btn.addActionListener(e -> switchTab("TAB_SCORING", tab4Btn));

        tabButtonBar.add(tab1Btn);
        tabButtonBar.add(tab2Btn);
        tabButtonBar.add(tab3Btn);
        tabButtonBar.add(tab4Btn);

        // 3. CENTER CARDS
        contentCards.add(buildControlsCard(), "TAB_CONTROLS");
        contentCards.add(buildInventoryCard(), "TAB_INVENTORY");
        contentCards.add(buildPuzzlesCard(), "TAB_PUZZLES");
        contentCards.add(buildScoringCard(), "TAB_SCORING");

        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setOpaque(false);
        centerPanel.add(tabButtonBar, BorderLayout.NORTH);
        centerPanel.add(contentCards, BorderLayout.CENTER);

        rootPanel.add(centerPanel, BorderLayout.CENTER);

        // 4. BOTTOM ACTION BAR
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomBar.setOpaque(false);

        SciFiButton closeBtn = new SciFiButton("Close Field Manual", SciFiButton.ButtonStyle.PRIMARY);
        closeBtn.setPreferredSize(new Dimension(180, 32));
        closeBtn.addActionListener(e -> dispose());
        bottomBar.add(closeBtn);

        rootPanel.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    private void switchTab(String cardName, SciFiButton activeBtn) {
        cardLayout.show(contentCards, cardName);
        tab1Btn.setStyle(activeBtn == tab1Btn ? SciFiButton.ButtonStyle.PRIMARY : SciFiButton.ButtonStyle.SECONDARY);
        tab2Btn.setStyle(activeBtn == tab2Btn ? SciFiButton.ButtonStyle.PRIMARY : SciFiButton.ButtonStyle.SECONDARY);
        tab3Btn.setStyle(activeBtn == tab3Btn ? SciFiButton.ButtonStyle.PRIMARY : SciFiButton.ButtonStyle.SECONDARY);
        tab4Btn.setStyle(activeBtn == tab4Btn ? SciFiButton.ButtonStyle.PRIMARY : SciFiButton.ButtonStyle.SECONDARY);
    }

    private JScrollPane buildControlsCard() {
        String html = "<html><body style='font-family:Segoe UI, sans-serif; font-size:11px; color:#F3F4F6; background-color:#121316; padding:10px;'>"
                + "<h2 style='color:#3B82F6; margin-top:0;'>MISSION OVERVIEW & CONTROLS</h2>"
                + "<p>An automated quarantine lockdown has isolated all 5 research sectors of the facility. "
                + "You must investigate chamber fixtures, harvest diagnostic tools, and decode 3 security deadbolts per sector to escape.</p>"
                + "<h3 style='color:#F59E0B;'>MOUSE INTERACTIONS</h3>"
                + "<ul>"
                + "<li><b>Explore Stations:</b> Click directly on 2D room fixtures (Workbenches, Consoles, Terminals) or use the <i>'Explore Fixtures'</i> button to inspect clues and discover physical tools.</li>"
                + "<li><b>Engage Locks:</b> Click <b>'Easy Puzzle'</b>, <b>'Medium Puzzle'</b>, or <b>'Hard Puzzle'</b> in the right sidebar (or <i>'Solve Puzzles'</i>) to open the security terminal.</li>"
                + "<li><b>Advance to Next Sector:</b> Once all 3 lock bolts retract (turning green), click the central <b>Security Blast Door</b> or click <i>'Advance to Next Room →'</i>.</li>"
                + "<li><b>Inspect Inventory:</b> Click any item in your inventory rack to launch the Hardware Diagnostic Analyzer.</li>"
                + "</ul>"
                + "<h3 style='color:#22C55E;'>KEYBOARD SHORTCUTS</h3>"
                + "<ul>"
                + "<li><b>[Esc] or [P]:</b> Pause simulation (stops countdown clock and freezes gameplay)</li>"
                + "<li><b>[S]:</b> Save simulation checkpoint to disk (<code>escapex_savegame.dat</code>)</li>"
                + "<li><b>[L]:</b> Load simulation checkpoint from disk</li>"
                + "<li><b>[H]:</b> Open this How to Play field manual</li>"
                + "<li><b>[O]:</b> Open interactive Object-Oriented Architecture Guide</li>"
                + "</ul>"
                + "</body></html>";
        return createHtmlScrollPane(html);
    }

    private JScrollPane buildInventoryCard() {
        String html = "<html><body style='font-family:Segoe UI, sans-serif; font-size:11px; color:#F3F4F6; background-color:#121316; padding:10px;'>"
                + "<h2 style='color:#3B82F6; margin-top:0;'>INVENTORY SYSTEM & TOOL SYNERGY</h2>"
                + "<p>Your inventory stores physical tools, keys, and cryptographic media discovered during chamber exploration.</p>"
                + "<h3 style='color:#F59E0B;'>PAGINATED STORAGE RACK</h3>"
                + "<ul>"
                + "<li><b>Capacity:</b> Holds up to <b>16 items</b> across 2 pages.</li>"
                + "<li><b>Display:</b> Displays 8 slots in a clean 2x4 visual rack. Use <code>[&lt; Prev]</code> and <code>[Next &gt;]</code> buttons to switch pages.</li>"
                + "<li>Auto-pagination flips to page 2 when you collect your 9th item.</li>"
                + "</ul>"
                + "<h3 style='color:#22C55E;'>HARDWARE DIAGNOSTIC ANALYZER</h3>"
                + "<ul>"
                + "<li>Click any item slot in the inventory to open the <b>Diagnostic Analyzer</b>.</li>"
                + "<li>Displays physical item descriptions, technical lore, and critical hidden clues:</li>"
                + "  <ul>"
                + "    <li>Laser frequencies (e.g. <code>4096</code> on the Optical Magnifier)</li>"
                + "    <li>Cipher keys and matrix dimensions</li>"
                + "    <li>Synaptic weight matrices and quantum entanglement states</li>"
                + "  </ul>"
                + "</ul>"
                + "<h3 style='color:#8B5CF6;'>TOOL SYNERGY MECHANICS</h3>"
                + "<p>Level 3 (Hard) puzzles feature <b>Item-Combination synergy</b>. You cannot solve them with guesswork alone; "
                + "you must have the corresponding hardware tool physically present in your inventory rack before submitting the solution code!</p>"
                + "</body></html>";
        return createHtmlScrollPane(html);
    }

    private JScrollPane buildPuzzlesCard() {
        String html = "<html><body style='font-family:Segoe UI, sans-serif; font-size:11px; color:#F3F4F6; background-color:#121316; padding:10px;'>"
                + "<h2 style='color:#3B82F6; margin-top:0;'>15 DIAGNOSTIC PUZZLES & 5 SECTORS</h2>"
                + "<p>Every sector is protected by 3 diagnostic puzzles ranging from novice to expert.</p>"
                + "<h3 style='color:#F59E0B;'>THE 5 RESEARCH SECTORS</h3>"
                + "<ol>"
                + "<li><b>Sector 01: Cybernetics Workshop:</b> Boolean logic gates (NAND/NOR), binary ASCII decoding, and optical frequency calibration.</li>"
                + "<li><b>Sector 02: Cryptographic Vault:</b> Caesar rotational ciphers, matrix columnar transposition, and optical mirror angles.</li>"
                + "<li><b>Sector 03: Neural Core:</b> Single-layer perceptron threshold calculations, ReLU activation functions, and synaptic weight convergence.</li>"
                + "<li><b>Sector 04: Quantum Nexus:</b> Pauli-X qubit operations, Bell state entanglement spin, and quantum superposition angles.</li>"
                + "<li><b>Sector 05: Singularity Mainframe:</b> AI alignment principles, prompt injection defense mitigation, and master protocol handshake.</li>"
                + "</ol>"
                + "<h3 style='color:#22C55E;'>DIAGNOSTIC HINTS</h3>"
                + "<p>If you encounter a challenging lock, click the <b>'Request Hint'</b> button inside the puzzle terminal. "
                + "Each puzzle features multi-tiered hint progression. Hints deduct <b>15 points</b> from your score.</p>"
                + "</body></html>";
        return createHtmlScrollPane(html);
    }

    private JScrollPane buildScoringCard() {
        String html = "<html><body style='font-family:Segoe UI, sans-serif; font-size:11px; color:#F3F4F6; background-color:#121316; padding:10px;'>"
                + "<h2 style='color:#3B82F6; margin-top:0;'>SCORING SYSTEM & DIFFICULTY MODES</h2>"
                + "<h3 style='color:#F59E0B;'>DIFFICULTY PRESETS</h3>"
                + "<ul>"
                + "<li><b>Easy (45 Minutes):</b> Extended containment window designed for training operatives.</li>"
                + "<li><b>Normal (30 Minutes):</b> Standard automated quarantine containment protocol.</li>"
                + "<li><b>Hard (20 Minutes):</b> High-stress rapid lockdown emergency scenario.</li>"
                + "</ul>"
                + "<h3 style='color:#22C55E;'>SCORE REWARDS</h3>"
                + "<ul>"
                + "<li><b>Easy Puzzle:</b> +100 points &amp; +30 seconds clock bonus</li>"
                + "<li><b>Medium Puzzle:</b> +250 points &amp; +45 seconds clock bonus</li>"
                + "<li><b>Hard Puzzle:</b> +500 points &amp; +60 seconds clock bonus</li>"
                + "<li><b>Object Exploration:</b> +50 points per item harvested</li>"
                + "<li><b>Sector Clearance:</b> +200 points upon unlocking blast door</li>"
                + "<li><b>Victory Time Bonus:</b> +2 points per remaining second when escaping Sector 05!</li>"
                + "</ul>"
                + "<h3 style='color:#EF4444;'>PENALTIES</h3>"
                + "<ul>"
                + "<li><b>Hint Request:</b> -15 points per hint used</li>"
                + "<li><b>Incorrect Attempts:</b> Decreases the attempt multiplier for that puzzle</li>"
                + "</ul>"
                + "<h3 style='color:#8B5CF6;'>PERSISTENT HALL OF FAME</h3>"
                + "<p>Top 20 escape runs are permanently recorded in <code>escapex_scores.csv</code> and viewable in the Hall of Fame.</p>"
                + "</body></html>";
        return createHtmlScrollPane(html);
    }

    private JScrollPane createHtmlScrollPane(String html) {
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(html);
        textPane.setEditable(false);
        textPane.setCaretPosition(0);
        textPane.setBackground(Theme.BG_INPUT);

        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }
}
