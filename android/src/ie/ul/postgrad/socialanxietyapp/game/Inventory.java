package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;
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
    private ArrayList<ChestItem> chests;

    public Inventory() {
        items = new InventoryItemArray();
        weapons = new ArrayList<>();
        chests = new ArrayList<>();
    }

    public Inventory(InventoryItemArray items, ArrayList<WeaponItem> weapons, ArrayList<ChestItem> chests, Context context) {
        this.items = items;
        this.weapons = weapons;
        this.chests = chests;
        this.context = context;
    }

    public void addItem(int itemID, int amount) {
        Item item = ItemFactory.buildItem(context, itemID);
        if (item instanceof WeaponItem) {
            weapons.add((WeaponItem) item);
        } else {
            items.put(itemID, items.get(itemID) + amount);
        }
    }

    public void removeItem(int itemID, int amount) {
        if (!(ItemFactory.buildItem(context, itemID) instanceof WeaponItem)) {
            if ((items.get(itemID) - amount) <= 0) {
                items.delete(itemID);
            } else {
                items.put(itemID, items.get(itemID) - amount);
            }
        }
    }

    public ArrayList<WeaponItem> getEquippedWeapons() {
        ArrayList<WeaponItem> equippedWeapons = new ArrayList<>();
        for (WeaponItem weaponItem : weapons) {
            if (weaponItem.isEquipped()) {
                equippedWeapons.add(weaponItem);
            }
        }
        return equippedWeapons;
    }

    public ArrayList<WeaponItem> getUnequippedWeapons() {
        ArrayList<WeaponItem> unequippedWeapons = new ArrayList<>();
        for (WeaponItem weaponItem : weapons) {
            if (!weaponItem.isEquipped()) {
                unequippedWeapons.add(weaponItem);
            }
        }
        return unequippedWeapons;
    }

    public InventoryItemArray getItems() {
        return items;
    }

    public ArrayList<WeaponItem> getWeapons() {
        return weapons;
    }

    public ArrayList<ChestItem> getChests() {
        return chests;
    }

    public WeaponItem getWeapon(String UUID) {
        for (WeaponItem w : weapons) {
            if (w.getUUID().equals(UUID)) {
                return w;
            }
        }
        return null;
    }
}
