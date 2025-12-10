package studentapp;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private UserDatabase userDB = new UserDatabase();

    public AdminDashboard() {
        setTitle("Admin Panel - Create User Accounts");
        setSize(400, 250);
        setLayout(new GridLayout(4, 2, 10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new JLabel("New Username:"));
        txtUser = new JTextField();
        add(txtUser);

        add(new JLabel("New Password:"));
        txtPass = new JPasswordField();
        add(txtPass);

        JButton addBtn = new JButton("Add User");
        add(addBtn);

        JButton logoutBtn = new JButton("Logout");
        add(logoutBtn);

        addBtn.addActionListener(e -> addUser());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginForm();
        });

        setVisible(true);
    }

    private void addUser() {
        String u = txtUser.getText();
        String p = new String(txtPass.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both fields are required!");
            return;
        }

        if (userDB.addUser(u, p)) {
            JOptionPane.showMessageDialog(this, "User added successfully!");
            txtUser.setText("");
            txtPass.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Error adding user");
        }
    }
}
