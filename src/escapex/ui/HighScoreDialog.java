package escapex.ui;

import escapex.service.HighScoreService;
import escapex.ui.components.GlowPanel;
import escapex.ui.components.SciFiButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: SEPARATION OF CONCERNS (Model-View Table Binding)
 * ============================================================================
 * Why this matters in OOP:
 * `HighScoreDialog` displays the leaderboard.
 *
 * It demonstrates:
 * 1. Delegation: Retrieves sorted data from `HighScoreService.loadScores()`.
 * 2. Separation of Concerns: The dialog doesn't parse CSV files or compute scores;
 *    it solely presents data passed to it from the service layer.
 * ============================================================================
 */
public class HighScoreDialog extends JDialog {

    public HighScoreDialog(Window owner, HighScoreService highScoreService) {
        super(owner, "EscapeX Hall of Fame - Top Operatives Leaderboard", ModalityType.APPLICATION_MODAL);
        setSize(560, 420);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_DARK);

        JPanel mainPanel = new GlowPanel(Theme.BORDER_SUBTLE, Theme.BG_PANEL, 12);
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Hall of Fame - Top Operatives");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"Rank", "Operative Name", "Score", "Sectors Cleared", "Time", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<HighScoreService.ScoreEntry> scores = highScoreService.loadScores();
        if (scores.isEmpty()) {
            model.addRow(new Object[]{"-", "No escape records logged yet", "-", "-", "-", "-"});
        } else {
            int rank = 1;
            for (HighScoreService.ScoreEntry entry : scores) {
                model.addRow(new Object[]{
                        "#" + rank++,
                        entry.getPlayerName(),
                        entry.getScore() + " pts",
                        entry.getRoomsCleared() + " / 5",
                        entry.getTimeFormatted(),
                        entry.getDate()
                });
            }
        }

        JTable table = new JTable(model);
        table.setBackground(Theme.BG_INPUT);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setFont(Theme.FONT_TERMINAL);
        table.setRowHeight(26);
        table.getTableHeader().setBackground(Theme.BG_PANEL_ALT);
        table.getTableHeader().setForeground(Theme.TEXT_PRIMARY);
        table.getTableHeader().setFont(Theme.FONT_TERMINAL_BOLD);
        table.setGridColor(Theme.BORDER_SUBTLE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        SciFiButton closeBtn = new SciFiButton("Close", SciFiButton.ButtonStyle.PRIMARY);
        closeBtn.setPreferredSize(new Dimension(100, 32));
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn, BorderLayout.EAST);
        mainPanel.add(footer, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }
}
