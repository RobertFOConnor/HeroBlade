package ie.ul.postgrad.socialanxietyapp.game.item;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.SparseIntArray;

import ie.ul.postgrad.socialanxietyapp.R;

/**
 * Created by Robert on 22-Feb-17.
 * <p>
 * Factory for creating game items. All items are created through this class.
 */

public class ItemFactory {

    private static final int NAME = 0;
    private static final int DESCRIPTION = 1;
    private static final int WORTH = 2;
    private static final int DAMAGE = 3;
    private static final int ENERGY = 4;

    private static final int INGREDIENT_1_ID = 5; //Index of first ingredient. (if item can be crafted)
    //INFO: Ingredients refer to items which are combined in recipes to craft other items (e.g. string and sticks make a bow etc.)


    public static Item buildItem(Context context, int id) {

        TypedArray ta = context.getResources().obtainTypedArray(R.array.item_array_refs);
        int resId = ta.getResourceId(id, 0);
        TypedArray itemValues = context.getResources().obtainTypedArray(resId);


        String name = itemValues.getString(NAME);
        String description = itemValues.getString(DESCRIPTION);
        int imageId = context.getResources().getIdentifier("item_" + String.format("%04d", id), "drawable", context.getPackageName());
        int damage = Integer.parseInt(itemValues.getString(DAMAGE));

        SparseIntArray ingredients = new SparseIntArray();

        for(int i = INGREDIENT_1_ID; i < itemValues.length(); i+=2) {
            int ingredientId = i;
            int ingredientQuantity = i+1;

            if(itemValues.getString(ingredientId) != null) {
                int ingredient_1_id = Integer.parseInt(itemValues.getString(ingredientId));
                int ingredient_1_quantity = Integer.parseInt(itemValues.getString(ingredientQuantity));
                ingredients.put(ingredient_1_id, ingredient_1_quantity);
            }
        }

        ta.recycle();
        itemValues.recycle();


        if(damage > -1) {
            return new WeaponItem(id, name, description, imageId, 0, 0, ingredients);
        } else {
            return new Item(id, name, description, imageId, ingredients);
        }

    }
}
