package studentapp.student;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import studentapp.database.DatabaseConnection;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class GradesOverviewPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    private static final Color PRIMARY = new Color(41, 128, 185);   // darker blue
    private static final Color SUCCESS = new Color(39, 174, 96);    // darker green
    private static final Color DANGER  = new Color(192, 57, 43);    // darker red

    private JLabel tblTitle;

    public GradesOverviewPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // === HEADER: Title + Export Buttons (Top Right) ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel title = new JLabel("Student Grades");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(44, 62, 80));
        header.add(title, BorderLayout.WEST);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        exportPanel.setOpaque(false);

        JButton pdfBtn = createButton("Export PDF", DANGER);
        JButton csvBtn = createButton("Export CSV", SUCCESS);

        exportPanel.add(pdfBtn);
        exportPanel.add(csvBtn);

        pdfBtn.addActionListener(e -> exportToPDF());
        csvBtn.addActionListener(e -> exportToCSV());

        header.add(exportPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // === MAIN CARD ===
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(40, 40, 40, 40)));

        // === TOP BAR: Title + Search Only ===
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 30, 0));

        tblTitle = new JLabel("Student Grades");
        tblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tblTitle.setForeground(new Color(44, 62, 80));
        topBar.add(tblTitle, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(350, 44));
        searchPanel.add(searchField);

        topBar.add(searchPanel, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);

        // === TABLE ===
        String[] columns = { "#", "ID", "Full Name", "Course", "Year", "Semester", "Block",
                "Prelim", "Midterm", "Prefinal", "Finals", "Average", "Remarks", "Action" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 13; // Only Action column
            }
        };

        table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 13; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(0).setMaxWidth(80);

        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new GradeButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        // Live search as user types
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchGrades(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchGrades(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchGrades(); }
        });

        loadAllGrades(); // Initial load
    }
