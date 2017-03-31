package ie.ul.postgrad.socialanxietyapp.screen;

/**
 * Created by Robert on 24-Mar-17.
 */

public class ScreenManager {

    private static Screen screen;

    public static Screen getCurrentScreen() {
        return screen;
    }

    public static void setScreen(Screen s) {
        if (screen != null) {
            screen.dispose();
        }
        screen = s;
        screen.create();
    }
}
