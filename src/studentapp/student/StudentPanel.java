package studentapp.student;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import studentapp.database.DatabaseConnection;

// PDF (iText)
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class StudentPanel extends JPanel {

    private JTextField txtId, txtName, txtCourse, txtYear;
    private JTable table;
    private DefaultTableModel model;

    // 🎨 COLOR SYSTEM
    private static final Color PRIMARY = new Color(52, 152, 219);
    private static final Color SUCCESS = new Color(46, 204, 113);
    private static final Color WARNING = new Color(241, 196, 15);
    private static final Color DANGER  = new Color(231, 76, 60);
    private static final Color DARK    = new Color(44, 62, 80);

    public StudentPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        loadFont();

        // ---------- TITLE ----------
        JLabel title = new JLabel("Student Records");
        title.setFont(new Font("Poppins", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 10));
        add(title, BorderLayout.NORTH);

        // ---------- CENTER ----------
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        centerPanel.setBackground(Color.WHITE);

        // ---------- FORM PANEL ----------
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        txtId = createTextField("Student ID");
        txtName = createTextField("Full Name");
        txtCourse = createTextField("Course");
        txtYear = createTextField("Year Level");

        formPanel.add(txtId);
        formPanel.add(txtName);
        formPanel.add(txtCourse);
        formPanel.add(txtYear);
        formPanel.add(Box.createVerticalStrut(15));

        // ---------- BUTTONS ----------
        JButton addBtn    = createButton("➕ Add Student", SUCCESS);
        JButton updateBtn = createButton("✏ Update Student", PRIMARY);
        JButton deleteBtn = createButton("🗑 Delete Student", DANGER);
        JButton clearBtn  = createButton("🧹 Clear Fields", WARNING);

        JButton exportCsvBtn = createButton("⬇ Export CSV", DARK);
        JButton exportPdfBtn = createButton("⬇ Export PDF", DARK);

        formPanel.add(addBtn);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(updateBtn);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(deleteBtn);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(clearBtn);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(exportCsvBtn);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(exportPdfBtn);

        // ---------- TABLE ----------
        model = new DefaultTableModel(new String[]{"ID", "Name", "Course", "Year"}, 0);
        table = new JTable(model);

        table.setFont(new Font("Poppins", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 14));
        table.setRowHeight(28);
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);

        table.setDefaultEditor(Object.class, null);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        centerPanel.add(formPanel);
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // ---------- ACTIONS ----------
        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearFields());
        exportCsvBtn.addActionListener(e -> exportCSV());
        exportPdfBtn.addActionListener(e -> exportPDF());

        table.getSelectionModel().addListSelectionListener(e -> loadSelectedRow());

        loadStudents();
    }

    // ---------- UI HELPERS ----------
    private JTextField createTextField(String title) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Poppins", Font.PLAIN, 15));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        tf.setBorder(BorderFactory.createTitledBorder(title));
        return tf;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bg.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    private void loadFont() {
        try {
            Font pop = Font.createFont(Font.TRUETYPE_FONT, new File("Poppins-Regular.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pop);
        } catch (Exception ignored) {}
    }

    // ---------- DATABASE ----------
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
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void addStudent() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String course = txtCourse.getText().trim();
        String year = txtYear.getText().trim();

        if (id.isEmpty() || name.isEmpty() || year.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        if (!id.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Student ID must be numeric.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
    INSERT INTO students (student_id, fullname, course, year, created_at)
    VALUES (?, ?, ?, ?, NOW())
""";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, course);
            ps.setString(4, year);
            ps.executeUpdate();

            logUpdate(conn, id, "Added");

            JOptionPane.showMessageDialog(this, "Student added successfully!");
            loadStudents();
            clearFields();

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Student ID already exists.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void updateStudent() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE students SET fullname=?, course=?, year=? WHERE student_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtName.getText());
            ps.setString(2, txtCourse.getText());
            ps.setString(3, txtYear.getText());
            ps.setString(4, txtId.getText());
            ps.executeUpdate();

            logUpdate(conn, txtId.getText(), "Updated");

            JOptionPane.showMessageDialog(this, "Student updated!");
            loadStudents();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void deleteStudent() {
        if (txtId.getText().isEmpty()) return;

        if (JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm",
                JOptionPane.YES_NO_OPTION) != 0) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM students WHERE student_id=?");
            ps.setString(1, txtId.getText());
            ps.executeUpdate();

            logUpdate(conn, txtId.getText(), "Deleted");

            JOptionPane.showMessageDialog(this, "Student deleted.");
            loadStudents();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void logUpdate(Connection conn, String id, String action) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO update_logs(student_id, action, timestamp) VALUES (?, ?, NOW())");
        ps.setString(1, id);
        ps.setString(2, action);
        ps.executeUpdate();
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
        table.clearSelection();
    }

    private void exportCSV() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("students.csv"));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            FileWriter fw = new FileWriter(chooser.getSelectedFile());
            for (int i = 0; i < table.getColumnCount(); i++) {
                fw.append(table.getColumnName(i)).append(",");
            }
            fw.append("\n");

            for (int r = 0; r < table.getRowCount(); r++) {
                for (int c = 0; c < table.getColumnCount(); c++) {
                    fw.append(table.getValueAt(r, c).toString()).append(",");
                }
                fw.append("\n");
            }
            fw.close();

            JOptionPane.showMessageDialog(this, "CSV exported!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void exportPDF() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("students.pdf"));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            PdfWriter.getInstance(doc, new FileOutputStream(chooser.getSelectedFile()));
            doc.open();

            PdfPTable pdfTable = new PdfPTable(4);
            pdfTable.addCell("ID");
            pdfTable.addCell("Name");
            pdfTable.addCell("Course");
            pdfTable.addCell("Year");

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    pdfTable.addCell(model.getValueAt(i, j).toString());
                }
            }

            doc.add(pdfTable);
            doc.close();

            JOptionPane.showMessageDialog(this, "PDF exported!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