private JButton createButton(String text, Color bg) {
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
    private void loadAllGrades() {
        searchGrades(); // Reuse search logic with empty keyword
    }

    private void searchGrades() {
        String keyword = searchField.getText().toLowerCase().trim();

        model.setRowCount(0);

        String sql = """
                SELECT s.student_id, s.fullname, s.course, s.year, s.semester, s.block,
                       g.prelim, g.midterm, g.prefinal, g.finals
                FROM students s
                LEFT JOIN grades g ON s.student_id = g.student_id
                ORDER BY s.fullname
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            int counter = 1;
            while (rs.next()) {
                String id = rs.getString("student_id");
                String name = rs.getString("fullname");
                String course = rs.getString("course");
                String year = rs.getString("year");
                String semester = rs.getString("semester");
                String block = rs.getString("block");

                String prelim = rs.getString("prelim");
                String midterm = rs.getString("midterm");
                String prefinal = rs.getString("prefinal");
                String finals = rs.getString("finals");

                double avg = calculateAverage(prelim, midterm, prefinal, finals);
                String average = avg >= 0 ? String.format("%.2f", avg) : "-";
                String remarks = avg >= 75 ? "Passed" : (avg >= 0 ? "Failed" : "-");

                String lowerName = name.toLowerCase();
                String lowerId = id.toLowerCase();
                String lowerCourse = course == null ? "" : course.toLowerCase();
                String lowerYear = year == null ? "" : year.toLowerCase();
                String lowerSemester = semester == null ? "" : semester.toLowerCase();
                String lowerBlock = block == null ? "" : block.toLowerCase();
                String lowerPrelim = prelim == null ? "" : prelim.toLowerCase();
                String lowerMidterm = midterm == null ? "" : midterm.toLowerCase();
                String lowerPrefinal = prefinal == null ? "" : prefinal.toLowerCase();
                String lowerFinals = finals == null ? "" : finals.toLowerCase();
                String lowerAvg = average.toLowerCase();
                String lowerRemarks = remarks.toLowerCase();

                if (keyword.isEmpty() ||
                    lowerName.contains(keyword) ||
                    lowerId.contains(keyword) ||
                    lowerCourse.contains(keyword) ||
                    lowerYear.contains(keyword) ||
                    lowerSemester.contains(keyword) ||
                    lowerBlock.contains(keyword) ||
                    lowerPrelim.contains(keyword) ||
                    lowerMidterm.contains(keyword) ||
                    lowerPrefinal.contains(keyword) ||
                    lowerFinals.contains(keyword) ||
                    lowerAvg.contains(keyword) ||
                    lowerRemarks.contains(keyword)) {

                    model.addRow(new Object[] {
                        counter++,
                        id,
                        name,
                        course,
                        year,
                        semester != null ? semester : "-",
                        block != null ? block : "-",
                        prelim != null ? prelim : "-",
                        midterm != null ? midterm : "-",
                        prefinal != null ? prefinal : "-",
                        finals != null ? finals : "-",
                        average,
                        remarks,
                        "Edit Grades"
                    });
                }
            }
            tblTitle.setText("Student Grades (" + (counter - 1) + ")");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading grades: " + e.getMessage());
        }
    }

    private double calculateAverage(String p, String m, String pf, String f) {
        try {
            double prelim = p == null || p.isEmpty() || p.equals("-") ? 0 : Double.parseDouble(p);
            double midterm = m == null || m.isEmpty() || m.equals("-") ? 0 : Double.parseDouble(m);
            double prefinal = pf == null || pf.isEmpty() || pf.equals("-") ? 0 : Double.parseDouble(pf);
            double finals = f == null || f.isEmpty() || f.equals("-") ? 0 : Double.parseDouble(f);

            int count = 0;
            double sum = 0;
            if (prelim > 0) { sum += prelim; count++; }
            if (midterm > 0) { sum += midterm; count++; }
            if (prefinal > 0) { sum += prefinal; count++; }
            if (finals > 0) { sum += finals; count++; }

            return count > 0 ? sum / count : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private void exportToCSV() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = "grades_overview_" + timestamp + ".csv";

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fileName));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                // Header
                for (int i = 0; i < model.getColumnCount() - 1; i++) { // Exclude Action column
                    fw.append(model.getColumnName(i));
                    if (i < model.getColumnCount() - 2) fw.append(",");
                }
                fw.append("\n");

                // Data
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount() - 1; j++) { // Exclude Action
                        Object val = model.getValueAt(i, j);
                        fw.append(val == null ? "" : val.toString());
                        if (j < model.getColumnCount() - 2) fw.append(",");
                    }
                    fw.append("\n");
                }
            }
            JOptionPane.showMessageDialog(this, "Grades exported to CSV successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "CSV Export Error: " + ex.getMessage());
        }
    }

    private void exportToPDF() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = "grades_overview_" + timestamp + ".pdf";

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fileName));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(chooser.getSelectedFile()));
            document.open();

            PdfPTable pdfTable = new PdfPTable(13); // 13 columns (exclude Action)
            String[] headers = { "#", "ID", "Full Name", "Course", "Year", "Semester", "Block",
                    "Prelim", "Midterm", "Prefinal", "Finals", "Average", "Remarks" };
            for (String header : headers) pdfTable.addCell(header);

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < 13; j++) { // Exclude Action column
                    Object val = model.getValueAt(i, j);
                    pdfTable.addCell(val == null ? "-" : val.toString());
                }
            }

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(this, "Grades exported to PDF successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "PDF Export Error: " + ex.getMessage());
        }
    }

    // ButtonRenderer & GradeButtonEditor (keep exactly as you had them)
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(PRIMARY);
            setForeground(Color.WHITE);
            setText("Edit Grades");
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class GradeButtonEditor extends DefaultCellEditor {
        private JButton button;

        public GradeButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Edit Grades");
            button.setOpaque(true);
            button.setBackground(PRIMARY);
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String studentId = (String) model.getValueAt(row, 1);
                    GradePanel.showGradeDialog(studentId, GradesOverviewPanel.this);
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Edit Grades";
        }
    }

    public void refresh() {
        loadAllGrades();
    }
}