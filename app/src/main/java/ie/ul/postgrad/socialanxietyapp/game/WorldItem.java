package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 22-Feb-17.
 *
 * An item which only appears on the world map.
 */

public class WorldItem extends Item {

    private int markerIconID;
    private int dropItemID;
    private int hitAmount;

    public WorldItem(int id, String name, String description, int markerIconID, int dropItemID, int hitAmount) {
        super(id, name, description, markerIconID);
        this.markerIconID = markerIconID;
        this.dropItemID = dropItemID;
        this.hitAmount = hitAmount;
    }

    public int getMarkerIconID() {
        return markerIconID;
    }

    public int onHit() {
        return (int) (Math.random()*3)+1;
    }

    public int getDropItemID() {
        return dropItemID;
    }

    public int getHitAmount() {
        return hitAmount;
    }

    public void setHitAmount(int hitAmount) {
        this.hitAmount = hitAmount;
    }
}
