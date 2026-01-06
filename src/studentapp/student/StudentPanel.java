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
import javax.swing.border.AbstractBorder;

public class StudentPanel extends JPanel {

    private JTextField txtId, txtName, txtCourse;
    private JComboBox<String> cmbYear, cmbSemester, cmbBlock;
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;

private static final Color PRIMARY = new Color(41, 128, 185);   // darker blue
private static final Color SUCCESS = new Color(39, 174, 96);    // darker green
private static final Color DANGER  = new Color(192, 57, 43);    // darker red


    private JLabel tblTitle;

    class RoundedShadowBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(x + 6, y + 6, width - 12, height - 12, 20, 20);
            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillRoundRect(x + 4, y + 4, width - 8, height - 8, 24, 24);
            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(x, y, width - 1, height - 1, 20, 20);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(15, 15, 15, 15);
        }
    }

    public StudentPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

        // === HEADER ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel title = new JLabel("Students Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(44, 62, 80));
        header.add(title, BorderLayout.WEST);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        exportPanel.setOpaque(false);
        JButton pdfBtn = createButton("Export PDF", DANGER);
        JButton csvBtn = createButton("Export CSV", SUCCESS);
        exportPanel.add(pdfBtn);
        exportPanel.add(csvBtn);
        header.add(exportPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // === MAIN CONTENT ===
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(10, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // === LEFT: FORM (20%) ===
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setPreferredSize(new Dimension(420, 0));
        formCard.setMaximumSize(new Dimension(420, Integer.MAX_VALUE));
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(12, 12, 12, 12),
            BorderFactory.createCompoundBorder(
                new RoundedShadowBorder(),
                BorderFactory.createEmptyBorder(30, 35, 35, 35)
            )
        ));

        formCard.add(createTopLabeledField("Full Name", txtName = new JTextField()));
        formCard.add(createTopLabeledField("Student ID", txtId = new JTextField()));
        formCard.add(createTopLabeledField("Course     ", txtCourse = new JTextField())); 

        JPanel comboPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        comboPanel.setOpaque(false);
        comboPanel.add(createTopLabeledField("Year", cmbYear = new JComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year"})));
        comboPanel.add(createTopLabeledField("Sem", cmbSemester = new JComboBox<>(new String[]{"1st Semester", "2nd Semester", "Summer"})));
        comboPanel.add(createTopLabeledField("Block", cmbBlock = new JComboBox<>(new String[]{
            "Block A", "Block B", "Block C", "Block D", "Block E",
            "Block F", "Block G", "Block H", "Block I", "Block J"
        })));
        formCard.add(comboPanel);
        formCard.add(Box.createVerticalStrut(35));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setOpaque(false);
        JButton addBtn = createButton("Add", PRIMARY);
        JButton updateBtn = createButton("Update", PRIMARY);
        JButton clearBtn = createButton("Clear", PRIMARY);
        JButton deleteBtn = createButton("Delete", DANGER);
        buttonPanel.add(addBtn); buttonPanel.add(updateBtn);
        buttonPanel.add(clearBtn); buttonPanel.add(deleteBtn);
        formCard.add(buttonPanel);

        gbc.gridx = 0;
        gbc.weightx = 0.20;
        gbc.insets = new Insets(0, 0, 0, 40);
        main.add(formCard, gbc);

        // === RIGHT: STUDENT LIST (80%) ===
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(12, 12, 12, 12),
            BorderFactory.createCompoundBorder(
                new RoundedShadowBorder(),
                BorderFactory.createEmptyBorder(30, 40, 40, 40)
            )
        ));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 25, 0));

        tblTitle = new JLabel("Student List");
        tblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tblTitle.setForeground(new Color(44, 62, 80));
        topBar.add(tblTitle, BorderLayout.WEST);

        // Only Search — NO filters
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search:"));
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtSearch.setPreferredSize(new Dimension(320, 42));
        searchPanel.add(txtSearch);

        topBar.add(searchPanel, BorderLayout.EAST);
        tableCard.add(topBar, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[] { "#", "ID", "Name", "Course", "Year", "Semester", "Block" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(48);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.setSelectionBackground(PRIMARY);
        table.setGridColor(new Color(230, 230, 230));
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tableCard.add(tableScroll, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.weightx = 0.80;
        main.add(tableCard, gbc);

        add(main, BorderLayout.CENTER);

        // === LISTENERS ===
        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearFields());
        pdfBtn.addActionListener(e -> exportPDF());
        csvBtn.addActionListener(e -> exportCSV());

        // Only text search
        txtSearch.getDocument().addDocumentListener(docListener(this::searchTable));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelected();
        });

        loadStudents();
    }

    private javax.swing.event.DocumentListener docListener(Runnable action) {
        return new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
        };
    }

    private JPanel createTopLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(70, 70, 70));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (field instanceof JTextField) {
            JTextField tf = (JTextField) field;
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            tf.setPreferredSize(new Dimension(0, 48));
            tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
            ));
            tf.addFocusListener(focusListener(tf));
        } else if (field instanceof JComboBox) {
            JComboBox<?> cb = (JComboBox<?>) field;
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            cb.setPreferredSize(new Dimension(0, 48));
            cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
            ));
            cb.addFocusListener(focusListener(cb));
        }

        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);
        return panel;
    }

    private FocusListener focusListener(JComponent c) {
        return new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 3),
                    c instanceof JTextField ? BorderFactory.createEmptyBorder(9, 13, 9, 13) :
                                              BorderFactory.createEmptyBorder(9, 13, 9, 9)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                    c instanceof JTextField ? BorderFactory.createEmptyBorder(10, 14, 10, 14) :
                                              BorderFactory.createEmptyBorder(10, 14, 10, 10)
                ));
            }
        };
    }

    private JButton createButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(14, 30, 14, 30));
        //b.setPreferredSize(new Dimension(140, 42));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { b.setBackground(color.darker()); }
            @Override
            public void mouseExited(MouseEvent e) { b.setBackground(color); }
        });
        return b;
    }

    private void searchTable() {
        String search = txtSearch.getText().toLowerCase().trim();

        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM students ORDER BY fullname")) {

            int counter = 1;
            while (rs.next()) {
                String name = rs.getString("fullname");
                String lowerName = name.toLowerCase();
                String id = rs.getString("student_id");
                String course = rs.getString("course");
                String lowerCourse = course == null ? "" : course.toLowerCase();
                String year = rs.getString("year");
                String lowerYear = year == null ? "" : year.toLowerCase();
                String semester = rs.getString("semester");
                String lowerSemester = semester == null ? "" : semester.toLowerCase();
                String block = rs.getString("block");
                String lowerBlock = block == null ? "" : block.toLowerCase();

                // Search in ALL fields
                if (search.isEmpty() ||
                    lowerName.contains(search) ||
                    id.contains(search) ||
                    lowerCourse.contains(search) ||
                    lowerYear.contains(search) ||
                    lowerSemester.contains(search) ||
                    lowerBlock.contains(search)) {

                    model.addRow(new Object[]{
                        counter++,
                        id,
                        name,
                        course,
                        year,
                        semester,
                        block
                    });
                }
            }
            tblTitle.setText("Student List (" + (counter - 1) + ")");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStudents() {
        txtSearch.setText("");
        searchTable();
    }

    // === HELPER METHODS (unchanged from last version) ===
    private void styleSmallCombo(JComboBox<?> c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.setPreferredSize(new Dimension(120, 34));
    }

    private JPanel createSideLabeledInput(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(new Color(70, 70, 70));
        l.setPreferredSize(new Dimension(110, 44));

        if (field instanceof JTextField) {
            JTextField tf = (JTextField) field;
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
            ));
            tf.addFocusListener(focusListener(tf));
        } else if (field instanceof JComboBox) {
            JComboBox<?> cb = (JComboBox<?>) field;
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 10)
            ));
            cb.addFocusListener(focusListener(cb));
        }

        p.add(l, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void filterTable() {
        String search = txtSearch.getText().toLowerCase().trim();
        String year = (String) cmbFilterYear.getSelectedItem();
        String semester = (String) cmbFilterSemester.getSelectedItem();
        String block = (String) cmbFilterBlock.getSelectedItem();

        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM students ORDER BY fullname")) {

            int counter = 1;
            while (rs.next()) {
                String dbName = rs.getString("fullname").toLowerCase();
                String dbId = rs.getString("student_id");
                String dbCourse = rs.getString("course") == null ? "" : rs.getString("course").toLowerCase();
                String dbYear = rs.getString("year");
                String dbSemester = rs.getString("semester");
                String dbBlock = rs.getString("block");

                boolean matchSearch = search.isEmpty() || dbName.contains(search) || dbId.contains(search) || dbCourse.contains(search);
                boolean matchYear = year.equals("All Years") || dbYear.equals(year);
                boolean matchSem = semester.equals("All Semesters") || dbSemester.equals(semester);
                boolean matchBlock = block.equals("All Blocks") || dbBlock.equals(block);

                if (matchSearch && matchYear && matchSem && matchBlock) {
                    model.addRow(new Object[]{counter++, dbId, rs.getString("fullname"), rs.getString("course"),
                            dbYear, dbSemester, dbBlock});
                }
            }
            tblTitle.setText("Student List (" + (counter - 1) + ")");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Filter error: " + e.getMessage());
        }
    }

    private void logAction(String action, String studentId) { 
        String sql = "INSERT INTO update_logs (action, student_id, timestamp) VALUES (?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, action); ps.setString(2, studentId); ps.executeUpdate();
        } catch (Exception e) { System.err.println("Failed to log action: " + e.getMessage()); }
    }

    private void addStudent() { 
        String id = txtId.getText().trim(); String name = txtName.getText().trim(); String course = txtCourse.getText().trim();
        if (id.isEmpty() || name.isEmpty()) { JOptionPane.showMessageDialog(this, "ID and Name are required."); return; }
        if (!id.matches("\\d+")) { JOptionPane.showMessageDialog(this, "ID must be numeric."); return; }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO students (student_id, fullname, course, year, semester, block, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())")) {
            ps.setInt(1, Integer.parseInt(id)); ps.setString(2, name); ps.setString(3, course);
            ps.setString(4, (String) cmbYear.getSelectedItem()); ps.setString(5, (String) cmbSemester.getSelectedItem());
            ps.setString(6, (String) cmbBlock.getSelectedItem()); ps.executeUpdate();
            logAction("Added", id); JOptionPane.showMessageDialog(this, "Student added successfully!");
            clearFields(); loadStudents();
        } catch (SQLIntegrityConstraintViolationException e) { JOptionPane.showMessageDialog(this, "ID already exists."); }
          catch (Exception e) { JOptionPane.showMessageDialog(this, "Error adding student: " + e.getMessage()); }
    }

    private void updateStudent() { /* same */ 
        int row = table.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Select a student first."); return; }
        String originalId = model.getValueAt(row, 1).toString();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE students SET fullname=?, course=?, year=?, semester=?, block=? WHERE student_id=?")) {
            ps.setString(1, txtName.getText().trim()); ps.setString(2, txtCourse.getText().trim());
            ps.setString(3, (String) cmbYear.getSelectedItem()); ps.setString(4, (String) cmbSemester.getSelectedItem());
            ps.setString(5, (String) cmbBlock.getSelectedItem()); ps.setInt(6, Integer.parseInt(originalId));
            if (ps.executeUpdate() > 0) { logAction("Updated", originalId);
                JOptionPane.showMessageDialog(this, "Student updated successfully!"); loadStudents(); }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error updating: " + e.getMessage()); }
    }

    private void deleteStudent() { 
        int row = table.getSelectedRow(); if (row == -1) return; String id = txtId.getText().trim();
        if (JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE student_id=?")) {
            ps.setInt(1, Integer.parseInt(id)); ps.executeUpdate(); logAction("Deleted", id);
            JOptionPane.showMessageDialog(this, "Student deleted."); loadStudents(); clearFields();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void loadSelected() {
        int row = table.getSelectedRow(); if (row == -1) return;
        txtId.setText(model.getValueAt(row, 1).toString()); txtId.setEditable(false); txtId.setBackground(new Color(240,240,240));
        txtName.setText(model.getValueAt(row, 2).toString()); txtCourse.setText(model.getValueAt(row, 3).toString());
        cmbYear.setSelectedItem(model.getValueAt(row, 4)); cmbSemester.setSelectedItem(model.getValueAt(row, 5));
        cmbBlock.setSelectedItem(model.getValueAt(row, 6));
    }

    private void clearFields() { 
        txtId.setText(""); txtId.setEditable(true); txtId.setBackground(Color.WHITE);
        txtName.setText(""); txtCourse.setText(""); cmbYear.setSelectedIndex(0);
        cmbSemester.setSelectedIndex(0); cmbBlock.setSelectedIndex(0); table.clearSelection();
    }

    private void exportCSV() { 
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = "students_" + timestamp + ".csv"; JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fileName));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                fw.append("Student ID,Full Name,Course,Year Level,Semester,Block\n");
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 1; j <= 6; j++) { fw.append(model.getValueAt(i, j).toString()); if (j < 6) fw.append(","); }
                    fw.append("\n");
                }
            }
            JOptionPane.showMessageDialog(this, "Exported to CSV successfully!");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "CSV Error: " + ex.getMessage()); }
    }

    private void exportPDF() {
        try { 
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = "students_" + timestamp + ".pdf"; JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fileName));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            Document document = new Document(); PdfWriter.getInstance(document, new FileOutputStream(chooser.getSelectedFile()));
            document.open(); PdfPTable pdfTable = new PdfPTable(6);
            pdfTable.addCell("Student ID"); pdfTable.addCell("Full Name"); pdfTable.addCell("Course");
            pdfTable.addCell("Year Level"); pdfTable.addCell("Semester"); pdfTable.addCell("Block");
            for (int i = 0; i < model.getRowCount(); i++) 
                for (int j = 1; j <= 6; j++) pdfTable.addCell(model.getValueAt(i, j).toString());
            document.add(pdfTable); document.close();
            JOptionPane.showMessageDialog(this, "PDF exported successfully!");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "PDF Error: " + ex.getMessage()); }
    }

    public void refresh() { loadStudents(); }
}
