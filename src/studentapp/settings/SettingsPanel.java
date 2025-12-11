package studentapp.settings;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import studentapp.auth.LoginForm;
import studentapp.database.DatabaseConnection; 

public class SettingsPanel extends JPanel {

    public SettingsPanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        loadFont();

        // ---------- TITLE ----------
        JLabel title = new JLabel("Settings & System Controls");
        title.setFont(new Font("Poppins", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(15, 20, 15, 20));
        add(title, BorderLayout.NORTH);

        // ---------- MAIN CONTENT WRAPPER ----------
        JPanel container = new JPanel();
        container.setLayout(new GridLayout(1, 3, 20, 10));
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setBackground(Color.WHITE);
        add(container, BorderLayout.CENTER);

        // ==================================================
        // PANEL 1: ADD ADMIN + CHANGE PASSWORD
        // ==================================================

        JPanel adminPanel = createCard("Admin Controls");

        // Add Admin
        JTextField txtAdminUser = new JTextField();
        JPasswordField txtAdminPass = new JPasswordField();
        JPasswordField txtAdminConfirm = new JPasswordField();
        JButton btnAddAdmin = new JButton("Create Admin");

        JButton changePassBtn = new JButton("Change Password");
        changePassBtn.setFont(new Font("Poppins", Font.BOLD, 14));
        changePassBtn.addActionListener(e -> openChangePasswordDialog());
        adminPanel.add(changePassBtn);


        styleInput(txtAdminUser);
        styleInput(txtAdminPass);
        styleInput(txtAdminConfirm);
        styleButton(btnAddAdmin);

        adminPanel.add(new JLabel("New Admin Username"));
        adminPanel.add(txtAdminUser);

        adminPanel.add(new JLabel("New Admin Password"));
        adminPanel.add(txtAdminPass);

        adminPanel.add(new JLabel("Confirm Password"));
        adminPanel.add(txtAdminConfirm);

        adminPanel.add(btnAddAdmin);

        btnAddAdmin.addActionListener(e -> {
            String user = txtAdminUser.getText().trim();
            String pass = new String(txtAdminPass.getPassword());
            String confirm = new String(txtAdminConfirm.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                toast("All fields required!");
                return;
            }
            if (!pass.equals(confirm)) {
                toast("Passwords do not match!");
                return;
            }

            createAdmin(user, pass);
        });


        // ==================================================
        // PANEL 2: DATABASE CONTROLS
        // ==================================================

        JPanel dbPanel = createCard("Database Controls");

        JButton btnResetAll = new JButton("RESET DATABASE");
        JButton btnClearStudents = new JButton("Delete ALL Students");
        JButton btnClearLogs = new JButton("Clear All Logs");

        styleButton(btnResetAll);
        styleButton(btnClearStudents);
        styleButton(btnClearLogs);

        dbPanel.add(btnClearStudents);
        dbPanel.add(btnClearLogs);
        dbPanel.add(btnResetAll);

        btnClearStudents.addActionListener(e -> clearStudents());
        btnClearLogs.addActionListener(e -> clearHistory());
        btnResetAll.addActionListener(e -> resetDatabase());


        // ==================================================
        // PANEL 3: LOGOUT
        // ==================================================

        JPanel logoutPanel = createCard("Account & Exit");
        JButton btnLogout = new JButton("Logout");

        styleButton(btnLogout);
        logoutPanel.add(btnLogout);

        btnLogout.addActionListener(e -> logout());


        // Add all panels
        container.add(adminPanel);
        container.add(dbPanel);
        container.add(logoutPanel);
    }


