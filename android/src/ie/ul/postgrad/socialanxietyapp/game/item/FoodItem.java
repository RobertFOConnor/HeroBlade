package ie.ul.postgrad.socialanxietyapp.game.item;

import android.util.SparseIntArray;

/**
 * Created by Robert on 03-May-17.
 */

public class FoodItem extends Item {

    private int energy;

    FoodItem(int id, String name, String description, int worth, int imageID, SparseIntArray ingredients, int energy) {
        super(id, name, description, worth, imageID, ingredients);
        this.energy = energy;
    }

    public int getEnergy() {
        return energy;
    }
}
