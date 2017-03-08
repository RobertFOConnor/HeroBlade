package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 20-Feb-17.
 *
 * The base class for all items in the game.
 */

public class Item {

    private int id;
    private String name;
    private String description;
    private int imageID;


    public Item(int id, String name, String description, int imageID) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageID = imageID;
    }

    public int getId() {
        return id;
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
}
