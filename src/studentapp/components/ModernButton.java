package studentapp.components;

import studentapp.core.AppColors;
import studentapp.core.AppFonts;

import javax.swing.*;
import java.awt.*;

public class ModernButton extends JButton {

    public ModernButton(String text) {
        super(text);
        setFont(AppFonts.poppinsBold(16));
        setForeground(Color.DARK_GRAY);
        setBackground(AppColors.PRIMARY);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(160, 40));

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(AppColors.PRIMARY_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(AppColors.PRIMARY);
            }
        });
    }
}
