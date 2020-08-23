package szewek.mctool;

import javafx.application.Application;
import szewek.mctool.app.MCToolApp;

public final class Launcher {
    public static void main(String[] args) {
        Application.launch(MCToolApp.class, args);
    }

    private Launcher() {}
}
