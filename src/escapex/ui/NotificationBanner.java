package escapex.ui;

import escapex.ui.components.GlowPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: DECORATOR / NOTIFICATION OVERLAY COMPONENT
 * ============================================================================
 * Why this matters in OOP:
 * `NotificationBanner` encapsulates transient HUD notification banners.
 *
 * It decouples notification presentation from game events:
 *   - Auto-dismiss countdown via Swing `Timer`
 *   - Distinct visual themes for Success (Item/Puzzle), Alert, and Danger
 *   - Zero impact on layout stability
 * ============================================================================
 */
public class NotificationBanner extends JPanel {

    public enum NotificationType {
        ITEM_COLLECTED(Theme.EMERALD, "✔ ITEM ACQUIRED"),
        PUZZLE_SOLVED(Theme.ACCENT_BLUE, "★ LOCK DISENGAGED"),
        DOOR_UNLOCKED(Theme.EMERALD, "🔓 BLAST DOOR UNLOCKED"),
        WARNING(Theme.AMBER, "⚠ ALERT"),
        DANGER(Theme.ROSE, "✕ ACCESS DENIED");

        final Color color;
        final String tag;

        NotificationType(Color color, String tag) {
            this.color = color;
            this.tag = tag;
        }
    }

    private final JLabel tagLabel;
    private final JLabel messageLabel;
    private final Timer dismissTimer;
    private Color activeBorderColor;

    public NotificationBanner() {
        setLayout(new BorderLayout(8, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        setPreferredSize(new Dimension(500, 34));
        setVisible(false);

        activeBorderColor = Theme.BORDER_SUBTLE;

        tagLabel = new JLabel();
        tagLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        add(tagLabel, BorderLayout.WEST);

        messageLabel = new JLabel();
        messageLabel.setFont(Theme.FONT_BODY_BOLD);
        messageLabel.setForeground(Color.WHITE);
        add(messageLabel, BorderLayout.CENTER);

        dismissTimer = new Timer(4000, e -> setVisible(false));
        dismissTimer.setRepeats(false);
    }

    public void showNotification(NotificationType type, String message) {
        dismissTimer.stop();
        this.activeBorderColor = type.color;

        tagLabel.setText(type.tag + "  |");
        tagLabel.setForeground(type.color);

        messageLabel.setText(message);

        setVisible(true);
        repaint();
        dismissTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!isVisible()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(new Color(18, 19, 22, 235));
        g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);

        // Glowing border
        g2.setColor(activeBorderColor);
        g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);

        g2.dispose();
        super.paintComponent(g);
    }
}
