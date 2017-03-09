package ie.ul.postgrad.socialanxietyapp.game;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by Robert on 22-Feb-17.
 *
 * Represents the users character. Holds all the information related to the player.
 */

@IgnoreExtraProperties
public class Player {

    private String name;
    private int level;
    private int money;
    private Inventory inventory;
    private WeaponItem weapon;
    private ArrayList<ConsumedLocation> usedLocations;

    public Player() {
        // Default constructor required for calls to DataSnapshot.getValue(Player.class)
    }

    public Player(String name) {
        this.name = name;
        this.level = 1;
        this.money = 0;
        inventory = new Inventory();
        usedLocations = new ArrayList<>();
    }


    public WeaponItem getWeapon() {
        return weapon;
    }

    public void setWeapon(WeaponItem weapon) {
        this.weapon = weapon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void addUsedLocation(LatLng latLng) {
        usedLocations.add(new ConsumedLocation(latLng));
    }

    public boolean hasUsedLocation(LatLng latLng) {
        for(int i = 0; i < usedLocations.size(); i++) {
            if(usedLocations.get(i).getLatLong().equals(latLng)) {
                return true;
            }
        }
        return false;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
