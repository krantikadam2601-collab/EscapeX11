package escapex.ui;

import escapex.model.Room;
import escapex.model.puzzle.Puzzle;
import escapex.observer.GameEvent;
import escapex.observer.GameEventListener;
import escapex.service.GameEngine;
import escapex.ui.components.GlowPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: OBSERVER PATTERN & COMPOSITE VIEW (Dynamic Objectives)
 * ============================================================================
 * Why this matters in OOP:
 * `MissionObjectivesPanel` observes the `GameEngine` and dynamically updates
 * the current sector's 3 primary mission objectives:
 *   1. Fixture investigation / tool harvesting
 *   2. Blast door deadbolt puzzle clearing (Easy, Med, Hard)
 *   3. Door unsealed and sector evacuation
 *
 * Demonstrates high cohesion: encapsulated objective state without polluting
 * the core game engine.
 * ============================================================================
 */
public class MissionObjectivesPanel extends GlowPanel implements GameEventListener {

    private final GameEngine gameEngine;

    private final JLabel obj1Label;
    private final JLabel obj2Label;
    private final JLabel obj3Label;

    public MissionObjectivesPanel(GameEngine gameEngine) {
        super(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 8);
        this.gameEngine = gameEngine;

        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        setPreferredSize(new Dimension(380, 104));

        JLabel header = new JLabel("Sector Directives");
        header.setFont(Theme.FONT_HEADER);
        header.setForeground(Theme.CYAN_NEON);
        add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        listPanel.setOpaque(false);

        obj1Label = new JLabel("1. Fixtures: Checking...");
        obj1Label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        listPanel.add(obj1Label);

        obj2Label = new JLabel("2. Locks: Checking...");
        obj2Label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        listPanel.add(obj2Label);

        obj3Label = new JLabel("3. Evacuate: Checking...");
        obj3Label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        listPanel.add(obj3Label);

        add(listPanel, BorderLayout.CENTER);

        updateObjectives();
    }

    public void updateObjectives() {
        Room room = gameEngine.getCurrentRoom();
        if (room == null) return;

        // 1. Fixture investigation progress
        long harvestedCount = room.getRoomObjects().stream()
                .filter(obj -> !"DOOR".equalsIgnoreCase(obj.getIconType()) && !"AIRLOCK".equalsIgnoreCase(obj.getIconType()))
                .filter(obj -> obj.isItemHarvested())
                .count();
        long totalFixtures = room.getRoomObjects().stream()
                .filter(obj -> !"DOOR".equalsIgnoreCase(obj.getIconType()) && !"AIRLOCK".equalsIgnoreCase(obj.getIconType()))
                .count();

        boolean allFixturesHarvested = (harvestedCount == totalFixtures && totalFixtures > 0);
        if (allFixturesHarvested) {
            obj1Label.setText("✔ 1. Investigate fixtures (" + harvestedCount + "/" + totalFixtures + " tools acquired)");
            obj1Label.setForeground(Theme.EMERALD);
        } else {
            obj1Label.setText("◻ 1. Investigate fixtures (" + harvestedCount + "/" + totalFixtures + " tools acquired)");
            obj1Label.setForeground(Theme.TEXT_MUTED);
        }

        // 2. Puzzle lock status
        List<Puzzle> puzzles = room.getPuzzles();
        boolean p1Solved = puzzles.size() > 0 && puzzles.get(0).isSolved();
        boolean p2Solved = puzzles.size() > 1 && puzzles.get(1).isSolved();
        boolean p3Solved = puzzles.size() > 2 && puzzles.get(2).isSolved();
        int solvedCount = room.getSolvedPuzzlesCount();

        StringBuilder pzStr = new StringBuilder();
        if (solvedCount == 3) {
            pzStr.append("✔ 2. Override 3 locks: Easy [✔]  Med [✔]  Hard [✔]");
            obj2Label.setForeground(Theme.EMERALD);
        } else {
            pzStr.append("◻ 2. Override 3 locks: ");
            pzStr.append("Easy [").append(p1Solved ? "✔" : " ").append("]  ");
            pzStr.append("Med [").append(p2Solved ? "✔" : " ").append("]  ");
            pzStr.append("Hard [").append(p3Solved ? "✔" : " ").append("]");
            obj2Label.setForeground(solvedCount > 0 ? Theme.AMBER : Theme.TEXT_MUTED);
        }
        obj2Label.setText(pzStr.toString());

        // 3. Blast door status
        if (room.isDoorUnlocked()) {
            obj3Label.setText("✔ 3. Blast Door: UNLOCKED! Advance to Sector 0" + (room.getRoomNumber() < 5 ? (room.getRoomNumber() + 1) : "5 Escape"));
            obj3Label.setForeground(Theme.EMERALD);
        } else {
            obj3Label.setText("◻ 3. Blast Door: Sealed (" + solvedCount + "/3 deadbolts retracted)");
            obj3Label.setForeground(Theme.TEXT_DIM);
        }
    }

    @Override
    public void onGameEvent(GameEvent event) {
        SwingUtilities.invokeLater(this::updateObjectives);
    }
}
