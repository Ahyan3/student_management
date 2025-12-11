package studentapp.student;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
// import javax.swing.text.Document;

import studentapp.database.DatabaseConnection;

// PDF (iText)
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Paragraph;


public class StudentPanel extends JPanel {

    private JTextField txtId, txtName, txtCourse, txtYear;
    private JTable table;
    private DefaultTableModel model;

    public StudentPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        loadFont();

        // ---------- TITLE ----------
        JLabel title = new JLabel("Student Records");
        title.setFont(new Font("Poppins", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        add(title, BorderLayout.NORTH);

        // ---------- CENTER FORM + TABLE ----------
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.setBackground(Color.WHITE);

        // ---------- LEFT SIDE FORM ----------
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtId = createTextField("Student ID");
        txtName = createTextField("Full Name");
        txtCourse = createTextField("Course");
        txtYear = createTextField("Year Level");

        formPanel.add(txtId);
        formPanel.add(txtName);
        formPanel.add(txtCourse);
        formPanel.add(txtYear);
        formPanel.add(Box.createVerticalStrut(15));

        JButton addBtn = createButton("Add Student");
        JButton updateBtn = createButton("Update Student");
        JButton deleteBtn = createButton("Delete Student");
        JButton clearBtn = createButton("Clear Fields");

        JButton exportCsvBtn = createButton("Export CSV");      // NEW
        JButton exportPdfBtn = createButton("Export PDF");      // NEW

        formPanel.add(addBtn);
        formPanel.add(updateBtn);
        formPanel.add(deleteBtn);
        formPanel.add(clearBtn);
        formPanel.add(Box.createVerticalStrut(15));             // spacing
        formPanel.add(exportCsvBtn);                            // NEW
        formPanel.add(exportPdfBtn);                            // NEW

        // ---------- TABLE ----------
        model = new DefaultTableModel(new String[]{"ID", "Name", "Course", "Year"}, 0);
        table = new JTable(model);

        table.setRowHeight(25);
        table.setFont(new Font("Poppins", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 14));

        // ====== TABLE LOCKED (NO EDIT, NO RESIZE, NO REORDER) ====== // NEW
        table.setDefaultEditor(Object.class, null);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        centerPanel.add(formPanel);
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // ---------- BUTTON ACTIONS ----------
        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearFields());

        exportCsvBtn.addActionListener(e -> exportCSV());       // NEW
        exportPdfBtn.addActionListener(e -> exportPDF());       // NEW

        // Table click → load values
        table.getSelectionModel().addListSelectionListener(x -> loadSelectedRow());

        loadStudents();
    }

