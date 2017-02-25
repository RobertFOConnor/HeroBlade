package ie.ul.postgrad.socialanxietyapp.game;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

/**
 * Created by Robert on 22-Feb-17.
 */

public class Player {

    private String name;
    private int level;
    private Inventory inventory;
    private Item weapon;
    private ArrayList<ConsumedLocation> usedLocations;


    public Player(String name) {
        this.name = name;
        this.level = 1;
        inventory = new Inventory();
        usedLocations = new ArrayList<>();
    }


    public Item getWeapon() {
        return weapon;
    }

    public void setWeapon(Item weapon) {
        this.weapon = weapon;
    }

    public String getName() {
        return name;
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
}
