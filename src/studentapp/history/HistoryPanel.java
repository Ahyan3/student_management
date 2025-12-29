package studentapp.history;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import studentapp.database.DatabaseConnection;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class HistoryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> filterBox;
    private JTextField searchField;

    private static final Color PRIMARY = new Color(41, 128, 185);   // darker blue
    private static final Color SUCCESS = new Color(39, 174, 96);    // darker green
    private static final Color DANGER  = new Color(192, 57, 43);    // darker red


    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

        // === HEADER: Title + Export Buttons ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel title = new JLabel("System Activity History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(44, 62, 80));
        header.add(title, BorderLayout.WEST);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        exportPanel.setOpaque(false);

        JButton pdfBtn = createStyledButton("Export PDF", DANGER);
        JButton csvBtn = createStyledButton("Export CSV", SUCCESS);

        pdfBtn.addActionListener(e -> exportToPDF());
        csvBtn.addActionListener(e -> exportToCSV());

        exportPanel.add(pdfBtn);
        exportPanel.add(csvBtn);

        header.add(exportPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // === MAIN CARD ===
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(40, 40, 40, 40)));

        // === TOP CONTROL BAR: Filter + Search + Refresh ===
        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        controlBar.setBackground(Color.WHITE);
        controlBar.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel filterLabel = new JLabel("Filter by Action:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        filterBox = new JComboBox<>(new String[] { "All", "Added", "Updated", "Deleted" });
        filterBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        filterBox.setPreferredSize(new Dimension(180, 40));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchField = new JTextField(30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(350, 44));


        controlBar.add(filterLabel);
        controlBar.add(filterBox);
        controlBar.add(Box.createHorizontalStrut(30));
        controlBar.add(searchLabel);
        controlBar.add(searchField);
        controlBar.add(Box.createHorizontalStrut(20));

        card.add(controlBar, BorderLayout.NORTH);

        // === TABLE ===
        model = new DefaultTableModel(new String[] { "Action", "Details", "Date & Time" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(48);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        // === ACTIONS ===
        filterBox.addActionListener(e -> applyFiltersAndSearch());
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFiltersAndSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFiltersAndSearch(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFiltersAndSearch(); }
        });

        loadHistory(); // Initial load
    }

private JButton createStyledButton(String text, Color bg) {
    JButton btn = new JButton(text);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
    btn.setForeground(Color.WHITE);
    btn.setBackground(bg);

    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setContentAreaFilled(false);
    btn.setOpaque(true);

    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setBorder(new EmptyBorder(14, 30, 14, 30));

    Color hoverColor = bg.darker(); // 🔥 darker hover

    btn.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            btn.setBackground(hoverColor);
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

    private void applyFiltersAndSearch() {
        String selectedAction = (String) filterBox.getSelectedItem();
        String keyword = searchField.getText().trim();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT action, student_id, timestamp FROM update_logs WHERE 1=1");

        if (!"All".equals(selectedAction)) {
            sql.append(" AND action = '").append(selectedAction).append("'");
        }
        if (!keyword.isEmpty()) {
            sql.append(" AND (action LIKE '%").append(keyword).append("%'")
              .append(" OR student_id LIKE '%").append(keyword).append("%'")
              .append(" OR timestamp LIKE '%").append(keyword).append("%')");
        }

        sql.append(" ORDER BY timestamp DESC");
        runQuery(sql.toString());
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

    // === EXPORT TO CSV ===
    private void exportToCSV() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = "activity_history_" + timestamp + ".csv";

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fileName));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                fw.append("Action,Details,Date & Time\n");
                for (int i = 0; i < model.getRowCount(); i++) {
                    fw.append(model.getValueAt(i, 0).toString()).append(",");
                    fw.append("\"").append(model.getValueAt(i, 1).toString().replace("\"", "\"\"")).append("\"").append(",");
                    fw.append(model.getValueAt(i, 2).toString()).append("\n");
                }
            }
            JOptionPane.showMessageDialog(this, "History exported to CSV successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "CSV Export Error: " + ex.getMessage());
        }
    }

    // === EXPORT TO PDF ===
    private void exportToPDF() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = "activity_history_" + timestamp + ".pdf";

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fileName));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(chooser.getSelectedFile()));
            document.open();

            PdfPTable pdfTable = new PdfPTable(3);
            pdfTable.addCell("Action");
            pdfTable.addCell("Details");
            pdfTable.addCell("Date & Time");

            for (int i = 0; i < model.getRowCount(); i++) {
                pdfTable.addCell(model.getValueAt(i, 0).toString());
                pdfTable.addCell(model.getValueAt(i, 1).toString());
                pdfTable.addCell(model.getValueAt(i, 2).toString());
            }

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(this, "History exported to PDF successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "PDF Export Error: " + ex.getMessage());
        }
    }

    public void refresh() {
        loadHistory();
    }
}