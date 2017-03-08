package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 20-Feb-17.
 *
 * The players inventory. Holds all the items collected by the user.
 */

public class Inventory {

    private InventoryItemArray items;

    public Inventory() {
        items = new InventoryItemArray();
    }

    public void addItem(int itemID, int amount) {
        items.put(itemID, items.get(itemID) + amount);
    }

    public void removeItem(int itemID, int amount) {
        if ((items.get(itemID) - amount) <= 0) {
            items.delete(itemID);
        } else {
            items.put(itemID, items.get(itemID) - amount);
        }
    }


    public InventoryItemArray getItems() {
        return items;
    }

    public InventoryItemArray getWeapons() {
        InventoryItemArray arr = new InventoryItemArray();
        for (int i = 0; i < items.size(); i++) {
            if (ItemFactory.buildItem(items.keyAt(i)) instanceof WeaponItem) {
                arr.put(items.keyAt(i), items.valueAt(i));
            }
        }
        return arr;
    }
}
