package escapex.ui;

import escapex.exception.RoomLockedException;
import escapex.model.Room;
import escapex.model.RoomObject;
import escapex.model.puzzle.Puzzle;
import escapex.service.GameEngine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: 2D GRAPHICS POLYMORPHISM & EVENT-DRIVEN UI
 * ============================================================================
 * Why this matters in OOP:
 * `RoomViewPanel` extends {@link JPanel} and renders the interactive escape room chamber.
 *
 * Designed with a clean, authentic escape room aesthetic:
 *   - Atmospheric quarantined chamber layout
 *   - Physical security blast door with 3 visible lock status bolts
 *   - Distinct investigation stations for inspecting clues and collecting tools
 *   - Interactive mouse hover feedback with investigation hints
 * ============================================================================
 */
public class RoomViewPanel extends JPanel {

    private final GameEngine gameEngine;
    private RoomObject hoveredObject = null;
    private boolean doorHovered = false;

    public RoomViewPanel(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        setOpaque(true);
        setBackground(Theme.BG_DARK);

        // Mouse motion handling for hovering over hotspots and door
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e.getX(), e.getY());
            }
        });

        // Mouse click handling for interaction
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoveredObject = null;
                doorHovered = false;
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        });
    }

    private void handleMouseMove(int mouseX, int mouseY) {
        Room room = gameEngine.getCurrentRoom();
        int width = getWidth();
        int height = getHeight();

        // Check if Blast Door was hovered (top center area of the chamber)
        int doorW = Math.min(380, width - 80);
        int doorH = 68;
        int doorX = (width - doorW) / 2;
        int doorY = 54;

        boolean wasDoorHovered = doorHovered;
        doorHovered = (mouseX >= doorX && mouseX <= doorX + doorW && mouseY >= doorY && mouseY <= doorY + doorH);

        RoomObject prevHovered = hoveredObject;
        hoveredObject = null;

        if (!doorHovered) {
            int cardW = 175;
            int cardH = 72;

            for (RoomObject obj : room.getRoomObjects()) {
                if ("DOOR".equalsIgnoreCase(obj.getIconType()) || "AIRLOCK".equalsIgnoreCase(obj.getIconType())) {
                    continue;
                }
                int cx = (int)(obj.getRelativeX() * width);
                int cy = (int)(obj.getRelativeY() * height);

                // Ensure stations stay within chamber bounds
                cx = Math.max(cardW / 2 + 30, Math.min(width - cardW / 2 - 30, cx));
                cy = Math.max(160 + cardH / 2, Math.min(height - cardH / 2 - 40, cy));

                int cardX = cx - cardW / 2;
                int cardY = cy - cardH / 2;

                if (mouseX >= cardX && mouseX <= cardX + cardW && mouseY >= cardY && mouseY <= cardY + cardH) {
                    hoveredObject = obj;
                    break;
                }
            }
        }

        if (doorHovered || hoveredObject != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }

        if (wasDoorHovered != doorHovered || prevHovered != hoveredObject) {
            repaint();
        }
    }

    private void handleMouseClick(int mouseX, int mouseY) {
        if (gameEngine.isGameOver() || gameEngine.isGameWon()) return;

        if (doorHovered) {
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
            repaint();
            return;
        }

        if (hoveredObject != null) {
            String discovery = gameEngine.exploreObject(hoveredObject.getId());
            repaint();
            JOptionPane.showMessageDialog(
                    this,
                    discovery,
                    hoveredObject.getName(),
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        Room room = gameEngine.getCurrentRoom();

        // 1. Draw Quarantined Chamber Environment
        drawChamberEnvironment(g2, w, h, room);

        // 2. Draw Heavy Security Blast Door with 3 Bolt Locks
        drawSecurityBlastDoor(g2, w, h, room);

        // 3. Draw Investigation Stations (Hotspots)
        drawInvestigationStations(g2, w, h, room);

        // 4. Draw Bottom Investigation Prompt / Tip Bar
        drawInvestigationPrompt(g2, w, h);

        g2.dispose();
    }

    private void drawChamberEnvironment(Graphics2D g2, int w, int h, Room room) {
        // Deep atmospheric slate backdrop
        g2.setColor(new Color(18, 20, 24));
        g2.fillRect(0, 0, w, h);

        // Subtle floor grid tiles (40px)
        g2.setColor(new Color(26, 29, 36));
        g2.setStroke(new BasicStroke(1f));
        for (int x = 0; x < w; x += 40) {
            g2.drawLine(x, 0, x, h);
        }
        for (int y = 0; y < h; y += 40) {
            g2.drawLine(0, y, w, y);
        }

        // Chamber Walls Boundary
        int margin = 16;
        int chamberW = w - margin * 2;
        int chamberH = h - margin * 2 - 28;

        g2.setColor(new Color(24, 27, 34));
        g2.fillRoundRect(margin, margin, chamberW, chamberH, 12, 12);

        g2.setColor(Theme.BORDER_SUBTLE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(margin, margin, chamberW, chamberH, 12, 12);

        // Top Chamber Header Bar
        g2.setColor(new Color(30, 34, 43));
        g2.fillRoundRect(margin + 1, margin + 1, chamberW - 2, 34, 12, 12);
        g2.fillRect(margin + 1, margin + 20, chamberW - 2, 15);
        g2.setColor(Theme.BORDER_SUBTLE);
        g2.drawLine(margin, margin + 35, margin + chamberW, margin + 35);

        // Chamber Title & Mission Objective (Guaranteed zero collision)
        g2.setFont(Theme.FONT_BODY_BOLD);
        g2.setColor(Theme.TEXT_PRIMARY);
        String leftTitle = "SECTOR 0" + room.getRoomNumber() + " CHAMBER";
        g2.drawString(leftTitle, margin + 14, margin + 23);

        boolean unlocked = room.isDoorUnlocked();
        int solved = room.getSolvedPuzzlesCount();
        String rightStatus = unlocked ? "STATUS: UNLOCKED [PROCEED]" : "LOCKS: " + solved + "/3 DISENGAGED";
        g2.setColor(unlocked ? Theme.EMERALD : Theme.AMBER);
        var fmRight = g2.getFontMetrics();
        g2.drawString(rightStatus, margin + chamberW - fmRight.stringWidth(rightStatus) - 14, margin + 23);
    }

    private void drawSecurityBlastDoor(Graphics2D g2, int w, int h, Room room) {
        int doorW = Math.min(380, w - 80);
        int doorH = 68;
        int doorX = (w - doorW) / 2;
        int doorY = 54;

        boolean unlocked = room.isDoorUnlocked();
        List<Puzzle> puzzles = room.getPuzzles();

        // Door Frame Background
        g2.setColor(doorHovered ? new Color(36, 40, 50) : new Color(28, 31, 39));
        g2.fillRoundRect(doorX, doorY, doorW, doorH, 6, 6);

        // Door Frame Border
        Color borderColor = unlocked ? Theme.EMERALD : (doorHovered ? Theme.ACCENT_BLUE : new Color(55, 65, 81));
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(doorHovered ? 1.8f : 1f));
        g2.drawRoundRect(doorX, doorY, doorW, doorH, 6, 6);

        // 4 Physical Bulkhead Corner Mounting Bolts
        int rSize = 4;
        g2.setColor(new Color(75, 85, 99));
        g2.fillOval(doorX + 5, doorY + 5, rSize, rSize);
        g2.fillOval(doorX + doorW - 9, doorY + 5, rSize, rSize);
        g2.fillOval(doorX + 5, doorY + doorH - 9, rSize, rSize);
        g2.fillOval(doorX + doorW - 9, doorY + doorH - 9, rSize, rSize);

        // Door Title Banner
        g2.setFont(Theme.FONT_BODY_BOLD);
        var fm = g2.getFontMetrics();
        String doorTitle = unlocked ? "✓ BLAST DOOR DISENGAGED • CLICK TO ESCAPE" : "SECURITY BULKHEAD • 3 LOCK BOLTS ACTIVE";
        g2.setColor(unlocked ? Theme.EMERALD : Theme.TEXT_PRIMARY);
        g2.drawString(doorTitle, doorX + (doorW - fm.stringWidth(doorTitle)) / 2, doorY + 22);

        // Draw 3 Physical Lock Bolts
        int boltW = (doorW - 32) / 3;
        int boltH = 26;
        int boltY = doorY + 32;

        for (int i = 0; i < 3; i++) {
            int boltX = doorX + 12 + i * (boltW + 4);
            boolean isSolved = (i < puzzles.size() && puzzles.get(i).isSolved());

            // Bolt background
            g2.setColor(isSolved ? new Color(20, 83, 45) : new Color(38, 42, 52));
            g2.fillRoundRect(boltX, boltY, boltW, boltH, 4, 4);

            g2.setColor(isSolved ? Theme.EMERALD : new Color(60, 66, 82));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(boltX, boltY, boltW, boltH, 4, 4);

            // Bolt text
            g2.setFont(Theme.FONT_TERMINAL);
            var fmBolt = g2.getFontMetrics();
            String label = isSolved ? "Bolt " + (i + 1) + " [OPEN]" : "Bolt " + (i + 1) + " [LOCKED]";
            g2.setColor(isSolved ? Color.WHITE : Theme.TEXT_MUTED);
            g2.drawString(label, boltX + (boltW - fmBolt.stringWidth(label)) / 2, boltY + 17);
        }
    }

    private void drawInvestigationStations(Graphics2D g2, int w, int h, Room room) {
        int cardW = 175;
        int cardH = 72;

        for (RoomObject obj : room.getRoomObjects()) {
            if ("DOOR".equalsIgnoreCase(obj.getIconType()) || "AIRLOCK".equalsIgnoreCase(obj.getIconType())) {
                continue;
            }

            int cx = (int)(obj.getRelativeX() * w);
            int cy = (int)(obj.getRelativeY() * h);

            cx = Math.max(cardW / 2 + 30, Math.min(w - cardW / 2 - 30, cx));
            cy = Math.max(160 + cardH / 2, Math.min(h - cardH / 2 - 40, cy));

            int cardX = cx - cardW / 2;
            int cardY = cy - cardH / 2;

            boolean isHovered = (obj == hoveredObject);

            // Station Card Background
            g2.setColor(isHovered ? new Color(38, 43, 54) : new Color(28, 31, 40));
            g2.fillRoundRect(cardX, cardY, cardW, cardH, 6, 6);

            // Station Border
            g2.setColor(isHovered ? Theme.ACCENT_BLUE : new Color(55, 65, 81));
            g2.setStroke(new BasicStroke(isHovered ? 1.8f : 1f));
            g2.drawRoundRect(cardX, cardY, cardW, cardH, 6, 6);

            // Status Indicator Dot
            Color dotColor = obj.isItemHarvested() ? Theme.EMERALD : (obj.hasCollectibleItem() ? Theme.AMBER : Theme.TEXT_DIM);
            g2.setColor(dotColor);
            g2.fillOval(cardX + 12, cardY + 12, 6, 6);

            // Station Name
            g2.setFont(Theme.FONT_BODY_BOLD);
            g2.setColor(isHovered ? Color.WHITE : Theme.TEXT_PRIMARY);
            String name = obj.getName();
            var fm = g2.getFontMetrics();
            if (fm.stringWidth(name) > cardW - 32) {
                while (name.length() > 3 && fm.stringWidth(name + "...") > cardW - 32) {
                    name = name.substring(0, name.length() - 1);
                }
                name += "...";
            }
            g2.drawString(name, cardX + 24, cardY + 20);

            // Category tag (formatted cleanly without noisy brackets)
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(Theme.TEXT_MUTED);
            String fixtureType = formatFixtureType(obj.getIconType());
            g2.drawString(fixtureType, cardX + 12, cardY + 40);

            // Investigation Status
            g2.setFont(Theme.FONT_TERMINAL);
            if (obj.isItemHarvested()) {
                g2.setColor(Theme.EMERALD);
                g2.drawString("✓ Examined", cardX + 12, cardY + 58);
            } else if (obj.hasCollectibleItem()) {
                g2.setColor(isHovered ? Theme.AMBER : new Color(217, 119, 6));
                g2.drawString("● Inspect Fixture", cardX + 12, cardY + 58);
            } else {
                g2.setColor(Theme.TEXT_MUTED);
                g2.drawString("Inspect Fixture", cardX + 12, cardY + 58);
            }
        }
    }

    private String formatFixtureType(String iconType) {
        if (iconType == null || iconType.isEmpty()) return "Investigation Fixture";
        return iconType.charAt(0) + iconType.substring(1).toLowerCase() + " Station";
    }

    private void drawInvestigationPrompt(Graphics2D g2, int w, int h) {
        int barY = h - 20;
        g2.setFont(Theme.FONT_BODY);
        String msg;
        Color color;

        if (doorHovered) {
            Room room = gameEngine.getCurrentRoom();
            if (room.isDoorUnlocked()) {
                color = Theme.EMERALD;
                msg = "Doorway Unlocked: Click the blast door to advance to the next sector!";
            } else {
                color = Theme.AMBER;
                msg = "Doorway Sealed: Solve all 3 chamber locks to unlock the blast door.";
            }
        } else if (hoveredObject != null) {
            color = Theme.ACCENT_BLUE;
            msg = "Inspect " + hoveredObject.getName() + ": Click to search for clues and tools.";
        } else {
            color = Theme.TEXT_MUTED;
            msg = "Search chamber fixtures for clues and tools. Crack all 3 locks to escape.";
        }

        // Safety check to ensure string fits without horizontal truncation
        var fm = g2.getFontMetrics();
        int maxW = w - 48;
        if (fm.stringWidth(msg) > maxW) {
            while (msg.length() > 6 && fm.stringWidth(msg + "...") > maxW) {
                msg = msg.substring(0, msg.length() - 1);
            }
            msg = msg + "...";
        }

        g2.setColor(color);
        g2.drawString(msg, 24, barY);
    }
}
