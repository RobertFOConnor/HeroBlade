package ie.ul.postgrad.socialanxietyapp.game;

/**
 * Created by Robert on 16-Aug-17.
 */

public class MarkerTag {
    private int id = 0;
    private boolean enabled;

    public MarkerTag(int id, boolean enabled) {
        this.id = id;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
