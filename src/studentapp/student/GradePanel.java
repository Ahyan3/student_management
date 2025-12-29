package studentapp.student;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent; 
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import studentapp.database.DatabaseConnection;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class GradePanel extends JPanel {

    private JTextField txtPrelim, txtMidterm, txtPrefinal, txtFinals;
    private String studentId;
    private String fullName;
    private String year;
    
    private static final Color PRIMARY = new Color(41, 128, 185);   // darker blue
    private static final Color SUCCESS = new Color(39, 174, 96);    // darker green
    private static final Color DANGER  = new Color(192, 57, 43);    // darker red


    public GradePanel(String studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Fetch student info
        fetchStudentInfo();

        // === HEADER: Title + Export Buttons ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(30, 40, 20, 40));

        String titleText = "Edit Grades - " + fullName + " (ID: " + studentId + ")" +
                (year != null && !year.isEmpty() ? ", " + year : "");

        JLabel title = new JLabel(titleText);
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

        // === FORM ===
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(50, 60, 50, 60)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtPrelim = createGradeField();
        txtMidterm = createGradeField();
        txtPrefinal = createGradeField();
        txtFinals = createGradeField();

        addFormRow(form, gbc, "Prelim Grade:", txtPrelim, 0);
        addFormRow(form, gbc, "Midterm Grade:", txtMidterm, 1);
        addFormRow(form, gbc, "Prefinal Grade:", txtPrefinal, 2);
        addFormRow(form, gbc, "Finals Grade:", txtFinals, 3);

        JButton saveBtn = createStyledButton("Save All Grades", PRIMARY);
        saveBtn.setPreferredSize(new Dimension(300, 56));
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(50, 0, 0, 0);
        form.add(saveBtn, gbc);

        add(form, BorderLayout.CENTER);

        loadGrades();
        saveBtn.addActionListener(e -> {
            saveGrades();
            JOptionPane.showMessageDialog(this, "Grades saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    private void fetchStudentInfo() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT fullname, year FROM students WHERE student_id = ?")) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                fullName = rs.getString("fullname");
                year = rs.getString("year");
            }
        } catch (Exception e) {
            fullName = "Unknown Student";
            year = "";
            System.err.println("Error fetching student info: " + e.getMessage());
        }
    }

    private JTextField createGradeField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 2),
                new EmptyBorder(14, 20, 14, 20)));
        return field;
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


    private void loadGrades() {
        txtPrelim.setText("");
        txtMidterm.setText("");
        txtPrefinal.setText("");
        txtFinals.setText("");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM grades WHERE student_id = ?")) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                setFieldText(txtPrelim, rs.getString("prelim"));
                setFieldText(txtMidterm, rs.getString("midterm"));
                setFieldText(txtPrefinal, rs.getString("prefinal"));
                setFieldText(txtFinals, rs.getString("finals"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading grades: " + e.getMessage());
        }
    }

    private void setFieldText(JTextField field, String value) {
        field.setText(value != null && !value.isEmpty() ? value : "");
    }

    private void saveGrades() {
        String prelim = txtPrelim.getText().trim().isEmpty() ? null : txtPrelim.getText().trim();
        String midterm = txtMidterm.getText().trim().isEmpty() ? null : txtMidterm.getText().trim();
        String prefinal = txtPrefinal.getText().trim().isEmpty() ? null : txtPrefinal.getText().trim();
        String finals = txtFinals.getText().trim().isEmpty() ? null : txtFinals.getText().trim();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO grades (student_id, prelim, midterm, prefinal, finals) " +
                             "VALUES (?, ?, ?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE " +
                             "prelim = VALUES(prelim), midterm = VALUES(midterm), " +
                             "prefinal = VALUES(prefinal), finals = VALUES(finals)")) {

            ps.setString(1, studentId);
            ps.setString(2, prelim);
            ps.setString(3, midterm);
            ps.setString(4, prefinal);
            ps.setString(5, finals);

            ps.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving grades: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === EXPORT TO CSV (Individual Student) ===
    private void exportToCSV() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String safeName = fullName.replaceAll("[^a-zA-Z0-9]", "_");
            String fileName = "grades_" + safeName + "_" + studentId + "_" + timestamp + ".csv";

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fileName));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                fw.append("Student Name,").append(fullName).append("\n");
                fw.append("Student ID,").append(studentId).append("\n");
                fw.append("Year Level,").append(year != null ? year : "-").append("\n\n");

                fw.append("Grade Item,Score\n");
                fw.append("Prelim,").append(txtPrelim.getText().trim().isEmpty() ? "-" : txtPrelim.getText().trim()).append("\n");
                fw.append("Midterm,").append(txtMidterm.getText().trim().isEmpty() ? "-" : txtMidterm.getText().trim()).append("\n");
                fw.append("Prefinal,").append(txtPrefinal.getText().trim().isEmpty() ? "-" : txtPrefinal.getText().trim()).append("\n");
                fw.append("Finals,").append(txtFinals.getText().trim().isEmpty() ? "-" : txtFinals.getText().trim()).append("\n");

                double avg = calculateAverage();
                fw.append("Average,").append(avg >= 0 ? String.format("%.2f", avg) : "-").append("\n");
                fw.append("Remarks,").append(avg >= 75 ? "Passed" : (avg >= 0 ? "Failed" : "-")).append("\n");
            }

            JOptionPane.showMessageDialog(this, "Grades exported to CSV successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "CSV Export Error: " + ex.getMessage());
        }
    }

    // === EXPORT TO PDF (Individual Student) ===
    private void exportToPDF() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String safeName = fullName.replaceAll("[^a-zA-Z0-9]", "_");
            String fileName = "grades_" + safeName + "_" + studentId + "_" + timestamp + ".pdf";

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fileName));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(chooser.getSelectedFile()));
            document.open();

            document.add(new Paragraph("Student Grades Report", new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD)));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Name: " + fullName));
            document.add(new Paragraph("Student ID: " + studentId));
            document.add(new Paragraph("Year Level: " + (year != null ? year : "-")));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(60);

            table.addCell("Grade Item");
            table.addCell("Score");

            table.addCell("Prelim");
            table.addCell(txtPrelim.getText().trim().isEmpty() ? "-" : txtPrelim.getText().trim());

            table.addCell("Midterm");
            table.addCell(txtMidterm.getText().trim().isEmpty() ? "-" : txtMidterm.getText().trim());

            table.addCell("Prefinal");
            table.addCell(txtPrefinal.getText().trim().isEmpty() ? "-" : txtPrefinal.getText().trim());

            table.addCell("Finals");
            table.addCell(txtFinals.getText().trim().isEmpty() ? "-" : txtFinals.getText().trim());

            double avg = calculateAverage();
            table.addCell("Average");
            table.addCell(avg >= 0 ? String.format("%.2f", avg) : "-");

            table.addCell("Remarks");
            table.addCell(avg >= 75 ? "Passed" : (avg >= 0 ? "Failed" : "-"));

            document.add(table);
            document.close();

            JOptionPane.showMessageDialog(this, "Grades exported to PDF successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "PDF Export Error: " + ex.getMessage());
        }
    }

    private double calculateAverage() {
        try {
            double p = txtPrelim.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtPrelim.getText().trim());
            double m = txtMidterm.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtMidterm.getText().trim());
            double pf = txtPrefinal.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtPrefinal.getText().trim());
            double f = txtFinals.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtFinals.getText().trim());

            int count = 0;
            double sum = 0;
            if (p > 0) { sum += p; count++; }
            if (m > 0) { sum += m; count++; }
            if (pf > 0) { sum += pf; count++; }
            if (f > 0) { sum += f; count++; }

            return count > 0 ? sum / count : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public static void showGradeDialog(String studentId, GradesOverviewPanel overviewPanel) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(overviewPanel),
                "Edit Grades", true);

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(overviewPanel);

        GradePanel gradePanel = new GradePanel(studentId);
        dialog.add(gradePanel);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                overviewPanel.refresh(); // Now calls the correct refresh method
            }
        });

        dialog.setVisible(true);
    }
}