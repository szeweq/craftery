package szewek.craftery;

import javax.swing.*;

public final class Craftery {
    public static final String APP_TITLE = "Craftery";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Unable to set system Look and Feel");
            e.printStackTrace();
        }
        CrafteryKt.startApp();
    }
}
