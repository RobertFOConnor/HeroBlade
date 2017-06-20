package ie.ul.postgrad.socialanxietyapp.game.item;

import android.util.SparseIntArray;

/**
 * Created by Robert on 24-Feb-17.
 * <p>
 * An item which can be equipped by the user and used as a weapon.
 */

public class WeaponItem extends Item {

    private String UUID;
    private int damage; //How much damage the weapon can do.
    private int critDamage; //How much damage a critical hit can do.
    private int maxHealth; //How many hits the weapon can last.
    private int currHealth; //How much health does the weapon have left.
    private SparseIntArray ingredients;

    protected WeaponItem(int id, String name, String description, int imageID, int damage, int critDamage, int maxHealth, int currHealth, SparseIntArray ingredients) {
        super(id, name, description, imageID, ingredients);
        this.damage = damage;
        this.critDamage = critDamage;
        this.maxHealth = maxHealth;
        this.currHealth = currHealth;
        this.ingredients = ingredients;
    }

    public int getDamage() {
        return damage;
    }

    public int getCritDamage() {
        return critDamage;
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
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
