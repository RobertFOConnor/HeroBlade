package ie.ul.postgrad.socialanxietyapp.game.item;

import android.util.SparseIntArray;

/**
 * Created by Robert on 24-Feb-17.
 * <p>
 * An item which can be equipped by the user and used as a weapon.
 */

public class WeaponItem extends Item {

    private String UID;
    private String type;
    private int damage; //How much damage the weapon can do.
    private int maxHealth; //How many hits the weapon can last.
    private int currHealth; //How much health does the weapon have left.
    private int rarity;
    private int worth;
    private SparseIntArray ingredients;

    protected WeaponItem(int id, String name, String description, String type, int imageID, int worth, int damage, int maxHealth, int rarity, SparseIntArray ingredients) {
        super(id, name, description, imageID, ingredients);
        this.type = type;
        this.worth = worth;
        this.damage = damage;
        this.maxHealth = maxHealth;
        this.currHealth = this.maxHealth;
        this.ingredients = ingredients;
        this.rarity = rarity;
    }

    public int getDamage() {
        return damage;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrHealth() {
        return currHealth;
    }

    public void setCurrHealth(int currHealth) {
        this.currHealth = currHealth;
    }

    public SparseIntArray getIngredients() {
        return ingredients;
    }

    public String getUUID() {
        return UID;
    }

    public void setUUID(String UUID) {
        this.UID = UUID;
    }

    public String getType() {
        return type;
    }

    public int getRarity() {
        return rarity;
    }

    public int getWorth() {
        return worth;
    }
}
