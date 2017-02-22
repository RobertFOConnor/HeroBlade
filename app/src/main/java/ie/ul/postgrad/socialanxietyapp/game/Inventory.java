package ie.ul.postgrad.socialanxietyapp.game;

import java.util.ArrayList;

/**
 * Created by Robert on 20-Feb-17.
 */

public class Inventory {

    private ArrayList<Item> inventoryItems;

    public Inventory() {
        inventoryItems = new ArrayList<>();
    }

    public void addItem(Item item) {
        inventoryItems.add(item);
    }
}
