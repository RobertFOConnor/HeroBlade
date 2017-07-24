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

    public Player(String id, String name, int xp, int level, int money, int maxHealth, int currHealth) {
        this.id = id;
        this.name = name;
        this.xp = xp;
        this.level = level;
        this.money = money;
        this.maxHealth = maxHealth;
        this.currHealth = currHealth;
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

        if (xp >= getXPNeeded()) { //Check for level up
            setLevel(getLevel() + 1);
            setMaxHealth(getMaxHealth() + 5);
            setCurrHealth(getMaxHealth());
            return true;
        }
        return false;
    }

    public int getXPNeeded() {
        int xpNeeded = 0;
        for (int i = 0; i <= level; i++) {
            xpNeeded += XPLevels.XP_LEVELS[i];
        }
        return xpNeeded;
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

    /*
    public void addUsedLocation(LatLng latLng) {
        usedLocations.add(new ConsumedLocation(latLng));
    }

    public boolean hasUsedLocation(LatLng latLng) {
        for (int i = 0; i < usedLocations.size(); i++) {
            if (usedLocations.get(i).getLatLong().equals(latLng)) {
                return true;
            }
        }
        return false;
    }*/
}
