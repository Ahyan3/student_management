package studentapp.history;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import studentapp.database.DatabaseConnection;

public class HistoryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> filterBox;
    private JTextField searchField;

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        loadFont();

        // ---------- TITLE ----------
        JLabel title = new JLabel("System Activity History");
        title.setFont(new Font("Poppins", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        add(title, BorderLayout.NORTH);

        // ---------- TOP FILTER BAR ----------
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);

        filterBox = new JComboBox<>(new String[]{"All", "Added", "Updated", "Deleted"});
        filterBox.setFont(new Font("Poppins", Font.PLAIN, 14));

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setFont(new Font("Poppins", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createTitledBorder("Search"));

        JButton refreshBtn = new JButton("Refresh");
        styleButton(refreshBtn);

        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn);

        filterPanel.add(filterBox);
        filterPanel.add(searchField);
        filterPanel.add(searchBtn);
        filterPanel.add(refreshBtn);

        add(filterPanel, BorderLayout.SOUTH);

        // ---------- TABLE ----------
        model = new DefaultTableModel(new String[]{"Action", "Details", "Date & Time"}, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Poppins", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ---------- BUTTON ACTIONS ----------
        searchBtn.addActionListener(e -> searchHistory());
        refreshBtn.addActionListener(e -> loadHistory());
        filterBox.addActionListener(e -> filterHistory());

        loadHistory();
    }

    private void loadFont() {
        try {
            Font pop = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("Poppins-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pop);
        } catch (Exception e) {
            System.out.println("Poppins loading failed.");
        }
    }

    // ---------------------- LOAD ALL HISTORY ----------------------
    private void loadHistory() {
        String sql = "SELECT action, student_id, timestamp FROM update_logs ORDER BY timestamp DESC";
        runQuery(sql);
    }

    // ---------------------- FILTER HISTORY ----------------------
    private void filterHistory() {
        String selected = filterBox.getSelectedItem().toString();

        String sql;

        if (selected.equals("All")) {
            sql = "SELECT action, student_id, timestamp FROM update_logs ORDER BY timestamp DESC";
        } else {
            sql = "SELECT action, student_id, timestamp FROM update_logs WHERE action = '" + selected + "' ORDER BY timestamp DESC";
        }

        runQuery(sql);
    }

    // ---------------------- SEARCH HISTORY ----------------------
    private void searchHistory() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadHistory();
            return;
        }

        String sql =
            "SELECT action, student_id, timestamp FROM update_logs " +
            "WHERE action LIKE '%" + keyword + "%' " +
            "OR student_id LIKE '%" + keyword + "%' " +
            "OR timestamp LIKE '%" + keyword + "%' " +
            "ORDER BY timestamp DESC";

        runQuery(sql);
    }

    // ---------------------- RUN QUERY + FORMAT TABLE ----------------------
    private void runQuery(String sql) {
        model.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String action = rs.getString("action");
                String studentID = rs.getString("student_id");
                String time = rs.getString("timestamp");

                String details = "";

                // Format action descriptions
                switch (action) {
                    case "Added":
                        details = "Added student ID: " + studentID;
                        break;
                    case "Updated":
                        details = "Updated student ID: " + studentID;
                        break;
                    case "Deleted":
                        details = "Deleted student ID: " + studentID;
                        break;
                    default:
                        details = "Action on student ID: " + studentID;
                }

                model.addRow(new Object[]{action, details, time});
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "History Load Error: " + e.getMessage());
        }
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setBackground(new Color(51, 153, 255));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }
}
