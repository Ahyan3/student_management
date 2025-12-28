package studentapp;

import java.awt.*;
import javax.swing.*;
import studentapp.history.HistoryPanel;
import studentapp.home.HomePanel;
import studentapp.settings.SettingsPanel;
import studentapp.student.GradesOverviewPanel;
import studentapp.student.StudentPanel;

public class MainDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JButton btnHome, btnStudent, btnGrades, btnHistory, btnSettings;
    private JButton activeButton;

    private JButton userMenuBtn;

    private String currentUser = "User";
    private String currentRole = "Role";

    private HomePanel homePanel;
    private StudentPanel studentPanel;
    private HistoryPanel historyPanel;
    private GradesOverviewPanel gradesOverviewPanel;

    public MainDashboard() {
        setTitle("Teacher Assistant System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        loadFont();

        // ===== TOP HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setPreferredSize(new Dimension(getWidth(), 60));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Teacher Assistant System");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        // ===== USER MENU BUTTON =====
        userMenuBtn = new JButton();
        userMenuBtn.setFont(new Font("Poppins", Font.PLAIN, 14));
        userMenuBtn.setForeground(Color.WHITE);
        userMenuBtn.setBackground(new Color(41, 128, 185));
        userMenuBtn.setFocusPainted(false);
        userMenuBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        userMenuBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        userMenuBtn.setContentAreaFilled(false);
        userMenuBtn.setOpaque(false);

        // ===== DROPDOWN MENU =====
        JPopupMenu userMenu = new JPopupMenu();
        userMenu.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(new Font("Poppins", Font.PLAIN, 13));
        logoutItem.addActionListener(e -> logout());

        userMenu.add(logoutItem);

        userMenuBtn.addActionListener(e -> userMenu.show(userMenuBtn, 0, userMenuBtn.getHeight()));

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false);
        rightHeader.add(userMenuBtn);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(33, 47, 61));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        btnHome = navButton("Home");
        btnStudent = navButton("Student");
        btnGrades = navButton("Manage Grades");
        btnHistory = navButton("History");
        btnSettings = navButton("Settings");

        sidebar.add(btnHome);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnStudent);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnGrades);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnHistory);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnSettings);

        add(sidebar, BorderLayout.WEST);

        // ===== MAIN CONTENT =====
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        homePanel = new HomePanel();
        mainPanel.add(homePanel, "home");

        studentPanel = new StudentPanel();
        mainPanel.add(studentPanel, "student");

        gradesOverviewPanel = new GradesOverviewPanel();
        mainPanel.add(gradesOverviewPanel, "grades");

        historyPanel = new HistoryPanel();
        mainPanel.add(historyPanel, "history");

        mainPanel.add(new SettingsPanel(), "settings");

        add(mainPanel, BorderLayout.CENTER);

        // ===== BUTTON ACTIONS =====
        btnHome.addActionListener(e -> {
            switchPage(btnHome, "home");
            homePanel.refresh();
        });

        btnStudent.addActionListener(e -> {
            switchPage(btnStudent, "student");
            studentPanel.refresh(); // Refresh student list
        });

        btnGrades.addActionListener(e -> {
            switchPage(btnGrades, "grades");
            gradesOverviewPanel.refresh(); // ← Now calls the correct refresh() method
        });

        btnHistory.addActionListener(e -> {
            switchPage(btnHistory, "history");
            historyPanel.refresh();
        });

        btnSettings.addActionListener(e -> switchPage(btnSettings, "settings"));

        switchPage(btnHome, "home");

        setVisible(true);
    }

    // ===== CONSTRUCTOR WITH USER DATA =====
    public MainDashboard(String username, String role) {
        this();
        this.currentUser = username;
        this.currentRole = role;

        userMenuBtn.setText(username + " (" + role + ") ");
    }

    // ===== SIDEBAR BUTTON =====
    private JButton navButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setFont(new Font("Poppins", Font.PLAIN, 18));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(44, 62, 80));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeButton)
                    btn.setBackground(new Color(52, 73, 94));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeButton)
                    btn.setBackground(new Color(44, 62, 80));
            }
        });

        return btn;
    }

    // ===== PAGE SWITCH =====
    private void switchPage(JButton btn, String page) {
        if (activeButton != null) {
            activeButton.setBackground(new Color(44, 62, 80));
        }

        activeButton = btn;
        activeButton.setBackground(new Color(52, 152, 219));

        cardLayout.show(mainPanel, page);
    }

    // ===== LOGOUT =====
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new studentapp.auth.LoginForm();
        }
    }

    private void loadFont() {
        try {
            Font pop = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new java.io.File("src/main/resources/fonts/Poppins-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pop);
        } catch (Exception ignored) {
        }
    }
}