package ie.ul.postgrad.socialanxietyapp.game.item;

import android.content.Context;
import android.util.SparseIntArray;

/**
 * Created by Robert on 20-Feb-17.
 *
 * The base class for all items in the game.
 */

public class Item {

    private int id;
    private String name;
    private String description;
    private int imageID;

    private SparseIntArray ingredients;


    protected Item(int id, String name, String description, int imageID, SparseIntArray ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageID = imageID;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageID() {
        return imageID;
    }

    public SparseIntArray getIngredients() {
        return ingredients;
    }

    public String getIngredientsString(Context context) {
        String s = "Requires: ";

        for (int i = 0; i < ingredients.size(); i++) {
            s += ingredients.valueAt(i) + " " + ItemFactory.buildItem(context, ingredients.keyAt(i)).getName();
            if (i >= ingredients.size()-1) {
                s += ".";
            } else {
                s += ", ";
            }
        }
        return s;
    }
}
