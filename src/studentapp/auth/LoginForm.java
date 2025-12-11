package studentapp.auth;

import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.swing.*;
import studentapp.MainDashboard;
import studentapp.database.DatabaseConnection;

public class LoginForm extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginForm() {

        // ===== LOAD POPPINS FONT =====
        try {
            Font poppins = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File("src/main/resources/fonts/Poppins-Regular.ttf")
            ).deriveFont(14f);

            Font poppinsBold = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File("src/main/resources/fonts/Poppins-Bold.ttf")
            ).deriveFont(22f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(poppins);
            ge.registerFont(poppinsBold);

            UIManager.put("Label.font", poppins);
            UIManager.put("Button.font", poppins);
            UIManager.put("TextField.font", poppins);
            UIManager.put("PasswordField.font", poppins);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Failed to load Poppins font.");
        }

        // ===== WINDOW SETTINGS =====
        setTitle("Login");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); // REMOVE maximize button
        setLayout(new BorderLayout(10, 10));

        // ===== TITLE PANEL =====
        JLabel lblTitle = new JLabel("Student Record System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        add(lblTitle, BorderLayout.NORTH);

        // ===== CENTER FORM PANEL =====
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lblUser = new JLabel("Username:");
        txtUser = new JTextField();

        JLabel lblPass = new JLabel("Password:");
        txtPass = new JPasswordField();

        formPanel.add(lblUser);
        formPanel.add(txtUser);
        formPanel.add(lblPass);
        formPanel.add(txtPass);

        add(formPanel, BorderLayout.CENTER);

        // ===== LOGIN BUTTON =====
        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(140, 40));
        loginBtn.setFocusPainted(false);
        loginBtn.setBackground(new Color(52, 152, 219));
        loginBtn.setForeground(Color.WHITE);

        // Rounded button UI
        loginBtn.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2, true));

        JPanel btnPanel = new JPanel();
        btnPanel.add(loginBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // Button Action
        loginBtn.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        String u = txtUser.getText();
        String p = new String(txtPass.getPassword());

        try (Connection con = DatabaseConnection.getConnection()) {

            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, u);
            pst.setString(2, p);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                JOptionPane.showMessageDialog(this, "Welcome, " + u + "!");

                dispose();
                new MainDashboard(u, role);

            } else {
                JOptionPane.showMessageDialog(this, "Invalid Login!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
