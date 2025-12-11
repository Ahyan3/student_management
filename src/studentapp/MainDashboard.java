package studentapp;

import java.awt.*;
import javax.swing.*;
import studentapp.history.HistoryPanel;
import studentapp.home.HomePanel;
import studentapp.settings.SettingsPanel;
import studentapp.student.StudentPanel;

public class MainDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainDashboard() {
        setTitle("Student Record System");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // can maximize, but our layout stays clean

        loadFont();

        setLayout(new BorderLayout());

        // ===== LEFT SIDEBAR =====
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(33, 47, 61));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setLayout(new GridLayout(10, 1, 0, 10));

        JLabel title = new JLabel("MENU", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        sidebar.add(title);

        JButton btnHome = navButton("Home");
        JButton btnStudent = navButton("Student");
        JButton btnHistory = navButton("History");
        JButton btnSettings = navButton("Settings");

        sidebar.add(btnHome);
        sidebar.add(btnStudent);
        sidebar.add(btnHistory);
        sidebar.add(btnSettings);

        add(sidebar, BorderLayout.WEST);

        // ===== MAIN AREA (CardLayout) =====
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add all pages
        mainPanel.add(new HomePanel(), "home");
        mainPanel.add(new StudentPanel(), "student");
        mainPanel.add(new HistoryPanel(), "history");
        mainPanel.add(new SettingsPanel(), "settings");

        add(mainPanel, BorderLayout.CENTER);

        // ===== BUTTON ACTIONS =====
        btnHome.addActionListener(e -> cardLayout.show(mainPanel, "home"));
        btnStudent.addActionListener(e -> cardLayout.show(mainPanel, "student"));
        btnHistory.addActionListener(e -> cardLayout.show(mainPanel, "history"));
        btnSettings.addActionListener(e -> cardLayout.show(mainPanel, "settings"));

        setVisible(true);
    }

    public MainDashboard(String username, String role) {
        this(); // calls default constructor (builds UI)

        // Optional actions:
        System.out.println("Logged in as: " + username);
        System.out.println("Role: " + role);
    }


    private JButton navButton(String name) {
        JButton btn = new JButton(name);
        btn.setFont(new Font("Poppins", Font.PLAIN, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(44, 62, 80));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 73, 94));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(44, 62, 80));
            }
        });

        return btn;
    }

    private void loadFont() {
        try {
            Font pop = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("Poppins-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pop);
        } catch (Exception ignored) {}
    }
}