    private void loadFont() {
        try {
            Font pop = Font.createFont(Font.TRUETYPE_FONT,
                    new java.io.File("Poppins-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pop);
        } catch (Exception e) {
            System.out.println("Poppins font not loaded.");
        }
    }

    private JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Poppins", Font.PLAIN, 16));
        tf.setPreferredSize(new Dimension(260, 40));
        tf.setBorder(BorderFactory.createTitledBorder(placeholder));
        return tf;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setMaximumSize(new Dimension(300, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private void loadStudents() {
        model.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM students")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("student_id"),
                        rs.getString("fullname"),
                        rs.getString("course"),
                        rs.getString("year")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }

private void addStudent() {
    String id = txtId.getText().trim();
    String name = txtName.getText().trim();
    String course = txtCourse.getText().trim();
    String year = txtYear.getText().trim();

    // -------------------------
    // VALIDATION SECTION
    // -------------------------

    // Empty Student ID
    if (id.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter your Student ID / Student Number.");
        return;
    }

    // Validate Student ID (only numbers – you can modify)
    if (!id.matches("\\d+")) {
        JOptionPane.showMessageDialog(this, "Invalid Student ID!\nStudent ID must contain numbers only.");
        return;
    }

    // Empty Name
    if (name.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter the student's full name.");
        return;
    }

    // Empty or invalid Year Level
    if (year.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Enter your Year Level, please.");
        return;
    }

    // Validate Year (allow 1,2,3,4 or 1st, 2nd, 3rd, 4th)
    if (!year.matches("(1|2|3|4|1st|2nd|3rd|4th)")) {
        JOptionPane.showMessageDialog(this,
            "Invalid Year Level!\nValid values: 1, 2, 3, 4");
        return;
    }

    // -------------------------
    // INSERT TO DATABASE
    // -------------------------

    try (Connection conn = DatabaseConnection.getConnection()) {
        String sql = "INSERT INTO students(student_id, fullname, course, year) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, id);
        ps.setString(2, name);
        ps.setString(3, course);
        ps.setString(4, year);
        ps.executeUpdate();

        // Insert log
        logUpdate(conn, id, "Added");

        JOptionPane.showMessageDialog(this, "Student Added Successfully!");
        loadStudents();
        clearFields();

    } catch (SQLIntegrityConstraintViolationException e) {
        // Duplicate Student ID error
        JOptionPane.showMessageDialog(this, 
            "Student ID already exists!\nPlease use a unique Student Number.");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error adding student: " + e.getMessage());
    }
}


    private void updateStudent() {
        String id = txtId.getText();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE students SET fullname=?, course=?, year=? WHERE student_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, txtName.getText());
            ps.setString(2, txtCourse.getText());
            ps.setString(3, txtYear.getText());
            ps.setString(4, id);
            ps.executeUpdate();

            // ----- INSERT LOG (UPDATE) ----- // NEW
            logUpdate(conn, id, "Updated");

            JOptionPane.showMessageDialog(this, "Student Updated!");
            loadStudents();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating student: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        String id = txtId.getText();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a student to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete student?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm != 0) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM students WHERE student_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.executeUpdate();

            // ----- INSERT LOG (DELETE) ----- // NEW
            logUpdate(conn, id, "Deleted");

            JOptionPane.showMessageDialog(this, "Student Deleted!");
            loadStudents();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting: " + e.getMessage());
        }
    }

    // ---------- INSERT UPDATE LOG ---------
    private void logUpdate(Connection conn, String studentId, String action) {
        try {
            String logSql = "INSERT INTO update_logs(student_id, action, timestamp) VALUES (?, ?, NOW())";
            PreparedStatement ps = conn.prepareStatement(logSql);
            ps.setString(1, studentId);
            ps.setString(2, action);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Log Error: " + e.getMessage());
        }
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        txtId.setText(model.getValueAt(row, 0).toString());
        txtName.setText(model.getValueAt(row, 1).toString());
        txtCourse.setText(model.getValueAt(row, 2).toString());
        txtYear.setText(model.getValueAt(row, 3).toString());
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtCourse.setText("");
        txtYear.setText("");

        // RESET TABLE SELECTION // NEW
        table.clearSelection();
        table.getSelectionModel().clearSelection();
        table.repaint();
    }

    // ---------- EXPORT CSV ---------- // NEW
    private void exportCSV() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("students.csv"));

            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                FileWriter fw = new FileWriter(chooser.getSelectedFile());

                for (int i = 0; i < table.getColumnCount(); i++) {
                    fw.append(table.getColumnName(i));
                    fw.append(i == table.getColumnCount() - 1 ? "\n" : ",");
                }

                for (int r = 0; r < table.getRowCount(); r++) {
                    for (int c = 0; c < table.getColumnCount(); c++) {
                        fw.append(String.valueOf(table.getValueAt(r, c)));
                        fw.append(c == table.getColumnCount() - 1 ? "\n" : ",");
                    }
                }

                fw.flush();
                fw.close();
                JOptionPane.showMessageDialog(this, "CSV Exported!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "CSV Export Error: " + e.getMessage());
        }
    }


// ---------- EXPORT PDF ----------
private void exportPDF() {
    try {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("students.pdf"));

        int result = chooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            return; // user cancelled
        }

        File file = chooser.getSelectedFile();

        // Create document
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        PdfWriter.getInstance(document, new FileOutputStream(file.getAbsolutePath() + ".pdf"));

        document.open(); // MUST open before adding content

        // Create PDF table (columns match your table)
        PdfPTable tablePDF = new PdfPTable(4);
        tablePDF.addCell("ID");
        tablePDF.addCell("Name");
        tablePDF.addCell("Course");
        tablePDF.addCell("Year");

        // Fill the PDF table
        for (int i = 0; i < model.getRowCount(); i++) {
            tablePDF.addCell(model.getValueAt(i, 0).toString());
            tablePDF.addCell(model.getValueAt(i, 1).toString());
            tablePDF.addCell(model.getValueAt(i, 2).toString());
            tablePDF.addCell(model.getValueAt(i, 3).toString());
        }

        // Add table to document
        document.add(tablePDF);

        document.close(); // close properly

        JOptionPane.showMessageDialog(this, "PDF exported successfully!");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "PDF export failed: " + e.getMessage());
        e.printStackTrace();
    }
}

}
