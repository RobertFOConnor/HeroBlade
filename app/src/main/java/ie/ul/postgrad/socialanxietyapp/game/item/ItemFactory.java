package ie.ul.postgrad.socialanxietyapp.game.item;

import android.util.SparseIntArray;

import ie.ul.postgrad.socialanxietyapp.R;

/**
 * Created by Robert on 22-Feb-17.
 *
 * Factory for creating game items. All items are created through this class.
 */

public class ItemFactory {


    public static Item buildItem(int id) {
        SparseIntArray ingredients = new SparseIntArray();

        switch (id) {

            case 0:
                return new Item(id, "Cobblestone", "A rough gray stone.", R.drawable.cobblestone);

            case 1:
                return new Item(id, "Wood", "A wooden plank.", R.drawable.wood);

            case 2:
                return new Item(id, "Coal", "A chunk of coal.", R.drawable.wood);

            case 3:
                return new Item(id, "Grass Tuft", "A tuft of grass.", R.drawable.ic_grass_tuft);

            case 4:
                return new Item(id, "Gold Nugget", "A golden nugget.", R.drawable.ic_gold_nugget);

            case 300:
                return new WorldItem(id, "Tree", "A tree.", R.drawable.tree_1, 1, 10);

            case 301:
                return new WorldItem(id, "Rock", "A large rock.", R.drawable.rock_1, 0, 7);

            case 302:
                return new WorldItem(id, "Grass", "A patch of grass.", R.drawable.grass, 3, 3);

            case 500:
                return new WorldItem(id, "Trading Post", "A place to do business.", R.drawable.trading_post, 0, -1);

            case 600:
                ingredients.put(1, 15);
                ingredients.put(609, 1); // 1 rope
                return new WeaponItem(id, "Wooden Sword", "A wooden sword.", R.drawable.ic_wood_sword, 3, 25, ingredients);

            case 601:
                ingredients.put(1, 15);
                ingredients.put(609, 1); // 1 rope
                return new WeaponItem(id, "Wooden Axe", "A poorly crafted and blunt wooden axe.", R.drawable.ic_wood_axe, 3, 25, ingredients);

            case 602:
                ingredients.put(1, 15);
                ingredients.put(609, 1); // 1 rope
                return new WeaponItem(id, "Wooden Pickaxe", "A poorly crafted and blunt wooden pick-axe.", R.drawable.ic_wood_pickaxe, 3, 25, ingredients);

            case 603:
                ingredients.put(1, 10);
                ingredients.put(0, 10);
                ingredients.put(609, 1); // 1 rope
                return new WeaponItem(id, "Stone Sword", "A wooden sword.", R.drawable.ic_stone_sword, 3, 25, ingredients);

            case 604:
                ingredients.put(1, 10);
                ingredients.put(0, 10);
                ingredients.put(609, 1); // 1 rope
                return new WeaponItem(id, "Stone Axe", "A poorly crafted and blunt wooden axe.", R.drawable.ic_stone_axe, 3, 25, ingredients);

            case 605:
                ingredients.put(1, 10);
                ingredients.put(0, 10);
                ingredients.put(609, 1); // 1 rope
                return new WeaponItem(id, "Stone Pickaxe", "A poorly crafted and blunt wooden pick-axe.", R.drawable.ic_stone_pickaxe, 3, 25, ingredients);

            case 606:
                ingredients.put(1, 15);
                ingredients.put(4, 5);
                ingredients.put(609, 1); // 1 rope
                return new WeaponItem(id, "Gold Sword", "A poorly crafted and blunt wooden pick-axe.", R.drawable.ic_gold_sword, 3, 25, ingredients);

            case 607:
                ingredients.put(1, 15);
                ingredients.put(4, 5);
                ingredients.put(609, 1); // 1 rope
                return new WeaponItem(id, "Gold Axe", "A poorly crafted and blunt wooden pick-axe.", R.drawable.ic_gold_axe, 3, 25, ingredients);

            case 608:
                ingredients.put(1, 15);
                ingredients.put(4, 7);
                ingredients.put(609, 1); // 1 rope
                return new WeaponItem(id, "Gold Pickaxe", "A poorly crafted and blunt wooden pick-axe.", R.drawable.ic_gold_pickaxe, 3, 25, ingredients);

            case 609:
                ingredients.put(3, 3);
                return new WeaponItem(id, "Rope", "A short length of grass-woven rope.", R.drawable.ic_rope, 1, 25, ingredients);

            default:
                return new Item(-1, "Bagel", "A tasty bagel.", R.drawable.bagel);
        }
    }
}
