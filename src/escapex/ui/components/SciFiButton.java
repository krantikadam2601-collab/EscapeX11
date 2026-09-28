package escapex.ui.components;

import escapex.service.SoundEngine;
import escapex.ui.Theme;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: INHERITANCE & METHOD OVERRIDING (Custom UI Component)
 * ============================================================================
 * Why this matters in OOP:
 * `SciFiButton` extends {@link JButton}.
 *
 * It provides a clean, modern flat button aesthetic with smooth hover states,
 * clear typography, and subtle border lines instead of harsh neon glows.
 *
 * It inherits standard button semantics (listeners, accessibility, focus)
 * while overriding `paintComponent` to deliver a professional visual appearance.
 * ============================================================================
 */
public class SciFiButton extends JButton {

    public enum ButtonStyle {
        PRIMARY(Theme.ACCENT_BLUE, Color.WHITE),
        SUCCESS(Theme.EMERALD, Color.WHITE),
        WARNING(Theme.AMBER, Color.BLACK),
        DANGER(Theme.ROSE, Color.WHITE),
        SECONDARY(Theme.BG_PANEL_ALT, Theme.TEXT_PRIMARY);

        final Color accentColor;
        final Color textColor;

        ButtonStyle(Color accentColor, Color textColor) {
            this.accentColor = accentColor;
            this.textColor = textColor;
        }
    }

    private ButtonStyle style;
    private boolean hovered = false;
    private boolean pressed = false;

    public SciFiButton(String text, ButtonStyle style) {
        super(text);
        this.style = style != null ? style : ButtonStyle.PRIMARY;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setFont(Theme.FONT_BODY_BOLD);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    hovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    pressed = true;
                    SoundEngine.getInstance().playClick();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    public SciFiButton(String text) {
        this(text, ButtonStyle.PRIMARY);
    }

    public void setStyle(ButtonStyle style) {
        this.style = style;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int arc = 6; // Clean, subtle rounding

        Color bg;
        Color border;
        Color textCol = style.textColor;

        if (!isEnabled()) {
            bg = Theme.BG_PANEL;
            border = Theme.BORDER_SUBTLE;
            textCol = Theme.TEXT_DIM;
        } else if (style == ButtonStyle.SECONDARY) {
            if (pressed) {
                bg = Theme.BG_PANEL_ALT.darker();
            } else if (hovered) {
                bg = new Color(54, 58, 68);
            } else {
                bg = Theme.BG_PANEL_ALT;
            }
            border = hovered ? Theme.ACCENT_BLUE : Theme.BORDER_SUBTLE;
            textCol = hovered ? Color.WHITE : Theme.TEXT_PRIMARY;
        } else {
            // Colored action buttons (Primary, Success, Warning, Danger)
            if (pressed) {
                bg = style.accentColor.darker();
            } else if (hovered) {
                bg = style.accentColor.brighter();
            } else {
                bg = style.accentColor;
            }
            border = bg;
        }

        // Draw background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, width, height, arc, arc);

        // Draw border
        g2.setColor(border);
        g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

        // Draw text
        g2.setColor(textCol);
        g2.setFont(getFont());
        var fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(getText())) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
