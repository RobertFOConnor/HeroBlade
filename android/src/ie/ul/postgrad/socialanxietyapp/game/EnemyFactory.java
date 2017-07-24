package ie.ul.postgrad.socialanxietyapp.game;

import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 02-May-17.
 */

public class EnemyFactory {

    public static Enemy buildEnemy(Player player) {

        String[] enemyNames = {"Giant Spider", "Skeleton", "Robo_Brain", "Fire Demon", "Angler-Man", "Sharkhead", "Turnip-Legs", "Dark Angel", "Gremlin", "Scissor-Man", "Wolf-Man", "Giant", "Evil Crab"};

        //Logic here to select enemy type (Name and image, attack set)
        String name = enemyNames[(int) (Math.random() * enemyNames.length)];

        //Enemy level based within -3/+3 of players level.
        int level = (player.getLevel() - 3) + ((int) (Math.random() * 7));

        if (level < 1) {
            level = 1;
        }

        int health = 5 + ((level - 1) * 5) + ((int) Math.random() * 31);

        int random = (int) (Math.random() * 3);
        String type = WeaponItem.FIRE_TYPE;
        switch (random) {

            case 0:
                type = WeaponItem.WATER_TYPE;
                break;
            case 1:
                type = WeaponItem.GRASS_TYPE;
                break;
        }

        return new Enemy(name, type, level, health);
    }

}
