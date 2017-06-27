package ie.ul.postgrad.socialanxietyapp.game.item;

import android.util.SparseIntArray;

/**
 * Created by Robert on 03-May-17.
 */

public class ChestItem extends Item {

    public static final int NORMAL_CHEST = 74;
    public static final int GOLD_CHEST = 75;
    public static final int RARE_CHEST = 76;

    private String UID;
    private int itemID;
    private String name;
    private String description;
    private int imageID;

    private float maxDistance;
    private float currDistance;

    public ChestItem(int itemID, String name, String description, int imageID, float maxDistance) {
        super(itemID, name, description, imageID, new SparseIntArray());
        this.itemID = itemID;
        this.name = name;
        this.description = description;
        this.imageID = imageID;
        this.maxDistance = maxDistance * 1000; // in meters.
        this.currDistance = maxDistance * 1000;
    }

    public String getUID() {
        return UID;
    }

    public int getItemID() {
        return itemID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageID() {
        return imageID;
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public float getCurrDistance() {
        return currDistance;
    }

    public void setCurrDistance(float currDistance) {
        this.currDistance = currDistance;
    }
}
