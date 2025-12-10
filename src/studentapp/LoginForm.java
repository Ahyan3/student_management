package studentapp;

import java.awt.*;
import javax.swing.*;

public class LoginForm extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private UserDatabase userDB = new UserDatabase();

    public LoginForm() {
        setTitle("Login");
        setSize(350, 200);
        setLayout(new GridLayout(3, 2, 10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new JLabel("Username:"));
        txtUser = new JTextField();
        add(txtUser);

        add(new JLabel("Password:"));
        txtPass = new JPasswordField();
        add(txtPass);

        JButton loginBtn = new JButton("Login");
        add(loginBtn);

        JButton exitBtn = new JButton("Exit");
        add(exitBtn);

        loginBtn.addActionListener(e -> login());
        exitBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void login() {
        String u = txtUser.getText();
        String p = new String(txtPass.getPassword());

        // ADMIN LOGIN (hardcoded)
        if (u.equals("admin123") && p.equals("password123")) {
            JOptionPane.showMessageDialog(this, "Admin login successful!");
            dispose();
            new AdminDashboard();
            return;
        }

        // NORMAL USER LOGIN
        if (userDB.checkUser(u, p)) {
            JOptionPane.showMessageDialog(this, "User login successful!");
            dispose();
            new StudentManagementSystem();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!");
        }
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}
