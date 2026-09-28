package escapex.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: STATIC CONSTANTS & CENTRALIZED DESIGN SYSTEM
 * ============================================================================
 * Why this matters in OOP:
 * The `Theme` class centralizes visual styling tokens (colors, typography, spacing).
 *
 * Uses a refined, modern dark desktop palette (inspired by clean developer tools
 * like VS Code and GitHub Dark) instead of harsh neon glows:
 *   - Soft slate backgrounds
 *   - Clear, accessible contrast
 *   - Calibrated indigo/blue accents
 *   - Crisp, readable typography
 * ============================================================================
 */
public final class Theme {

    private Theme() {
        // Prevent instantiation
    }

    // --- Modern Clean Dark Backgrounds ---
    public static final Color BG_DARK = new Color(24, 25, 29);         // Main window background (#18191D)
    public static final Color BG_PANEL = new Color(33, 35, 41);        // Card / Container surface (#212329)
    public static final Color BG_PANEL_ALT = new Color(43, 46, 54);    // Hover / secondary card (#2B2E36)
    public static final Color BG_INPUT = new Color(18, 19, 22);        // Recessed inputs / console (#121316)

    // --- Clean Accent Colors (Accessible & Balanced) ---
    public static final Color CYAN_NEON = new Color(59, 130, 246);     // Calm modern Royal Blue (#3B82F6)
    public static final Color ACCENT_BLUE = new Color(59, 130, 246);   // Primary action blue
    public static final Color EMERALD = new Color(34, 197, 94);        // Soft success green (#22C55E)
    public static final Color AMBER = new Color(245, 158, 11);         // Warning amber (#F59E0B)
    public static final Color PURPLE = new Color(139, 92, 246);        // Soft violet (#8B5CF6)
    public static final Color ROSE = new Color(239, 68, 68);           // Danger red (#EF4444)

    // --- Foreground Text Colors ---
    public static final Color TEXT_PRIMARY = new Color(243, 244, 246); // Crisp off-white (#F3F4F6)
    public static final Color TEXT_MUTED = new Color(156, 163, 175);   // Secondary text gray (#9CA3AF)
    public static final Color TEXT_DIM = new Color(107, 114, 128);     // Faint label gray (#6B7280)

    // --- Borders ---
    public static final Color BORDER_SUBTLE = new Color(55, 59, 68);   // Clean divider line
    public static final Color BORDER_GLOW = new Color(59, 130, 246, 160);

    // --- Typography (Clean & Legible) ---
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TERMINAL = new Font("Consolas", Font.PLAIN, 12);
    public static final Font FONT_TERMINAL_BOLD = new Font("Consolas", Font.BOLD, 12);
    public static final Font FONT_TIMER = new Font("Consolas", Font.BOLD, 18);
}
