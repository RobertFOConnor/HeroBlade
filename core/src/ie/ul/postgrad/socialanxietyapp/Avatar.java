package ie.ul.postgrad.socialanxietyapp;

import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.Player;

/**
 * Created by Robert on 26/03/2017.
 * <p>
 * Represents the players avatar in the game.
 */

public class Avatar {

    private static final float[] BLOND = RGB(255f, 231f, 70f);
    private static final float[] BLACK = RGB(20f, 20f, 20f);
    private static final float[] DARK_BROWN = RGB(69f, 54f, 44f);
    private static final float[] BROWN = RGB(115f, 85f, 59f);
    private static final float[] GINGER = RGB(250f, 149f, 40f);
    private static final float[] GRAY = RGB(100f, 100f, 100f);
    private static final float[] WHITE = RGB(255f, 255f, 255f);
    private static final float[] PEACH = RGB(255f, 209f, 173f);
    private static final float[] TAN = RGB(209f, 158f, 119f);
    private static final float[] GREEN = RGB(145f, 255f, 120f);
    private static final float[] RED = RGB(255f, 59f, 96f);

    private static float[][] skinColorArray = new float[][]{PEACH, TAN, BROWN};
    static float[][] hairColorArray = new float[][]{BLOND, BLACK, GRAY, DARK_BROWN, GINGER, WHITE};
    static float[][] shirtColorArray = new float[][]{BLOND, GREEN, RED, BLACK, WHITE};

    private int shirtColor;
    private int skinColor;
    private int hairtype;
    private int hairColor;

    public Avatar(int shirtColor, int skinColor, int hairType, int hairColor) {
        this.shirtColor = shirtColor;
        this.skinColor = skinColor;
        this.hairtype = hairType;
        this.hairColor = hairColor;
    }

    public int getShirtColor() {
        return shirtColor;
    }

    public void setShirtColor(int shirtColor) {
        if (shirtColor >= shirtColorArray.length) { //No. of hairstyles
            shirtColor = 0;
        }
        this.shirtColor = shirtColor;
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

    public float[] getShirtRGB() {
        return new float[]{shirtColorArray[shirtColor][0], shirtColorArray[shirtColor][1], shirtColorArray[shirtColor][2]};
    }

    public void drawAvatar(Drawer drawer, Player player) {

        drawer.setColor(1, 1, 1, 1);
        drawer.draw(player);


        float[] shirtColor = getShirtRGB();
        drawer.setColor(shirtColor[0], shirtColor[1], shirtColor[2], 1);
        drawer.draw(player.getObject("shirt"));
        drawer.draw(player.getObject("arm"));
        drawer.draw(player.getObject("arm_000"));

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

    private static float[] RGB(float r, float g, float b) {
        return new float[]{r / 255f, g / 255f, b / 255f};
    }
}
