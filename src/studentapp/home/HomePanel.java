package studentapp.home;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import studentapp.database.DatabaseConnection;

public class HomePanel extends JPanel {

    private JLabel lblTotalStudents;
    private JLabel lblNewThisMonth;
    private JLabel lblActiveBlocks;
    private JLabel lblEmptyBlocks;
    private JLabel lblFirstSem;
    private JLabel lblSecondSem;
    private JLabel lblSummer;
    private JLabel lblFirstYear;
    private JLabel lblSecondYear;
    private JLabel lblThirdYear;
    private JLabel lblFourthYear;
    private static final Color BG_MAIN = new Color(245, 246, 250);
    Color PRIMARY = new Color(52, 152, 219);   // Blue
    Color NEUTRAL = new Color(149, 165, 166);  // Soft gray



    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        // === HEADER ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_MAIN);
        header.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(44, 62, 80));
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // === GRID ===
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(20, 40, 50, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(15, 15, 15, 15);

// === ROW 1: 4 cards ===
lblTotalStudents = new JLabel("0", SwingConstants.CENTER);
grid.add(createStatCard("Total Students", lblTotalStudents, PRIMARY), setGbc(gbc, 0, 0));

lblNewThisMonth = new JLabel("0", SwingConstants.CENTER);
grid.add(createStatCard("New This Month", lblNewThisMonth, PRIMARY), setGbc(gbc, 1, 0));

lblActiveBlocks = new JLabel("0", SwingConstants.CENTER);
grid.add(createStatCard("Active Blocks", lblActiveBlocks, PRIMARY), setGbc(gbc, 2, 0));

lblEmptyBlocks = new JLabel("0", SwingConstants.CENTER);
grid.add(createStatCard("Empty Blocks", lblEmptyBlocks, NEUTRAL), setGbc(gbc, 3, 0));

// === ROW 2: semester cards ===
lblFirstSem = new JLabel("0", SwingConstants.CENTER);
grid.add(createStatCard("1st Semester Students", lblFirstSem, PRIMARY), setGbc(gbc, 0, 1, 2));

lblSecondSem = new JLabel("0", SwingConstants.CENTER);
grid.add(createStatCard("2nd Semester Students", lblSecondSem, PRIMARY), setGbc(gbc, 2, 1));

lblSummer = new JLabel("0", SwingConstants.CENTER);
grid.add(createStatCard("Summer Students", lblSummer, NEUTRAL), setGbc(gbc, 3, 1));

// === ROW 3: year level cards ===
lblFirstYear = new JLabel("0", SwingConstants.CENTER);
grid.add(createStatCard("1st Year Students", lblFirstYear, PRIMARY), setGbc(gbc, 0, 2));

lblSecondYear = new JLabel("0", SwingConstants.CENTER);
grid.add(createStatCard("2nd Year Students", lblSecondYear, PRIMARY), setGbc(gbc, 1, 2));

lblThirdYear = new JLabel("0", SwingConstants.CENTER);
grid.add(createStatCard("3rd Year Students", lblThirdYear, PRIMARY), setGbc(gbc, 2, 2));

lblFourthYear = new JLabel("0", SwingConstants.CENTER);
grid.add(createStatCard("4th Year Students", lblFourthYear, NEUTRAL), setGbc(gbc, 3, 2));


        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        refresh(); // Auto load
    }

    private GridBagConstraints setGbc(GridBagConstraints gbc, int x, int y) {
        return setGbc(gbc, x, y, 1);
    }

    private GridBagConstraints setGbc(GridBagConstraints gbc, int x, int y, int width) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        return gbc;
    }

private JPanel createStatCard(String title, JLabel valueLabel, Color accent) {
    JPanel card = new JPanel(new BorderLayout());
    card.setOpaque(false); // ← KEY: allow transparency

    // Semi-transparent background panel
    JPanel content = new JPanel(new BorderLayout());
    content.setBackground(new Color(255, 255, 255, 180)); // transparent white
    content.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220, 180), 1),
            new EmptyBorder(30, 30, 30, 30)
    ));

    JPanel accentBar = new JPanel();
    accentBar.setBackground(accent);
    accentBar.setPreferredSize(new Dimension(0, 8));

    valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
    valueLabel.setForeground(accent);
    valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

    JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    titleLabel.setForeground(new Color(80, 80, 80));

    content.add(valueLabel, BorderLayout.CENTER);
    content.add(titleLabel, BorderLayout.SOUTH);

    card.add(accentBar, BorderLayout.NORTH);
    card.add(content, BorderLayout.CENTER);

    return card;
}

    public void refresh() {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Total Students
            lblTotalStudents.setText(count(conn, "SELECT COUNT(*) FROM students"));

            // New This Month
            lblNewThisMonth.setText(count(conn,
                    "SELECT COUNT(*) FROM students WHERE MONTH(created_at) = MONTH(CURRENT_DATE()) AND YEAR(created_at) = YEAR(CURRENT_DATE())"));

            // Semesters
            lblFirstSem.setText(count(conn, "SELECT COUNT(*) FROM students WHERE semester = '1st Semester'"));
            lblSecondSem.setText(count(conn, "SELECT COUNT(*) FROM students WHERE semester = '2nd Semester'"));
            lblSummer.setText(count(conn, "SELECT COUNT(*) FROM students WHERE semester = 'Summer'"));

            // Year Levels
            lblFirstYear.setText(count(conn, "SELECT COUNT(*) FROM students WHERE year = '1st Year'"));
            lblSecondYear.setText(count(conn, "SELECT COUNT(*) FROM students WHERE year = '2nd Year'"));
            lblThirdYear.setText(count(conn, "SELECT COUNT(*) FROM students WHERE year = '3rd Year'"));
            lblFourthYear.setText(count(conn, "SELECT COUNT(*) FROM students WHERE year = '4th Year'"));

            // Active & Empty Blocks
            int active = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(DISTINCT block) FROM students WHERE block IS NOT NULL AND block != ''");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) active = rs.getInt(1);
            }
            lblActiveBlocks.setText(String.valueOf(active));
            lblEmptyBlocks.setText(String.valueOf(10 - active)); // A to J = 10 blocks

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load dashboard data: " + e.getMessage());
        }
    }

    private String count(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : "0";
        }
    }
}