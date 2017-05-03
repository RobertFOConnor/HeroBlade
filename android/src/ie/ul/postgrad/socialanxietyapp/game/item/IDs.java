package ie.ul.postgrad.socialanxietyapp.game.item;

/**
 * Created by Robert on 03-May-17.
 */

public class IDs {

    public static final int WOOD_SWORD = 59;
    public static final int GOOD_SWORD = 60;
    public static final int GOLD_SWORD = 61;
    public static final int RUBY_SWORD = 62;

    private static int[] WEAPONS = new int[]{WOOD_SWORD, GOOD_SWORD, GOLD_SWORD, RUBY_SWORD};

    public static boolean isWeapon(int id) {
        for(int i : WEAPONS) {
            if(i == id) {
                return true;
            }
        }
        return false;
    }

}
