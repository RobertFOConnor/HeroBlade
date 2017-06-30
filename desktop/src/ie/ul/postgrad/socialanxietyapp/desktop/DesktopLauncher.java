package ie.ul.postgrad.socialanxietyapp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ie.ul.postgrad.socialanxietyapp.MainGame;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 720 / 2;
        config.height = 1280 / 2;
        new LwjglApplication(new MainGame(new DesktopLibgdxInterfaceDummy(), MainGame.AVATAR_SCREEN), config);


        int damage = 0;

        int level = 20;
        int basePower = 8;
        int offence = 20;
        int defense = 2;

        damage = (int) Math.floor(Math.floor(Math.floor(2 * level / 5 + 2) * offence * basePower / defense) / 50) + 2;


        System.out.println(damage);
    }
}
