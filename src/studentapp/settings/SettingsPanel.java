package studentapp.settings;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import studentapp.auth.LoginForm;
import studentapp.database.DatabaseConnection;


public class SettingsPanel extends JPanel {

private static final Color PRIMARY = new Color(41, 128, 185);   // darker blue
private static final Color SUCCESS = new Color(39, 174, 96);    // darker green
private static final Color DANGER  = new Color(192, 57, 43);    // darker red


    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

        // === HEADER: Title + Export Buttons ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel title = new JLabel("Settings & System Controls");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(44, 62, 80));
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // === MAIN CONTENT: 3 Cards ===
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(20, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 20, 0, 20);

        // CARD 1: Admin Controls
        JPanel adminCard = createSettingsCard("Admin Controls");
        addAdminControls(adminCard);
        gbc.gridx = 0;
        main.add(adminCard, gbc);

        // CARD 2: Database Controls
        JPanel dbCard = createSettingsCard("Database Management");
        addDatabaseControls(dbCard);
        gbc.gridx = 1;
        main.add(dbCard, gbc);

        // CARD 3: Account & System
        JPanel accountCard = createSettingsCard("Account & System");
        addAccountControls(accountCard);
        gbc.gridx = 2;
        main.add(accountCard, gbc);

        JScrollPane scrollPane = new JScrollPane(main);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSettingsCard(String cardTitle) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(30, 30, 30, 30)
        ));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel(cardTitle);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        card.add(title);

        return card;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false); 
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBorder(new EmptyBorder(14, 30, 14, 30));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void addAdminControls(JPanel card) {
        JButton changePassBtn = createStyledButton("Change Admin Password", PRIMARY);
        changePassBtn.addActionListener(e -> openChangePasswordDialog());
        card.add(changePassBtn);
        card.add(Box.createVerticalStrut(20));

        JLabel userLabel = new JLabel("New Admin Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField txtAdminUser = new JTextField();
        txtAdminUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        txtAdminUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtAdminUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel passLabel = new JLabel("New Admin Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField txtAdminPass = new JPasswordField();
        txtAdminPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        txtAdminPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtAdminPass.setBorder(txtAdminUser.getBorder());

        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        confirmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField txtAdminConfirm = new JPasswordField();
        txtAdminConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        txtAdminConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtAdminConfirm.setBorder(txtAdminUser.getBorder());

        // JButton btnAddAdmin = createStyledButton("Create New Admin", PRIMARY);

    //     card.add(userLabel);
    //     card.add(Box.createVerticalStrut(8));
    //     card.add(txtAdminUser);
    //     card.add(Box.createVerticalStrut(15));
    //     card.add(passLabel);
    //     card.add(Box.createVerticalStrut(8));
    //     card.add(txtAdminPass);
    //     card.add(Box.createVerticalStrut(15));
    //     card.add(confirmLabel);
    //     card.add(Box.createVerticalStrut(8));
    //     card.add(txtAdminConfirm);
    //     card.add(Box.createVerticalStrut(25));
    //     card.add(btnAddAdmin);

    //     btnAddAdmin.addActionListener(e -> {
    //         String user = txtAdminUser.getText().trim();
    //         String pass = new String(txtAdminPass.getPassword());
    //         String confirm = new String(txtAdminConfirm.getPassword());

    //         if (user.isEmpty() || pass.isEmpty()) {
    //             JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
    //             return;
    //         }
    //         if (!pass.equals(confirm)) {
    //             JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
    //             return;
    //         }

    //         createAdmin(user, pass);
    //     });
    }

    private void addDatabaseControls(JPanel card) {
        JButton btnClearStudents = createStyledButton("Delete All Students", DANGER);
        JButton btnClearLogs = createStyledButton("Clear All Logs", DANGER);
        // JButton btnResetAll = createStyledButton("FULL DATABASE RESET", DANGER);

        card.add(btnClearStudents);
        card.add(Box.createVerticalStrut(15));
        card.add(btnClearLogs);
        card.add(Box.createVerticalStrut(15));
        // card.add(btnResetAll);

        btnClearStudents.addActionListener(e -> clearStudents());
        btnClearLogs.addActionListener(e -> clearHistory());
        // btnResetAll.addActionListener(e -> resetDatabase());
    } 

    private void addAccountControls(JPanel card) {
        JButton btnLogout = createStyledButton("Logout", new Color(108, 117, 125));
        btnLogout.addActionListener(e -> logout());
        card.add(btnLogout);
        card.add(Box.createVerticalGlue()); // Push logout to top
    }

    // === DATABASE OPERATIONS (unchanged) ===
    private void createAdmin(String user, String pass) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (username, password, role) VALUES (?, ?, 'Admin')")) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "New admin created successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating admin: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearStudents() {
        if (confirmDanger("This will permanently DELETE ALL student records and grades!")) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.prepareStatement("DELETE FROM grades").executeUpdate();
                conn.prepareStatement("DELETE FROM students").executeUpdate();
                JOptionPane.showMessageDialog(this, "All students and grades deleted.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void clearHistory() {
        if (confirmDanger("This will permanently DELETE ALL system logs!")) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.prepareStatement("DELETE FROM update_logs").executeUpdate();
                JOptionPane.showMessageDialog(this, "All history logs cleared.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void resetDatabase() {
        if (confirmDanger("This will RESET EVERYTHING:\n• All students & grades\n• All logs\n• All admins except default")) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.prepareStatement("DELETE FROM update_logs").executeUpdate();
                conn.prepareStatement("DELETE FROM grades").executeUpdate();
                conn.prepareStatement("DELETE FROM students").executeUpdate();
                conn.prepareStatement("DELETE FROM users WHERE username != 'admin1'").executeUpdate();

                JOptionPane.showMessageDialog(this, "Database fully reset!\nDefault admin preserved.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private boolean confirmDanger(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Dangerous Action", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void logout() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) window.dispose();
        new LoginForm().setVisible(true);
    }

    private void openChangePasswordDialog() {
        JDialog dialog = new JDialog((Frame) null, "Change Admin Password", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JPasswordField txtNew = new JPasswordField();
        JPasswordField txtConfirm = new JPasswordField();
        JLabel msg = new JLabel("");
        msg.setForeground(Color.RED);
        msg.setHorizontalAlignment(SwingConstants.CENTER);

        txtNew.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtNew.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                new EmptyBorder(12, 16, 12, 16)
        ));
        txtConfirm.setBorder(txtNew.getBorder());

        gbc.gridy = 0; dialog.add(new JLabel("New Password:"), gbc);
        gbc.gridy = 1; dialog.add(txtNew, gbc);
        gbc.gridy = 2; dialog.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridy = 3; dialog.add(txtConfirm, gbc);

        JButton btnUpdate = createStyledButton("Update Password", PRIMARY);
        gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        dialog.add(btnUpdate, gbc);

        gbc.gridy = 5; gbc.gridwidth = 2;
        dialog.add(msg, gbc);

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

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE users SET password = ?")) {
                ps.setString(1, newPass);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(dialog, "Password updated successfully!");
                    dialog.dispose();
                } else {
                    msg.setText("No users found to update.");
                }
            } catch (Exception ex) {
                msg.setText("Error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }
}