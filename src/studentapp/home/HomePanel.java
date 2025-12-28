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

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // === HEADER ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel title = new JLabel("Overview");
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
        grid.add(createStatCard("Total Students", lblTotalStudents, new Color(52, 152, 219)), setGbc(gbc, 0, 0));

        lblNewThisMonth = new JLabel("0", SwingConstants.CENTER);
        grid.add(createStatCard("New This Month", lblNewThisMonth, new Color(39, 174, 96)), setGbc(gbc, 1, 0));

        lblActiveBlocks = new JLabel("0", SwingConstants.CENTER);
        grid.add(createStatCard("Active Blocks", lblActiveBlocks, new Color(155, 89, 182)), setGbc(gbc, 2, 0));

        lblEmptyBlocks = new JLabel("0", SwingConstants.CENTER);
        grid.add(createStatCard("Empty Blocks", lblEmptyBlocks, new Color(149, 165, 166)), setGbc(gbc, 3, 0));

        // === ROW 2: 3 semester cards ===
        lblFirstSem = new JLabel("0", SwingConstants.CENTER);
        grid.add(createStatCard("1st Semester Students", lblFirstSem, new Color(241, 196, 15)), setGbc(gbc, 0, 1, 2)); // spans 2 columns

        lblSecondSem = new JLabel("0", SwingConstants.CENTER);
        grid.add(createStatCard("2nd Semester Students", lblSecondSem, new Color(230, 126, 34)), setGbc(gbc, 2, 1));

        lblSummer = new JLabel("0", SwingConstants.CENTER);
        grid.add(createStatCard("Summer Students", lblSummer, new Color(231, 76, 60)), setGbc(gbc, 3, 1));

        // === ROW 3: 4 year level cards ===
        lblFirstYear = new JLabel("0", SwingConstants.CENTER);
        grid.add(createStatCard("1st Year Students", lblFirstYear, new Color(46, 204, 113)), setGbc(gbc, 0, 2));

        lblSecondYear = new JLabel("0", SwingConstants.CENTER);
        grid.add(createStatCard("2nd Year Students", lblSecondYear, new Color(52, 152, 219)), setGbc(gbc, 1, 2));

        lblThirdYear = new JLabel("0", SwingConstants.CENTER);
        grid.add(createStatCard("3rd Year Students", lblThirdYear, new Color(155, 89, 182)), setGbc(gbc, 2, 2));

        lblFourthYear = new JLabel("0", SwingConstants.CENTER);
        grid.add(createStatCard("4th Year Students", lblFourthYear, new Color(231, 76, 60)), setGbc(gbc, 3, 2));

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
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(30, 30, 30, 30)));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(0, 8));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLabel.setForeground(accent);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(80, 80, 80));

        card.add(accentBar, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

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