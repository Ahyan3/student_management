package studentapp;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class LoginForm extends JFrame {

    private final JTextField txtUser;
    private final JPasswordField txtPass;

    private final UserDatabase userDB = new UserDatabase();
    private final AdminDatabase adminDB = new AdminDatabase();

    public LoginForm() {

        // === WINDOW SETTINGS ===
        setTitle("Login");
        setSize(640, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // LOAD POPPINS FONT (Fallback Segoe UI)
        Font poppins;
        try {
            poppins = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"))
                    .deriveFont(15f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(poppins);
        } catch (Exception e) {
            poppins = new Font("Segoe UI", Font.PLAIN, 15);
        }

        // === MAIN PANEL ===
        JPanel panel = new JPanel();
        panel.setBackground(new Color(248, 248, 248));
        panel.setBorder(new EmptyBorder(25, 35, 25, 35));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // === TITLE ===
        JLabel lblTitle = new JLabel("STUDENT MANAGEMENT SYSTEM");
        lblTitle.setFont(poppins.deriveFont(Font.BOLD, 20f));
        lblTitle.setForeground(new Color(40, 40, 40));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblLogin = new JLabel("LOGIN");
        lblLogin.setFont(poppins.deriveFont(Font.BOLD, 18f));
        lblLogin.setForeground(new Color(40, 40, 40));
        lblLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

       // === USERNAME FIELD ===
JLabel lblUser = new JLabel("Username");
lblUser.setFont(poppins);
lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);

txtUser = new JTextField();
txtUser.setFont(poppins);

// Bigger text field
txtUser.setPreferredSize(new Dimension(380, 48));
txtUser.setMaximumSize(new Dimension(380, 48));
txtUser.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));

// === PASSWORD FIELD ===
JLabel lblPass = new JLabel("Password");
lblPass.setFont(poppins);
lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);

txtPass = new JPasswordField();
txtPass.setFont(poppins);

// Bigger text field
txtPass.setPreferredSize(new Dimension(380, 48));
txtPass.setMaximumSize(new Dimension(380, 48));
txtPass.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));

        // === LOGIN BUTTON ===
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(poppins.deriveFont(Font.BOLD, 15f));
        btnLogin.setFocusPainted(false);
        btnLogin.setBackground(new Color(66, 135, 245));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(300, 40));

        // === EXIT BUTTON ===
        JButton btnExit = new JButton("Exit");
        btnExit.setFont(poppins.deriveFont(Font.BOLD, 15f));
        btnExit.setFocusPainted(false);
        btnExit.setBackground(new Color(200, 50, 50));
        btnExit.setForeground(Color.WHITE);
        btnExit.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExit.setMaximumSize(new Dimension(300, 40));

        // BUTTON ACTIONS
        btnLogin.addActionListener(e -> login());
        btnExit.addActionListener(e -> System.exit(0));

        int gap = 12;

        // ADD COMPONENTS
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(10));

        panel.add(lblLogin);
        panel.add(Box.createVerticalStrut(20));

        panel.add(lblUser);
        panel.add(txtUser);
        panel.add(Box.createVerticalStrut(gap));

        panel.add(lblPass);
        panel.add(txtPass);
        panel.add(Box.createVerticalStrut(gap + 10));

        panel.add(btnLogin);
        panel.add(Box.createVerticalStrut(gap));
        panel.add(btnExit);

        add(panel);
    }

    private void login() {
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username & password.");
            return;
        }

        // Check admin database first
        if (adminDB.validateAdmin(user, pass)) {
            JOptionPane.showMessageDialog(this, "Admin login successful!");
            new AdminDashboard();   // Make sure this class exists
            dispose();
            return;
        }

        // Check normal user database
        if (userDB.validateLogin(user, pass)) {
            JOptionPane.showMessageDialog(this, "User login successful!");
            new StudentManagementSystem();    // Make sure this class exists
            dispose();
            return;
        }

        JOptionPane.showMessageDialog(this, "Invalid username or password.");
    }


    public static void main(String[] args) {
        new LoginForm().setVisible(true);
    }
}
