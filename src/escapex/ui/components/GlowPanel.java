package escapex.ui.components;

import escapex.ui.Theme;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: INHERITANCE & METHOD OVERRIDING (Custom UI Container)
 * ============================================================================
 * Why this matters in OOP:
 * `GlowPanel` is a specialized {@link JPanel} that paints a sleek sci-fi
 * card background with rounded corners and an optional colored accent glow border.
 *
 * Demonstrates:
 * 1. Reusability: Any part of the UI needing a sci-fi card container uses `GlowPanel`.
 * 2. Polymorphism: Since `GlowPanel` IS-A `JPanel`, it can be added to any Swing
 *    layout container polymorphically.
 * ============================================================================
 */
public class GlowPanel extends JPanel {

    private Color borderColor;
    private Color backgroundColor;
    private int cornerRadius;

    public GlowPanel(Color borderColor, Color backgroundColor, int cornerRadius) {
        this.borderColor = borderColor != null ? borderColor : Theme.BORDER_SUBTLE;
        this.backgroundColor = backgroundColor != null ? backgroundColor : Theme.BG_PANEL;
        this.cornerRadius = cornerRadius;
        setOpaque(false);
    }

    public GlowPanel(Color borderColor) {
        this(borderColor, Theme.BG_PANEL, 10);
    }

    public GlowPanel() {
        this(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 10);
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Draw card background
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        // Draw border
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}
