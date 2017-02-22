package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 22-Feb-17.
 */

public class WorldItem extends Item {

    private int markerIconID;
    private int dropItemID;

    public WorldItem(int id, String name, String description, int markerIconID, int dropItemID) {
        super(id, name, description, markerIconID);
        this.markerIconID = markerIconID;
        this.dropItemID = dropItemID;
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
}
