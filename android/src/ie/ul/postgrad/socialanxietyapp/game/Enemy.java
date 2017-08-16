package ie.ul.postgrad.socialanxietyapp.game;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 02-May-17.
 */

public class Enemy {

    private int id;
    private String name;
    private int level;
    private int currHealth;
    private int maxHealth;
    private String type;
    private boolean poisoned = false;

    public Enemy(int id, String name, String type, int level, int maxHealth) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.level = level;
        this.currHealth = maxHealth;
        this.maxHealth = maxHealth;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getCurrHealth() {
        return currHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setCurrHealth(int currHealth) {
        this.currHealth = currHealth;
        if (this.currHealth < 0) {
            this.currHealth = 0;
        }
    }

    public int getTypeDrawableRes() {
        switch (getType()) {
            case WeaponItem.FIRE_TYPE:
                return R.drawable.fire;
            case WeaponItem.WATER_TYPE:
                return R.drawable.water;
            case WeaponItem.GRASS_TYPE:
                return R.drawable.grass;
        }
        return R.drawable.grass;
    }

    public boolean isPoisoned() {
        return poisoned;
    }

    public void setPoisoned(boolean poisoned) {
        this.poisoned = poisoned;
    }
}
