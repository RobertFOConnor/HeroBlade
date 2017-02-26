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
        items.put(itemID, items.get(itemID) + amount);
    }

    public SparseIntArray getItems() {
        return items;
    }

    public SparseIntArray getWeapons() {
        SparseIntArray arr = new SparseIntArray();
        for (int i = 0; i < items.size(); i++) {
            if (ItemFactory.buildItem(items.keyAt(i)) instanceof WeaponItem) {
                arr.put(items.keyAt(i), items.valueAt(i));
            }
        }
        return arr;
    }
}
