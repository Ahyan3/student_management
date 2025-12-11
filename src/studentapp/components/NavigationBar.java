package studentapp.components;

import javax.swing.*;
import java.awt.*;
import studentapp.core.AppColors;
import studentapp.core.AppFonts;

public class NavigationBar extends JPanel {

    public JButton btnHome, btnStudent, btnHistory, btnSettings;

    public NavigationBar() {
        setLayout(new GridLayout(4, 1));
        setBackground(AppColors.NAV_BG);
        setPreferredSize(new Dimension(180, 600));

        btnHome = createNavButton("Home");
        btnStudent = createNavButton("Student");
        btnHistory = createNavButton("History");
        btnSettings = createNavButton("Settings");

        add(btnHome);
        add(btnStudent);
        add(btnHistory);
        add(btnSettings);
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(AppFonts.poppinsBold(16));
        btn.setFocusPainted(false);
        btn.setBackground(AppColors.NAV_BG);
        btn.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
