package ie.ul.postgrad.socialanxietyapp.game;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Robert on 24-Feb-17.
 */

public class ConsumedLocation {

    private static final long RESPAWN_TIME = 50000;

    private LatLng latLong;
    private long timeUsed;

    public ConsumedLocation(LatLng latLong) {
        this.latLong = latLong;
        timeUsed = System.nanoTime();
    }

    public boolean shouldRespawn() {
        //check if location should respawn for user.
        return false;
    }

    public long getTimeUsed() {
        return timeUsed;
    }

    public LatLng getLatLong() {
        return latLong;
    }
}
