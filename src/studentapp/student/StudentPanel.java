package studentapp.student;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import studentapp.database.DatabaseConnection;

public class StudentPanel extends JPanel {

    private JTextField txtId, txtName, txtCourse;
    private JComboBox<String> cmbYear;
    private JTable table;
    private DefaultTableModel model;

    private static final Color PRIMARY = new Color(52, 152, 219);
    private static final Color SUCCESS = new Color(46, 204, 113);
    private static final Color WARNING = new Color(241, 196, 15);
    private static final Color DANGER = new Color(231, 76, 60);

    private JLabel tblTitle;

    public StudentPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 244, 248));

        JLabel title = new JLabel("Students Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(new EmptyBorder(40, 50, 20, 50));
        add(title, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);

        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(new Color(240, 244, 248));
        main.setBorder(new EmptyBorder(20, 40, 50, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // FORM CARD
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(40, 40, 40, 40)));
        formCard.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));

        formCard.add(createVerticalField("Student ID:", txtId = new JTextField()));
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(createVerticalField("Full Name:", txtName = new JTextField()));
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(createVerticalField("Course:", txtCourse = new JTextField()));
        formCard.add(Box.createVerticalStrut(20));

        // Year Level Dropdown
        JPanel yearWrapper = new JPanel(new BorderLayout());
        yearWrapper.setOpaque(false);
        yearWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel yearInner = new JPanel();
        yearInner.setLayout(new BoxLayout(yearInner, BoxLayout.Y_AXIS));
        yearInner.setOpaque(false);

        JLabel yearLabel = new JLabel("Year Level:");
        yearLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        yearLabel.setForeground(new Color(70, 70, 70));
        yearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        cmbYear = new JComboBox<>(new String[] { "1st Year", "2nd Year", "3rd Year", "4th Year" });
        cmbYear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        cmbYear.setPreferredSize(new Dimension(350, 52));
        cmbYear.setAlignmentX(Component.CENTER_ALIGNMENT);

        yearInner.add(yearLabel);
        yearInner.add(Box.createVerticalStrut(8));
        yearInner.add(cmbYear);
        yearWrapper.add(yearInner, BorderLayout.CENTER);

        formCard.add(yearWrapper);
        formCard.add(Box.createVerticalStrut(40));

        // Buttons - 3 rows × 2 columns
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        buttonPanel.setOpaque(false);

        JButton addBtn = createBtn("Add", SUCCESS);
        JButton updateBtn = createBtn("Update", PRIMARY);
        JButton clearBtn = createBtn("Clear", WARNING);
        JButton deleteBtn = createBtn("Delete", DANGER);
        JButton pdfBtn = createBtn("Export PDF", new Color(220, 53, 69));
        JButton csvBtn = createBtn("Export CSV", new Color(40, 167, 69));

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(pdfBtn);
        buttonPanel.add(csvBtn);

        formCard.add(Box.createVerticalStrut(20));
        formCard.add(buttonPanel);
        formCard.add(Box.createVerticalStrut(40));

        gbc.gridx = 0;
        gbc.weightx = 0.4;
        gbc.insets = new Insets(0, 0, 0, 40);
        main.add(formCard, gbc);

        // TABLE CARD
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(40, 40, 40, 40)));

        tblTitle = new JLabel("Student List");
        tblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tblTitle.setForeground(new Color(44, 62, 80));
        tblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        tableCard.add(tblTitle, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[] { "#", "ID", "Name", "Course", "Year" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.setSelectionBackground(PRIMARY);
        table.setGridColor(new Color(230, 230, 230));

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tableCard.add(tableScroll, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        main.add(tableCard, gbc);

        scrollPane.setViewportView(main);
        add(scrollPane, BorderLayout.CENTER);

        // ACTIONS WITH LOGGING
        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearFields());
        pdfBtn.addActionListener(e -> exportPDF());
        csvBtn.addActionListener(e -> exportCSV());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                loadSelected();
        });

        loadStudents();
    }

    private JPanel createVerticalField(String labelText, JTextField field) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(70, 70, 70));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        field.setPreferredSize(new Dimension(350, 52));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(12, 16, 12, 16)));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(label);
        inner.add(Box.createVerticalStrut(8));
        inner.add(field);
        wrapper.add(inner, BorderLayout.CENTER);

        return wrapper;
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(16, 32, 16, 32));
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

    // LOG ACTION TO update_logs TABLE
    private void logAction(String action, String studentId) {
        String sql = "INSERT INTO update_logs (action, student_id, timestamp) VALUES (?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, action);
            ps.setString(2, studentId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Failed to log action: " + e.getMessage());
        }
    }

    private void loadStudents() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM students ORDER BY fullname")) {

            int counter = 1;
            while (rs.next()) {
                model.addRow(new Object[] {
                        counter++,
                        rs.getString("student_id"),
                        rs.getString("fullname"),
                        rs.getString("course"),
                        rs.getString("year")
                });
            }
            tblTitle.setText("Student List (" + (counter - 1) + ")");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }

    private void addStudent() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String course = txtCourse.getText().trim();
        String year = (String) cmbYear.getSelectedItem();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID and Name are required.");
            return;
        }
        if (!id.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "ID must be numeric.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO students (student_id, fullname, course, year, created_at) VALUES (?, ?, ?, ?, NOW())")) {
            ps.setInt(1, Integer.parseInt(id));
            ps.setString(2, name);
            ps.setString(3, course);
            ps.setString(4, year);
            ps.executeUpdate();

            logAction("Added", id); // ← LOG ADDED
            JOptionPane.showMessageDialog(this, "Student added successfully!");
            clearFields();
            loadStudents();

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "ID already exists.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void updateStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }

        String id = txtId.getText().trim();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE students SET fullname=?, course=?, year=? WHERE student_id=?")) {
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, txtCourse.getText().trim());
            ps.setString(3, (String) cmbYear.getSelectedItem());
            ps.setInt(4, Integer.parseInt(id));
            int updated = ps.executeUpdate();

            if (updated > 0) {
                logAction("Updated", id); // ← LOG UPDATED
                JOptionPane.showMessageDialog(this, "Student updated!");
                loadStudents();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;

        String id = txtId.getText().trim();

        if (JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE student_id=?")) {
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();

            logAction("Deleted", id); // ← LOG DELETED
            JOptionPane.showMessageDialog(this, "Student deleted.");
            loadStudents();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void loadSelected() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;

        txtId.setText(model.getValueAt(row, 1).toString());
        txtName.setText(model.getValueAt(row, 2).toString());
        txtCourse.setText(model.getValueAt(row, 3).toString());
        cmbYear.setSelectedItem(model.getValueAt(row, 4));
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtCourse.setText("");
        cmbYear.setSelectedIndex(0);
        table.clearSelection();
    }

    private void exportCSV() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = "students_" + timestamp + ".csv";

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fileName));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
                return;

            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                fw.append("Student ID,Full Name,Course,Year Level\n");
                for (int i = 0; i < model.getRowCount(); i++) {
                    fw.append(model.getValueAt(i, 1).toString()).append(",");
                    fw.append(model.getValueAt(i, 2).toString()).append(",");
                    fw.append(model.getValueAt(i, 3).toString()).append(",");
                    fw.append(model.getValueAt(i, 4).toString()).append("\n");
                }
            }
            JOptionPane.showMessageDialog(this, "Exported to CSV: " + chooser.getSelectedFile().getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "CSV Error: " + ex.getMessage());
        }
    }

    private void exportPDF() {
        JOptionPane.showMessageDialog(this, "PDF Export disabled — add iTextPDF library to enable.");

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = "students_" + timestamp + ".pdf";

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fileName));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
                return;

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(chooser.getSelectedFile()));
            document.open();

            PdfPTable pdfTable = new PdfPTable(4);
            pdfTable.addCell("Student ID");
            pdfTable.addCell("Full Name");
            pdfTable.addCell("Course");
            pdfTable.addCell("Year Level");

            for (int i = 0; i < model.getRowCount(); i++) {
                pdfTable.addCell(model.getValueAt(i, 1).toString());
                pdfTable.addCell(model.getValueAt(i, 2).toString());
                pdfTable.addCell(model.getValueAt(i, 3).toString());
                pdfTable.addCell(model.getValueAt(i, 4).toString());
            }

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(this,
                    "Students exported to PDF successfully!\nFile: " + chooser.getSelectedFile().getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting PDF: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}