package ie.ul.postgrad.socialanxietyapp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ie.ul.postgrad.socialanxietyapp.MainGame;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 720 / 2;
        config.height = 1280 / 2;
        new LwjglApplication(new MainGame(new DesktopLibgdxInterfaceDummy(), MainGame.CONVERSATION_SCREEN), config);
    }
}
