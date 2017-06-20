package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 02-May-17.
 */

public class EnemyFactory {

    public static Enemy buildEnemy() {
        return new Enemy("Kabula", 5, 20);
    }

}
