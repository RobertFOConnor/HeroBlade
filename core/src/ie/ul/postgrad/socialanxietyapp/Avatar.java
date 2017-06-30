package ie.ul.postgrad.socialanxietyapp;

import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.Player;

/**
 * Created by Robert on 26/03/2017.
 * <p>
 * Represents the players avatar in the game.
 */

public class Avatar {

    private static final float[] BLOND = {255f / 255f, 231f / 255f, 70f / 255f};
    private static final float[] BLACK = {0.2f, 0.2f, 0.2f};
    private static final float[] BROWN = {115 / 255f, 85 / 255f, 59 / 255f};
    private static final float[] GINGER = {250 / 255f, 149 / 255f, 40 / 255f};
    private static final float[] GRAY = {0.6f, 0.6f, 0.6f};
    private static final float[] WHITE = {1f, 1f, 1f};
    private static final float[] PEACH = {255f / 255f, 209f / 255f, 173f / 255f};
    private static final float[] TAN = {209f / 255f, 158f / 255f, 119f / 255f};

    private static float[][] skinColorArray = new float[][]{PEACH, TAN, BROWN};
    static float[][] hairColorArray = new float[][]{BLOND, BLACK, GRAY, BROWN, GINGER, WHITE};

    private int skinColor;
    private int hairtype;
    private int hairColor;

    public Avatar(int skinColor, int hairType, int hairColor) {
        this.skinColor = skinColor;
        this.hairtype = hairType;
        this.hairColor = hairColor;
    }

    public int getHairtype() {
        return hairtype;
    }

    public void setHairtype(int hairtype) {
        if (hairtype >= 9) { //No. of hairstyles
            hairtype = 0;
        }
        this.hairtype = hairtype;
    }

    public int getHairColor() {
        return hairColor;
    }

    public void setHairColor(int hairColor) {
        if (hairColor >= hairColorArray.length) {
            hairColor = 0;
        }
        this.hairColor = hairColor;
    }

    public int getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(int skinColor) {
        if (skinColor >= skinColorArray.length) {
            skinColor = 0;
        }
        this.skinColor = skinColor;
    }

    public float[] getSkinRGB() {
        return new float[]{skinColorArray[skinColor][0], skinColorArray[skinColor][1], skinColorArray[skinColor][2]};
    }

    public float[] getHairRGB() {
        return new float[]{hairColorArray[hairColor][0], hairColorArray[hairColor][1], hairColorArray[hairColor][2]};
    }

    public void drawAvatar(Drawer drawer, Player player) {

        drawer.setColor(1, 1, 1, 1);
        drawer.draw(player);

        float[] skinColor = getSkinRGB();
        drawer.setColor(skinColor[0], skinColor[1], skinColor[2], 1);
        drawer.draw(player.getObject("head"));
        drawer.draw(player.getObject("hand"));
        drawer.draw(player.getObject("hand_000"));

        float[] hairColor = getHairRGB();
        drawer.setColor(hairColor[0], hairColor[1], hairColor[2], 1);
        drawer.draw(player.getObject("hair"));

        drawer.setColor(1, 1, 1, 1);
        drawer.draw(player.getObject("eyes"));
    }
}
