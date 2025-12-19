package studentapp.student;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import studentapp.MainDashboard;
import studentapp.database.DatabaseConnection;

public class GradePanel extends JPanel {

    private JTextField txtPrelim, txtMidterm, txtPrefinal, txtFinals;

    public GradePanel(String studentId) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 244, 248));

        // Title
        JLabel title = new JLabel("Grades for Student ID: " + studentId);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(new EmptyBorder(40, 50, 30, 50));
        add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(50, 60, 50, 60)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Grade Fields
        txtPrelim = createGradeField();
        txtMidterm = createGradeField();
        txtPrefinal = createGradeField();
        txtFinals = createGradeField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Prelim Grade:"), gbc);
        gbc.gridx = 1;
        form.add(txtPrelim, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Midterm Grade:"), gbc);
        gbc.gridx = 1;
        form.add(txtMidterm, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Prefinal Grade:"), gbc);
        gbc.gridx = 1;
        form.add(txtPrefinal, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        form.add(new JLabel("Finals Grade:"), gbc);
        gbc.gridx = 1;
        form.add(txtFinals, gbc);

        // Save Button
        JButton saveBtn = new JButton("Save All Grades");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setPreferredSize(new Dimension(200, 50));

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(40, 0, 0, 0);
        form.add(saveBtn, gbc);

        // Back Button
        JButton backBtn = new JButton("← Back to Students");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backBtn.setBackground(new Color(108, 117, 125));
        backBtn.setForeground(Color.WHITE);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> goBackToStudentPanel());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);
        add(form, BorderLayout.CENTER);

        // Load existing grades
        loadGrades(studentId);

        // Save action
        saveBtn.addActionListener(e -> saveGrades(studentId));
    }

    private JTextField createGradeField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                new EmptyBorder(12, 16, 12, 16)));
        return field;
    }

    private void loadGrades(String studentId) {
        // Clear fields first
        txtPrelim.setText("");
        txtMidterm.setText("");
        txtPrefinal.setText("");
        txtFinals.setText("");

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM grades WHERE student_id = ?")) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtPrelim.setText(rs.getString("prelim"));
                txtMidterm.setText(rs.getString("midterm"));
                txtPrefinal.setText(rs.getString("prefinal"));
                txtFinals.setText(rs.getString("finals"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading grades: " + e.getMessage());
        }
    }

    private void saveGrades(String studentId) {
        String prelim = txtPrelim.getText().trim();
        String midterm = txtMidterm.getText().trim();
        String prefinal = txtPrefinal.getText().trim();
        String finals = txtFinals.getText().trim();

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
            JOptionPane.showMessageDialog(this, "All grades saved successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving grades: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBackToStudentPanel() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // Close the current window (GradePanel)
        if (currentFrame != null) {
            currentFrame.dispose(); // Fully closes the window
        }

        // Open a fresh, maximized MainDashboard
        SwingUtilities.invokeLater(() -> {
            MainDashboard dashboard = new MainDashboard();
            dashboard.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the window
            dashboard.setVisible(true);
        });
    }

}