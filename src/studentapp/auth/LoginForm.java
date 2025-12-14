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
        }

        // ===== WINDOW SETTINGS =====
        setTitle("Login");
        setSize(720, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new GridLayout(1, 2));

        // ===== LEFT PANEL =====
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(41, 128, 185));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));

        JLabel lblTitle = new JLabel("Student Record System");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 22));
        lblTitle.setForeground(new Color(245, 245, 245));

        JLabel lblDesc = new JLabel(
                "<html>Manage student records efficiently and securely.<br>"
              + "Access academic data with ease.</html>"
        );
        lblDesc.setForeground(new Color(230, 230, 230));
        lblDesc.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        leftPanel.add(lblTitle);
        leftPanel.add(lblDesc);

        // ===== RIGHT PANEL =====
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        rightPanel.setBackground(new Color(255, 255, 255, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;

        JLabel lblLogin = new JLabel("Login");
        lblLogin.setFont(new Font("Poppins", Font.BOLD, 22));

        // ===== INPUT FIELDS =====
        txtUser = new JTextField();
        txtPass = new JPasswordField();

        Dimension fieldSize = new Dimension(260, 42);

        txtUser.setPreferredSize(fieldSize);
        txtUser.setMinimumSize(fieldSize);

        txtPass.setPreferredSize(fieldSize);
        txtPass.setMinimumSize(fieldSize);

        txtUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        txtPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // ===== WRAPPERS (CRITICAL) =====
        JPanel userWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        userWrapper.setOpaque(false);
        userWrapper.add(txtUser);

        JPanel passWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passWrapper.setOpaque(false);
        passWrapper.add(txtPass);

        JButton btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(260, 42));
        btnLogin.setMinimumSize(new Dimension(260, 42));
        btnLogin.setBackground(new Color(52, 152, 219, 220));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createLineBorder(
                new Color(41, 128, 185), 1, true
        ));

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnWrapper.setOpaque(false);
        btnWrapper.add(btnLogin);

        // ===== FORM LAYOUT =====
        gbc.gridy = 0;
        rightPanel.add(lblLogin, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Username"), gbc);

        gbc.gridy++;
        rightPanel.add(userWrapper, gbc);

        gbc.gridy++;
        rightPanel.add(new JLabel("Password"), gbc);

        gbc.gridy++;
        rightPanel.add(passWrapper, gbc);

        // 👇 THIS IS THE IMPORTANT PART
        gbc.gridy++;
        gbc.insets = new Insets(25, 0, 0, 0);
        gbc.weighty = 0;
        rightPanel.add(btnWrapper, gbc);

        // ===== ADD PANELS =====
        add(leftPanel);
        add(rightPanel);

        // ===== ACTION =====
        btnLogin.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        String u = txtUser.getText();
        String p = new String(txtPass.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

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
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
