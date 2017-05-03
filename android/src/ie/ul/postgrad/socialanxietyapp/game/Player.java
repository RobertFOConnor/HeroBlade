package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 22-Feb-17.
 * <p>
 * Represents the users character. Holds all the information related to the player.
 */

public class Player {

    private String name;
    private String email;
    private int xp;
    private int level;
    private int money;
    private int weapon_id;
    private int currQuestId;
    private int maxHealth;
    private int currHealth;

    public Player(String name, String email, int xp, int level, int money, int maxHealth, int currHealth) {
        this.name = name;
        this.email = email;
        this.xp = xp;
        this.level = level;
        this.money = money;
        this.weapon_id = -1;
        currQuestId = 1;
        this.maxHealth = maxHealth;
        this.currHealth = currHealth;
    }

    public Player() {
        name = "";
        xp = 0;
        level = 1;
        money = 0;
        weapon_id = -1;
        maxHealth = 10;
        currHealth = maxHealth;
    }

    public int getWeapon() {
        return weapon_id;
    }

    public void setWeapon(int weapon) {
        this.weapon_id = weapon;
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

    public void setXp(int xp) {
        this.xp = xp;

        if (xp >= getXPNeeded()) { //Check for level up
            setLevel(getLevel() + 1);
            setMaxHealth(getMaxHealth() + 5);
            setCurrHealth(getMaxHealth());
        }
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCurrQuestId() {
        return currQuestId;
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
