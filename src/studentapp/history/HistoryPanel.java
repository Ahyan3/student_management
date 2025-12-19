package studentapp.history;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import studentapp.database.DatabaseConnection;

public class HistoryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> filterBox;
    private JTextField searchField;

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 244, 248));

        // TITLE
        JLabel title = new JLabel("System Activity History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(BorderFactory.createEmptyBorder(40, 50, 30, 50));
        add(title, BorderLayout.NORTH);

        // MAIN CONTENT CARD
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(40, 40, 40, 40)));
        card.setPreferredSize(new Dimension(900, 600));

        // TOP CONTROL BAR (Search + Filter + Refresh)
        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        controlBar.setBackground(Color.WHITE);
        controlBar.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel filterLabel = new JLabel("Filter by Action:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        filterBox = new JComboBox<>(new String[] { "All", "Added", "Updated", "Deleted" });
        filterBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        filterBox.setPreferredSize(new Dimension(180, 40));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(300, 40));

        JButton searchBtn = createStyledButton("Search", new Color(52, 152, 219));
        JButton refreshBtn = createStyledButton("Refresh", new Color(46, 204, 113));

        controlBar.add(filterLabel);
        controlBar.add(filterBox);
        controlBar.add(Box.createHorizontalStrut(30));
        controlBar.add(searchLabel);
        controlBar.add(searchField);
        controlBar.add(searchBtn);
        controlBar.add(Box.createHorizontalStrut(10));
        controlBar.add(refreshBtn);

        card.add(controlBar, BorderLayout.NORTH);

        // TABLE
        model = new DefaultTableModel(new String[] { "Action", "Details", "Date & Time" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        card.add(scrollPane, BorderLayout.CENTER);

        // Wrap card in scroll for responsiveness
        JScrollPane mainScroll = new JScrollPane(card);
        mainScroll.setBorder(null);
        mainScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(mainScroll, BorderLayout.CENTER);

        // ACTIONS
        searchBtn.addActionListener(e -> searchHistory());
        refreshBtn.addActionListener(e -> loadHistory());
        filterBox.addActionListener(e -> filterHistory());

        loadHistory();
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 24, 12, 24));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private void loadHistory() {
        runQuery("SELECT action, student_id, timestamp FROM update_logs ORDER BY timestamp DESC");
    }

    private void filterHistory() {
        String selected = (String) filterBox.getSelectedItem();
        if ("All".equals(selected)) {
            loadHistory();
        } else {
            runQuery("SELECT action, student_id, timestamp FROM update_logs WHERE action = '" + selected
                    + "' ORDER BY timestamp DESC");
        }
    }

    private void searchHistory() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadHistory();
            return;
        }

        String sql = "SELECT action, student_id, timestamp FROM update_logs " +
                "WHERE action LIKE '%" + keyword + "%' " +
                "OR student_id LIKE '%" + keyword + "%' " +
                "OR timestamp LIKE '%" + keyword + "%' " +
                "ORDER BY timestamp DESC";

        runQuery(sql);
    }

    private void runQuery(String sql) {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String action = rs.getString("action");
                String studentID = rs.getString("student_id") != null ? rs.getString("student_id") : "N/A";
                String time = rs.getString("timestamp");

                String details = switch (action) {
                    case "Added" -> "Added new student ID: " + studentID;
                    case "Updated" -> "Updated student ID: " + studentID;
                    case "Deleted" -> "Deleted student ID: " + studentID;
                    default -> "Performed action on student ID: " + studentID;
                };

                model.addRow(new Object[] { action, details, time });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading history: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}