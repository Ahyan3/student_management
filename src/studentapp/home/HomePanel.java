package studentapp.home;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class HomePanel extends JPanel {

    private JLabel lblTotalStudents;
    private JLabel lblNewStudents;
    private JLabel lblUpdates;

    private JButton btnRefresh;

    private final String DB_URL = "jdbc:mysql://localhost:3306/student_system";
    private final String DB_USER = "root";
    private final String DB_PASS = "";

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        loadCustomFont("src/main/resources/fonts/Poppins-Regular.ttf");
        loadCustomFont("src/main/resources/fonts/Poppins-Bold.ttf");

        // ===== TOP BAR =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 10, 25));

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(new Font("Poppins", Font.BOLD, 26));

        btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Poppins", Font.BOLD, 14));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnRefresh.setToolTipText("Refresh dashboard data");

        btnRefresh.addActionListener(e -> refreshDashboard());

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnRefresh, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ===== STATS =====
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));

        lblTotalStudents = new JLabel("0");
        lblNewStudents = new JLabel("0");
        lblUpdates = new JLabel("0");

        statsPanel.add(createCard("Total Students", lblTotalStudents, new Color(52, 152, 219)));
        statsPanel.add(createCard("New This Month", lblNewStudents, new Color(46, 204, 113)));
        statsPanel.add(createCard("Updates Made", lblUpdates, new Color(155, 89, 182)));

        add(statsPanel, BorderLayout.CENTER);

        loadDashboardStats();
    }

    // ===== CARD DESIGN =====
    private JPanel createCard(String title, JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel topAccent = new JPanel();
        topAccent.setBackground(accent);
        topAccent.setPreferredSize(new Dimension(10, 5));

        value.setFont(new Font("Poppins", Font.BOLD, 36));
        value.setForeground(accent);
        value.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Poppins", Font.PLAIN, 15));
        lblTitle.setForeground(new Color(100, 100, 100));

        card.add(topAccent, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);

        return card;
    }

    // ===== REFRESH UX =====
    private void refreshDashboard() {
        btnRefresh.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingUtilities.invokeLater(() -> {
            loadDashboardStats();
            btnRefresh.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        });
    }

    private void loadDashboardStats() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            ResultSet rs;

            rs = conn.prepareStatement("SELECT COUNT(*) FROM students").executeQuery();
            if (rs.next()) lblTotalStudents.setText(rs.getString(1));

            rs = conn.prepareStatement(
                "SELECT COUNT(*) FROM students WHERE MONTH(created_at)=MONTH(CURRENT_DATE()) " +
                "AND YEAR(created_at)=YEAR(CURRENT_DATE())"
            ).executeQuery();
            if (rs.next()) lblNewStudents.setText(rs.getString(1));

            rs = conn.prepareStatement("SELECT COUNT(*) FROM update_logs").executeQuery();
            if (rs.next()) lblUpdates.setText(rs.getString(1));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load dashboard data.");
        }
    }

    private void loadCustomFont(String path) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new java.io.File(path));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        } catch (Exception ignored) {}
    }
}
