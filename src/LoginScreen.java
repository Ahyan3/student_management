import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("Student Management System");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Disable maximize button
        setResizable(false);

        // Center on screen
        setLocationRelativeTo(null);

        // Main panel
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Title label
        JLabel lblTitle = new JLabel("LOGIN");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 26));
        lblTitle.setForeground(new Color(40, 40, 40));

        // Username
        JLabel labelUser = new JLabel("Username");
        labelUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextField txtUser = new JTextField();
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUser.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        txtUser.setPreferredSize(new Dimension(0, 35));

        // Password
        JLabel labelPass = new JLabel("Password");
        labelPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPass.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        txtPass.setPreferredSize(new Dimension(0, 35));

        // Button
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Poppins", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBackground(new Color(70, 130, 250));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogin.addActionListener(e -> {
            String u = txtUser.getText();
            String p = new String(txtPass.getPassword());

            if (u.equals("admin") && p.equals("123")) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials");
            }
        });

        // Add spacing helper
        int gap = 10;

        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(gap + 10));
        panel.add(labelUser);
        panel.add(txtUser);
        panel.add(Box.createVerticalStrut(gap));
        panel.add(labelPass);
        panel.add(txtPass);
        panel.add(Box.createVerticalStrut(gap + 10));
        panel.add(btnLogin);

        add(panel);
    }

    // // For testing
    // public static void main(String[] args) {
    //     new LoginScreen().setVisible(true);
    // }
}
