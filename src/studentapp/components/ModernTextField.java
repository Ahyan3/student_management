package studentapp.components;

import studentapp.core.AppFonts;

import javax.swing.*;
import java.awt.*;

public class ModernTextField extends JTextField {

    public ModernTextField() {
        setFont(AppFonts.poppinsRegular(16));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(280, 40));
    }
}
