package ie.ul.postgrad.socialanxietyapp.game;

import android.util.SparseIntArray;

/**
 * Created by Robert on 20-Feb-17.
 */

public class Inventory {

    SparseIntArray items;

    public Inventory() {
        items = new SparseIntArray();
    }

    public void addItem(int itemID) {
        items.put(itemID, items.get(itemID)+1);
    }

    public SparseIntArray getItems() {
        return items;
    }
}
