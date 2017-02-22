package ie.ul.postgrad.socialanxietyapp.game;

import ie.ul.postgrad.socialanxietyapp.R;

/**
 * Created by Robert on 22-Feb-17.
 */

public class ItemFactory {

    public static final int TREE_ID = 3;
    public static final int ROCK_ID = 4;


    public static Item buildItem(int id) {

        switch (id) {

            case 0:
                return new Item(0, "Cobblestone", "A rough gray stone.", R.drawable.cobblestone);

            case 1:
                return new Item(1, "Wood", "A wooden plank.", R.drawable.wood);

            case 2:
                return new Item(2, "Coal", "A chunk of coal.", R.drawable.wood);

            case 3:
                return new WorldItem(3, "Tree", "A tree.", R.drawable.tree_1, 1);

            case 4:
                return new WorldItem(4, "Rock", "A large rock.", R.drawable.rock_1, 0);

            default:
                return null;
        }
    }
}
