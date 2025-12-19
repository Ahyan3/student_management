package studentapp.home;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HomePanel extends JPanel {

    private JLabel lblTotalStudents;
    private JLabel lblNewStudents;
    private JLabel lblUpdates;

    private JButton btnRefresh;
    private JPanel lastOpenedCard = null;
    private CardLayout lastFlip = null;

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

        statsPanel.add(createCard(
                "Total Students",
                lblTotalStudents,
                new Color(52, 152, 219),
                "SELECT student_id, fullname, course, year FROM students",
                "students"));

        statsPanel.add(createCard(
                "New This Month",
                lblNewStudents,
                new Color(39, 174, 96),
                "SELECT student_id, fullname, course, year FROM students WHERE MONTH(created_at)=MONTH(CURRENT_DATE())",
                "students"));

        statsPanel.add(createCard(
                "Updates Made",
                lblUpdates,
                new Color(44, 62, 80),
                "SELECT student_id, action, timestamp FROM update_logs",
                "updates"));

        add(statsPanel, BorderLayout.CENTER);

        loadDashboardStats();
    }

    // ===== CARD DESIGN =====
    private JPanel createCard(String title, JLabel value, Color accent, String query, String type) {

        JPanel frontCard = new JPanel(new BorderLayout());
        frontCard.setBackground(Color.WHITE);
        frontCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JPanel topAccent = new JPanel();
        topAccent.setBackground(accent);
        topAccent.setPreferredSize(new Dimension(10, 5));

        value.setFont(new Font("Poppins", Font.BOLD, 36));
        value.setForeground(accent);
        value.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 18));
        lblTitle.setForeground(new Color(100, 100, 100));

        frontCard.add(topAccent, BorderLayout.NORTH);
        frontCard.add(value, BorderLayout.CENTER);
        frontCard.add(lblTitle, BorderLayout.SOUTH);

        // Back table panel
        JPanel backCard = createTablePanel(query, accent, title, type);

        CardLayout flip = new CardLayout();
        JPanel wrapper = new JPanel(flip);
        wrapper.add(frontCard, "front");
        wrapper.add(backCard, "back");

        wrapper.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        wrapper.addMouseListener(new MouseAdapter() {
            private boolean showingBack = false;

            @Override
            public void mouseClicked(MouseEvent e) {

                if (lastOpenedCard != null && lastOpenedCard != wrapper) {
                    lastFlip.show(lastOpenedCard, "front");
                }

                if (!showingBack) {
                    flip.show(wrapper, "back");
                } else {
                    flip.show(wrapper, "front");
                }

                showingBack = !showingBack;
                lastOpenedCard = wrapper;
                lastFlip = flip;
            }
        });

        return wrapper;
    }

    private JPanel createTablePanel(String query, Color accent, String title, String type) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel(title + " - List", SwingConstants.CENTER);
        lbl.setFont(new Font("Poppins", Font.BOLD, 18));
        lbl.setForeground(accent);

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(table);

        if (type.equals("updates")) {
            model.addColumn("Student ID");
            model.addColumn("Action");
            model.addColumn("Timestamp");
        } else {
            model.addColumn("ID");
            model.addColumn("Name");
            model.addColumn("Course");
            model.addColumn("Year");
        }

        JPopupMenu popup = new JPopupMenu();
        JMenuItem viewProfile = new JMenuItem("View Profile");
        popup.add(viewProfile);
        table.setComponentPopupMenu(popup);

        viewProfile.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String studentId = model.getValueAt(row, 0).toString();
                JOptionPane.showMessageDialog(this, "Open profile for ID: " + studentId);
            }
        });

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                if (type.equals("updates")) {
                    model.addRow(new Object[] {
                            rs.getInt("student_id"),
                            rs.getString("action"),
                            rs.getString("timestamp")
                    });
                } else {
                    model.addRow(new Object[] {
                            rs.getInt("student_id"),
                            rs.getString("fullname"),
                            rs.getString("course"),
                            rs.getString("year")
                    });
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error loading list.");
        }

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
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
            if (rs.next())
                lblTotalStudents.setText(rs.getString(1));

            rs = conn.prepareStatement(
                    "SELECT COUNT(*) FROM students WHERE MONTH(created_at)=MONTH(CURRENT_DATE()) " +
                            "AND YEAR(created_at)=YEAR(CURRENT_DATE())")
                    .executeQuery();
            if (rs.next())
                lblNewStudents.setText(rs.getString(1));

            rs = conn.prepareStatement("SELECT COUNT(*) FROM update_logs").executeQuery();
            if (rs.next())
                lblUpdates.setText(rs.getString(1));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load dashboard data.");
        }
    }

    private void loadCustomFont(String path) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new java.io.File(path));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        } catch (Exception ignored) {
        }
    }
}
