package studentapp.core;

import java.awt.*;
import java.io.File;

public class AppFonts {

    public static Font poppinsRegular(int size) {
        return loadFont("fonts/Poppins-Regular.ttf", size);
    }

    public static Font poppinsBold(int size) {
        return loadFont("fonts/Poppins-Bold.ttf", size);
    }

    private static Font loadFont(String path, float size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(path)).deriveFont(size);
        } catch (Exception e) {
            System.out.println("Failed to load font: " + path);
            return new Font("SansSerif", Font.PLAIN, (int) size);
        }
    }
}
