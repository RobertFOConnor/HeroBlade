package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.item.IDs;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 20-Feb-17.
 * <p>
 * The players inventory. Holds all the items collected by the user.
 */

public class Inventory {

    private Context context;
    private InventoryItemArray items;
    private ArrayList<WeaponItem> weapons;

    public Inventory() {
        items = new InventoryItemArray();
        weapons = new ArrayList<>();
    }

    public Inventory(InventoryItemArray items, ArrayList<WeaponItem> weapons, Context context) {
        this.items = items;
        this.weapons = weapons;
        this.context = context;
    }

    public void addItem(int itemID, int amount) {
        if (IDs.isWeapon(itemID)) {
            weapons.add((WeaponItem) ItemFactory.buildItem(context, itemID));
        } else {
            items.put(itemID, items.get(itemID) + amount);
        }
    }

    public void removeItem(int itemID, int amount) {
        if (IDs.isWeapon(itemID)) {

        } else {

            if ((items.get(itemID) - amount) <= 0) {
                items.delete(itemID);
            } else {
                items.put(itemID, items.get(itemID) - amount);
            }
        }
    }

    public InventoryItemArray getItems() {
        return items;
    }

    public ArrayList<WeaponItem> getWeapons() {
        return weapons;
    }
}
