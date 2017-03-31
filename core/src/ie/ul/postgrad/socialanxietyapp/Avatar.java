package ie.ul.postgrad.socialanxietyapp;

/**
 * Created by Bobbalicious on 26/03/2017.
 *
 * Represents the players avatar in the game.
 *
 */

public class Avatar {

    private int hairtype;
    private int hairColor;

    public Avatar(int hairType, int hairColor) {
        this.hairtype = hairType;
        this.hairColor = hairColor;
    }

    public int getHairtype() {
        return hairtype;
    }

    public void setHairtype(int hairtype) {
        this.hairtype = hairtype;
    }

    public int getHairColor() {
        return hairColor;
    }

    public void setHairColor(int hairColor) {
        this.hairColor = hairColor;
    }
}
