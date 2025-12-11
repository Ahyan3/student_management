package studentapp.home;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class HomePanel extends JPanel {

    private JLabel lblTotalStudents;
    private JLabel lblNewStudents;
    private JLabel lblUpdates;
    
    private final String DB_URL = "jdbc:mysql://localhost:3306/student_system"; 
    private final String DB_USER = "root"; 
    private final String DB_PASS = "";   

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== LOAD POPPINS FONT =====
        loadCustomFont("src/main/resources/fonts/Poppins-Regular.ttf");
        loadCustomFont("src/main/resources/fonts/Poppins-Bold.ttf");

        // ===== TITLE =====
        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(new Font("Poppins", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(25, 25, 15, 25));
        // add(title, BorderLayout.NORTH);

        // ===== TOP PANEL (TITLE + REFRESH BUTTON) =====
JPanel topPanel = new JPanel(new BorderLayout());
topPanel.setBackground(Color.WHITE);

// JLabel title = new JLabel("Dashboard Overview");
// title.setFont(new Font("Poppins", Font.BOLD, 28));
// title.setBorder(BorderFactory.createEmptyBorder(25, 25, 15, 25));

// Refresh Button
JButton btnRefresh = new JButton("Refresh");
btnRefresh.setFocusPainted(false);
btnRefresh.setBackground(new Color(51, 153, 255));
btnRefresh.setForeground(Color.WHITE);
btnRefresh.setFont(new Font("Poppins", Font.BOLD, 14));
btnRefresh.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));

btnRefresh.addActionListener(e -> loadDashboardStats());

// Add button to the right
topPanel.add(title, BorderLayout.WEST);
topPanel.add(btnRefresh, BorderLayout.EAST);

add(topPanel, BorderLayout.NORTH);


        // ===== STATS PANEL =====
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));

        lblTotalStudents = new JLabel("0", SwingConstants.CENTER);
        lblNewStudents = new JLabel("0", SwingConstants.CENTER);
        lblUpdates = new JLabel("0", SwingConstants.CENTER);

        statsPanel.add(makeStatCard("Total Students", lblTotalStudents));
        statsPanel.add(makeStatCard("New This Month", lblNewStudents));
        statsPanel.add(makeStatCard("Updates Made", lblUpdates));

        add(statsPanel, BorderLayout.CENTER);

        // Load numbers from DB
        loadDashboardStats();
    }

    private JPanel makeStatCard(String label, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setLayout(new BorderLayout(5, 5));
        card.setPreferredSize(new Dimension(180, 120));
        card.setOpaque(true);

        valueLabel.setFont(new Font("Poppins", Font.BOLD, 34));
        valueLabel.setForeground(new Color(50, 50, 50));

        JLabel lblText = new JLabel(label, SwingConstants.CENTER);
        lblText.setFont(new Font("Poppins", Font.PLAIN, 16));
        lblText.setForeground(new Color(90, 90, 90));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblText, BorderLayout.SOUTH);

        return card;
    }

    private void loadDashboardStats() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            // TOTAL STUDENTS
            String totalQuery = "SELECT COUNT(*) FROM students";
            PreparedStatement ps1 = conn.prepareStatement(totalQuery);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) lblTotalStudents.setText(rs1.getString(1));

            // NEW THIS MONTH
            String monthQuery =
                "SELECT COUNT(*) FROM students WHERE MONTH(created_at) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(created_at) = YEAR(CURRENT_DATE())";
            PreparedStatement ps2 = conn.prepareStatement(monthQuery);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) lblNewStudents.setText(rs2.getString(1));

            // UPDATES MADE
            String updatesQuery = "SELECT COUNT(*) FROM update_logs";
            PreparedStatement ps3 = conn.prepareStatement(updatesQuery);
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) lblUpdates.setText(rs3.getString(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCustomFont(String path) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new java.io.File(path));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (Exception ex) {
            System.out.println("Font load failed: " + path);
        }
    }
}
