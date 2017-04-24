package ie.ul.postgrad.socialanxietyapp.game.item;

import android.util.SparseIntArray;

/**
 * Created by Robert on 24-Feb-17.
 * <p>
 * An item which can be equipped by the user and used as a weapon.
 */

public class WeaponItem extends Item {

    private int damage; //How much damage the weapon can do.
    private int durability; //How many hits the weapon can last.
    private SparseIntArray ingredients;

    protected WeaponItem(int id, String name, String description, int imageID, int damage, int durability, SparseIntArray ingredients) {
        super(id, name, description, imageID, ingredients);
        this.damage = damage;
        this.durability = durability;
        this.ingredients = ingredients;
    }

    public int getDamage() {
        return damage;
    }

    public int getDurability() {
        return durability;
    }

    public SparseIntArray getIngredients() {
        return ingredients;
    }
}
