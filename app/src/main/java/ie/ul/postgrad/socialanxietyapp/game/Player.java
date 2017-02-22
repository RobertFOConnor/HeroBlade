package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 22-Feb-17.
 */

public class Player {

    private String name;
    private int level;
    private Inventory inventory;

    private Item weapon;


    public Player(String name) {
        this.name = name;
        this.level = 1;
        inventory = new Inventory();
    }


    public Item getWeapon() {
        return weapon;
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
}
