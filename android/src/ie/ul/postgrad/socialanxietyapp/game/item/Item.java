package ie.ul.postgrad.socialanxietyapp.game.item;

import android.content.Context;
import android.util.SparseIntArray;

import ie.ul.postgrad.socialanxietyapp.R;

/**
 * Created by Robert on 20-Feb-17.
 *
 * The base class for all items in the game.
 */

public class Item {

    private int id;
    private String name;
    private String description;
    private int worth;
    private int imageID;

    private SparseIntArray ingredients;


    Item(int id, String name, String description, int imageID, SparseIntArray ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageID = imageID;
        this.ingredients = ingredients;
        worth = 0;
    }

    Item(int id, String name, String description, int worth, int imageID, SparseIntArray ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.worth = worth;
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

    public int getWorth() {
        return worth;
    }

    public int getImageID() {
        return imageID;
    }

    public SparseIntArray getIngredients() {
        return ingredients;
    }

    public String getIngredientsString(Context context) {
        String s = context.getString(R.string.requires);

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
