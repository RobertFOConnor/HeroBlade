package ie.ul.postgrad.socialanxietyapp.game;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 20-Feb-17.
 * <p>
 * The players inventory. Holds all the items collected by the user.
 */

public class Inventory {

    private InventoryItemArray items;
    private ArrayList<WeaponItem> weapons;
    private ArrayList<ChestItem> chests;

    private static final int GET_EQUIPED_WEAPONS = 1;
    private static final int GET_UNEQUIPED_WEAPONS = 2;


    public Inventory(InventoryItemArray items, ArrayList<WeaponItem> weapons, ArrayList<ChestItem> chests) {
        this.items = items;
        this.weapons = weapons;
        this.chests = chests;
    }

    public boolean hasUsableWeapons() {
        for (WeaponItem weapon : getEquippedWeapons()) {
            if (weapon.getCurrHealth() > 0) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<WeaponItem> getEquippedWeapons() {
        return getWeaponList(GET_EQUIPED_WEAPONS);
    }

    public ArrayList<WeaponItem> getUnequippedWeapons() {
        return getWeaponList(GET_UNEQUIPED_WEAPONS);
    }

    public ArrayList<WeaponItem> getWeaponList(int listCode) {
        ArrayList<WeaponItem> weaponItems = new ArrayList<>();
        for (WeaponItem weaponItem : weapons) {
            switch (listCode) {
                case GET_EQUIPED_WEAPONS:
                if (weaponItem.isEquipped()) {
                    weaponItems.add(weaponItem);
                }
                break;
                case GET_UNEQUIPED_WEAPONS:
                    if (!weaponItem.isEquipped()) {
                        weaponItems.add(weaponItem);
                    }
                    break;
            }
        }
        return weaponItems;
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
