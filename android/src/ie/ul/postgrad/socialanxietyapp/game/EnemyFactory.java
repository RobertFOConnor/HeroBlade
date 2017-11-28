package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;
import android.content.res.TypedArray;

import ie.ul.postgrad.socialanxietyapp.R;

/**
 * Created by Robert on 02-May-17.
 */

public class EnemyFactory {

    public static final int ENEMY_COUNT = 6;

    private static final int NAME = 0;
    private static final int BASE_LEVEL = 1;
    private static final int TYPE = 2;
    private static final int POISON = 3;
    private static final int HEAL = 4;

    //temp String[] enemyNames = {"Giant Spider", "Skeleton", "Robo_Brain", "Fire Demon", "Angler-Man", "Sharkhead", "Turnip-Legs", "Dark Angel", "Gremlin", "Scissor-Man", "Wolf-Man", "Giant", "Evil Crab"};

    public static Enemy buildEnemy(Context context, Player player, int id) {

        try {

            TypedArray ta = context.getResources().obtainTypedArray(R.array.enemy_array_refs);
            int resId = ta.getResourceId(id, 0);
            TypedArray itemValues = context.getResources().obtainTypedArray(resId);


            String name = itemValues.getString(NAME);
            int baseLevel = Integer.parseInt(itemValues.getString(BASE_LEVEL));
            String type = itemValues.getString(TYPE);

            int level = (player.getLevel() - 3) + ((int) (Math.random() * 7)) + baseLevel;
            if (level < 1) {
                level = 1;
            }

            if (player.getLevel() < 3) {
                level = 1;
            }

            int health = 5 + ((level - 1) * 5) + ((int) (Math.random() * 31));

            ta.recycle();
            itemValues.recycle();

            return new Enemy(id, name, type, level, health);
        } catch (Exception e) {
            e.printStackTrace();
            return new Enemy(1, "Giant Spider", "GRASS", 2, 15);
        }
    }

}
