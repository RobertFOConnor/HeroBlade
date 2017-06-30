package ie.ul.postgrad.socialanxietyapp.game.item;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.SparseIntArray;

import ie.ul.postgrad.socialanxietyapp.R;

/**
 * Created by Robert on 27-Jun-17.
 *
 * Class responsible for creating weapons.
 */

public class WeaponFactory {

    private static final int NAME = 0;
    private static final int DESCRIPTION = 1;
    private static final int TYPE = 2;
    private static final int WORTH = 3;
    private static final int DAMAGE = 4;
    private static final int HEALTH = 5;
    private static final int RARITY = 6;

    private static final int INGREDIENT_1_ID = 5; //Index of first ingredient. (if item can be crafted)
    //INFO: Ingredients refer to items which are combined in recipes to craft other items (e.g. string and sticks make a bow etc.)


    public static WeaponItem buildWeapon(Context context, int id) {
        try {
            TypedArray ta = context.getResources().obtainTypedArray(R.array.weapon_array_refs);
            int resId = ta.getResourceId(id, 0);
            TypedArray itemValues = context.getResources().obtainTypedArray(resId);

            String name = itemValues.getString(NAME);
            String description = itemValues.getString(DESCRIPTION);
            String type = itemValues.getString(TYPE);
            int worth = Integer.parseInt(itemValues.getString(WORTH));
            int damage = Integer.parseInt(itemValues.getString(DAMAGE));
            int health = Integer.parseInt(itemValues.getString(HEALTH));
            int rarity = Integer.parseInt(itemValues.getString(RARITY));
            //int imageId = context.getResources().getIdentifier("item_" + String.format("%04d", id), "drawable", context.getPackageName());

            SparseIntArray ingredients = new SparseIntArray();

            for (int i = INGREDIENT_1_ID; i < itemValues.length(); i += 2) {
                int ingredientId = i;
                int ingredientQuantity = i + 1;

                if (itemValues.getString(ingredientId) != null) {
                    int ingredient_1_id = Integer.parseInt(itemValues.getString(ingredientId));
                    int ingredient_1_quantity = Integer.parseInt(itemValues.getString(ingredientQuantity));
                    ingredients.put(ingredient_1_id, ingredient_1_quantity);
                }
            }

            ta.recycle();
            itemValues.recycle();

            return new WeaponItem(id, name, description, type, 0, worth, damage, health, rarity, ingredients);
        } catch (Exception e) {
            return new WeaponItem(2, "Fire Sword", "A sword coated in a bright flame.", "FIRE", 0, 300, 5, 12, 2, new SparseIntArray());
        }
    }


    public static WeaponItem getRandomWeaponByRarity(Context context, int rarity) {
        try {
            TypedArray ta = context.getResources().obtainTypedArray(R.array.weapon_array_refs);
            while (true) {
                int randomWeaponId = ((int) (Math.random() * (ta.length() - 1))) + 1;

                int resId = ta.getResourceId(randomWeaponId, 0);
                TypedArray itemValues = context.getResources().obtainTypedArray(resId);
                int randomRarity = Integer.parseInt(itemValues.getString(RARITY));

                if (rarity == randomRarity) {
                    ta.recycle();
                    itemValues.recycle();
                    return buildWeapon(context, randomWeaponId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return buildWeapon(context, 2); //Default to fire sword
        }
    }
}