    // ==================================================
    // SUPPORTING METHODS
    // ==================================================

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new GridLayout(10, 1, 8, 8));
        card.setBorder(BorderFactory.createTitledBorder(title));
        card.setBackground(Color.WHITE);
        return card;
    }

    private void styleInput(JTextField f) {
        f.setPreferredSize(new Dimension(200, 35));
        f.setFont(new Font("Poppins", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
    }

    private void styleButton(JButton b) {
        b.setFont(new Font("Poppins", Font.BOLD, 14));
        b.setBackground(new Color(52, 152, 219));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 8, true));
    }

    private void loadFont() {
        try {
            Font pop = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("Poppins-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pop);
        } catch (Exception e) {
            System.out.println("Font load failed");
        }
    }

    private void toast(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    private void log(String action, String details) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO update_logs (student_id, action) VALUES (?, ?)"
            );
            ps.setString(1, "0");   // System action, no student
            ps.setString(2, action + " – " + details);
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    // ==================================================
    // FUNCTIONS
    // ==================================================

    private void createAdmin(String user, String pass) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (username, password, role) VALUES (?, ?, 'Admin')"
            );
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.executeUpdate();

            log("Admin", "New admin account created");

            toast("Admin created successfully!");
        } catch (Exception e) {
            toast("Error: " + e.getMessage());
        }
    }

    private void clearStudents() {
        if (!confirmDanger("This will DELETE ALL STUDENT RECORDS.")) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.prepareStatement("DELETE FROM students").executeUpdate();
            log("Delete", "All student data removed");
            toast("All students deleted.");
        } catch (Exception e) {
            toast("Error: " + e.getMessage());
        }
    }

    private void clearHistory() {
        if (!confirmDanger("This will DELETE ALL SYSTEM LOGS.")) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.prepareStatement("DELETE FROM update_logs").executeUpdate();
            toast("History logs cleared.");
        } catch (Exception e) {
            toast("Error: " + e.getMessage());
        }
    }

private void resetDatabase() {
    if (!confirmDanger("RESET EVERYTHING?\nStudents, Logs, Admins (except main admin).")) return;

    try (Connection conn = DatabaseConnection.getConnection()) {

        // Clear system tables
        conn.prepareStatement("DELETE FROM update_logs").executeUpdate();
        conn.prepareStatement("DELETE FROM students").executeUpdate();
        conn.prepareStatement("DELETE FROM users").executeUpdate();

        // Re-create a default admin account
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO users (username, password, role) VALUES (?, ?, 'Admin')"
        );
        ps.setString(1, "admin1");     // default username
        ps.setString(2, "password1");  // default password
        ps.executeUpdate();

        toast("Database reset successfully!\nDefault admin restored.");
        log("Reset", "Database fully wiped; main admin restored.");
    } catch (Exception e) {
        toast("Error: " + e.getMessage());
    }
}

    private boolean confirmDanger(String message) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        return confirm == JOptionPane.YES_OPTION;
    }

    // ==================================================
    // LOGOUT (RETURN TO LOGINFORM)
    // ==================================================

    private void logout() {
        JOptionPane.showMessageDialog(this, "Logged out successfully.");

        // Close current window
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) window.dispose();

        // Open LoginForm again
        new LoginForm().setVisible(true);
    }

    private void openChangePasswordDialog() {
    JDialog dialog = new JDialog((Frame) null, "Change Password", true);
    dialog.setSize(400, 300);
    dialog.setLayout(new GridLayout(6, 1, 10, 10));
    dialog.setLocationRelativeTo(null);

    JLabel lbl1 = new JLabel("New Password:");
    JPasswordField txtNew = new JPasswordField();

    JLabel lbl2 = new JLabel("Confirm Password:");
    JPasswordField txtConfirm = new JPasswordField();

    JButton btnUpdate = new JButton("Update Password");
    styleButton(btnUpdate);

    JLabel msg = new JLabel("");
    msg.setForeground(Color.RED);
    msg.setHorizontalAlignment(SwingConstants.CENTER);

    styleInput(txtNew);
    styleInput(txtConfirm);

    dialog.add(lbl1);
    dialog.add(txtNew);
    dialog.add(lbl2);
    dialog.add(txtConfirm);
    dialog.add(btnUpdate);
    dialog.add(msg);

    btnUpdate.addActionListener(e -> {
        String newPass = new String(txtNew.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (newPass.isEmpty()) {
            msg.setText("Password cannot be empty.");
            return;
        }

        if (!newPass.equals(confirm)) {
            msg.setText("Passwords do not match.");
            return;
        }

        // Update DB
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE users SET password=? WHERE role='Admin'"
            );
            ps.setString(1, newPass);
            ps.executeUpdate();

            log("Admin", "Admin password updated");

            toast("Password updated successfully!");
            dialog.dispose();

        } catch (Exception ex) {
            toast("Error: " + ex.getMessage());
        }
    });

    dialog.setVisible(true);
}


}
