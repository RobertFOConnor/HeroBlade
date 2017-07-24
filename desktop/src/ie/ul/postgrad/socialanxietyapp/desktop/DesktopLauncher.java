package ie.ul.postgrad.socialanxietyapp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ie.ul.postgrad.socialanxietyapp.MainGame;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 720 / 2;
        config.height = 1280 / 2;
        new LwjglApplication(new MainGame(new DesktopLibgdxInterfaceDummy(), MainGame.BATTLE_SCREEN), config);


        int damage = 0;

        int level = 25;
        int basePower = 15;
        int offence = 30;
        int defense = 2;

        damage = (int) Math.floor(Math.floor(Math.floor(2 * level / 5 + 2) * offence * basePower / defense) / 50) + 2;

        damage = (int) (damage / 1.5f);

        System.out.println(damage);
    }
}
