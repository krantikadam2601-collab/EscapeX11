package escapex.ui;

import escapex.model.Room;
import escapex.service.GameEngine;
import escapex.ui.components.GlowPanel;
import escapex.ui.components.SciFiButton;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
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
 * OOP CONCEPT DEMONSTRATED: SECTOR TRANSITION PRESENTATION & VIEW DELEGATION
 * ============================================================================
 * Why this matters in OOP:
 * `SectorTransitionDialog` encapsulates the interstitial briefing screen shown
 * when an operative breaches a blast door and advances into the next sector.
 *
 * It decouples transition narrative and visual briefings from the core engine loop,
 * providing tactical sector objectives before the operative engages the new chamber.
 * ============================================================================
 */
public class SectorTransitionDialog extends JDialog {

    public SectorTransitionDialog(Frame owner, Room nextRoom, int previousRoomNumber) {
        super(owner, "EscapeX - Sector Transition Protocol", true);
        setSize(560, 480);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_DARK);

        initUI(nextRoom, previousRoomNumber);
    }

    private void initUI(Room nextRoom, int previousRoomNumber) {
        JPanel mainCard = new GlowPanel(Theme.CYAN_NEON, Theme.BG_PANEL, 14);
        mainCard.setLayout(new BorderLayout(12, 12));
        mainCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // 1. CLEARANCE BANNER
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel clearTag = new JLabel("✔  SECTOR 0" + previousRoomNumber + " BULKHEAD BREACHED (+200 PTS BONUS)", SwingConstants.CENTER);
        clearTag.setFont(Theme.FONT_TERMINAL_BOLD);
        clearTag.setForeground(Theme.EMERALD);
        clearTag.setAlignmentX(CENTER_ALIGNMENT);
        header.add(clearTag);

        header.add(Box.createVerticalStrut(6));

        JLabel title = new JLabel("SECTOR 0" + nextRoom.getRoomNumber() + " UNLOCKED", SwingConstants.CENTER);
        title.setFont(new Font("Consolas", Font.BOLD, 26));
        title.setForeground(Theme.CYAN_NEON);
        title.setAlignmentX(CENTER_ALIGNMENT);
        header.add(title);

        JLabel subtitle = new JLabel(nextRoom.getName().toUpperCase(), SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtitle.setForeground(Theme.TEXT_PRIMARY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        header.add(subtitle);

        mainCard.add(header, BorderLayout.NORTH);

        // 2. BRIEFING TEXT CARD
        String briefingHtml = "<html><body style='font-family:Segoe UI, sans-serif; font-size:11px; color:#F3F4F6; background-color:#121316; padding:12px;'>"
                + "<h3 style='color:#3B82F6; margin-top:0;'>TACTICAL ENVIRONMENT BRIEFING</h3>"
                + "<p>" + nextRoom.getLoreDescription() + "</p>"
                + "<h4 style='color:#F59E0B;'>PRIMARY MISSION DIRECTIVES:</h4>"
                + "<ol>"
                + "<li><b>Investigate Room Fixtures:</b> Click stations on the canvas to harvest critical tools and decrypt inscriptions.</li>"
                + "<li><b>Neutralize 3 Security Locks:</b> Clear Easy, Medium, and Hard diagnostics to retract the 3 deadbolts.</li>"
                + "<li><b>Unseal Blast Door:</b> Once unlocked, advance through the bulkhead door to reach the next sector.</li>"
                + "</ol>"
                + "<p style='color:#9CA3AF;'><i>Caution: Containment timer is running. Stay focused, Operative.</i></p>"
                + "</body></html>";

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(briefingHtml);
        textPane.setEditable(false);
        textPane.setCaretPosition(0);
        textPane.setBackground(Theme.BG_INPUT);

        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE));
        mainCard.add(scroll, BorderLayout.CENTER);

        // 3. ACTION BUTTON
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottomBar.setOpaque(false);

        SciFiButton proceedBtn = new SciFiButton("▶  PROCEED INTO SECTOR 0" + nextRoom.getRoomNumber(), SciFiButton.ButtonStyle.PRIMARY);
        proceedBtn.setPreferredSize(new Dimension(360, 38));
        proceedBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        proceedBtn.addActionListener(e -> dispose());
        bottomBar.add(proceedBtn);

        mainCard.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(mainCard);
    }
}
