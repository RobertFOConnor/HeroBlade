package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 02-May-17.
 */

public class Enemy {

    private String name;
    private int level;
    private int currHealth;
    private int maxHealth;

    public Enemy(String name, int level, int maxHealth) {
        this.name = name;
        this.level = level;
        this.currHealth = maxHealth;
        this.maxHealth = maxHealth;
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
    }
}
