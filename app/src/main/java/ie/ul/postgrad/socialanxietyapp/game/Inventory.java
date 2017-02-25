package ie.ul.postgrad.socialanxietyapp.game;

import android.util.SparseIntArray;

/**
 * Created by Robert on 20-Feb-17.
 */

public class Inventory {

    private SparseIntArray items;

    public Inventory() {
        items = new SparseIntArray();
    }

    public void addItem(int itemID, int amount) {
        items.put(itemID, items.get(itemID)+amount);
    }

    public SparseIntArray getItems() {
        return items;
    }
}
