package studentapp.components;

import studentapp.core.AppFonts;

import javax.swing.*;

public class ModernLabel extends JLabel {

    public ModernLabel(String text, int size, boolean bold) {
        super(text);
        setFont(bold 
                ? AppFonts.poppinsBold(size)
                : AppFonts.poppinsRegular(size));
    }
}
