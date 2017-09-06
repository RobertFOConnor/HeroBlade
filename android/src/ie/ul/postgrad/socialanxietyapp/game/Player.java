package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 22-Feb-17.
 * <p>
 * Represents the users character. Holds all the information related to the player.
 */

public class Player {

    private String id;
    private String name;
    private int xp;
    private int level;
    private int money;
    private int maxHealth;
    private int currHealth;
    private String baseSword;

    public Player(String id, String name, int xp, int level, int money, int maxHealth, int currHealth, String baseSword) {
        this.id = id;
        this.name = name;
        this.xp = xp;
        this.level = level;
        this.money = money;
        this.maxHealth = maxHealth;
        this.currHealth = currHealth;
        this.baseSword = baseSword;
    }

    public Player() {
        id = "";
        name = "";
        xp = 0;
        level = 1;
        money = 0;
        maxHealth = 30;
        currHealth = maxHealth;
    }

    public String getBaseSword() {
        return baseSword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getXp() {
        return xp;
    }

    public boolean setXp(int xp) {
        this.xp = xp;
        return (this.xp >= XPLevels.XP_LEVELS[level]);//Check for level up
    }

    public int getLevel() {
        return level;
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

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getCurrHealth() {
        return currHealth;
    }

    public void setCurrHealth(int currHealth) {
        this.currHealth = currHealth;
        if (currHealth > maxHealth) {
            this.currHealth = maxHealth;
        } else if (currHealth < 0) {
            this.currHealth = 0;
        }
    }
}
