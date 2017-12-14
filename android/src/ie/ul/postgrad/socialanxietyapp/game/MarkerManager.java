package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Vibrator;

import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.factory.MarkerFactory;
import ie.ul.postgrad.socialanxietyapp.screens.MapsActivity;
import ie.ul.postgrad.socialanxietyapp.screens.NearbyLocationsActivity;

/**
 * Created by Robert on 17-Aug-17.
 * <p>
 * Class with static methods for doing operations related to map markers.
 */

public class MarkerManager {

    public static int enemyCount = 0;
    public static final int MARKER_RADIUS = 1000;
    public static final int MARKER_USABLE_RADIUS = 30;

    public static void updateMarker(Context context, GoogleMap mMap, PlaceLikelihood placeLikelihood, ArrayList<Marker> markers, Location mCurrentLocation) {
        // Add a marker for each place near the device's current location, with an
        // info window showing place information.
        String attributions = (String) placeLikelihood.getPlace().getAttributions();
        String snippet = (String) placeLikelihood.getPlace().getName();
        if (attributions != null) {
            snippet = snippet + "\n" + attributions;
        }
        LatLng latLng = placeLikelihood.getPlace().getLatLng();

        if (!markerExists(latLng, markers)) {
            if (placeLikelihood.getPlace().getPlaceTypes().size() > 2) {
                int markerCount = context.getResources().getStringArray(R.array.marker_array_refs).length;
                int id = MarkerFactory.ID_VILLAGE;
                while (id == MarkerFactory.ID_VILLAGE) {
                    id = 1 + (int) (Math.random() * (markerCount - 1));
                }

                if (context.getString(R.string.alphabet_half).contains(snippet.toLowerCase().charAt(0) + "")) {
                    id = MarkerFactory.ID_BLACKSMITH;
                } else {
                    id = MarkerFactory.ID_VILLAGE;
                }

                Marker marker = MarkerFactory.buildMarker(context, mMap, latLng, snippet, id);

                if (!showUsedMarker(marker.getPosition())) {
                    disableMarker(context, marker);
                }
                marker.setVisible(false);
                markers.add(getMarkerPos(marker, markers, mCurrentLocation), marker);
            } else {
                if (showUsedMarker(latLng) && enemyCount < 2) {
                    Marker marker = MarkerFactory.buildMarker(context, mMap, latLng, snippet, MarkerFactory.ID_ENEMY);
                    marker.setVisible(false);
                    markers.add(getMarkerPos(marker, markers, mCurrentLocation), marker);
                    enemyCount++;
                }
            }
        }
    }

    public static void showNearbyMarkers(Context context, ArrayList<Marker> markers, Location currLoc) {
        Intent intent = new Intent(context, NearbyLocationsActivity.class);
        int[] markerIds = new int[9];
        float[] markerDistances = new float[9];

        if (markers.size() < 9) {
            markerIds = new int[markers.size()];
            markerDistances = new float[markers.size()];
        }

        for (int i = 0; i < markerIds.length; i++) {
            markerIds[i] = ((MarkerTag) markers.get(i).getTag()).getId();
            markerDistances[i] = getMarkerDistance(markers.get(i), currLoc);
        }
        intent.putExtra(NearbyLocationsActivity.MARKER_IDS, markerIds);
        intent.putExtra(NearbyLocationsActivity.MARKER_DISTANCES, markerDistances);

        context.startActivity(intent);
    }

    public static float getMarkerDistance(Marker marker, Location mCurrentLocation) {
        Location location = new Location(marker.getId());
        location.setLatitude(marker.getPosition().latitude);
        location.setLongitude(marker.getPosition().longitude);
        return location.distanceTo(mCurrentLocation);
    }

    private static int getMarkerPos(Marker marker, ArrayList<Marker> markers, Location currLoc) {
        int markerPos;

        for (markerPos = 0; markerPos < markers.size(); markerPos++) {
            if (getMarkerDistance(marker, currLoc) < getMarkerDistance(markers.get(markerPos), currLoc)) {
                break;
            }
        }
        return markerPos;
    }

    public static void revealMarkers(Context context, ArrayList<Marker> markers, Location currLoc) {
        //Cycle markers and check for nearby markers. Reveal if player is close.
        for (Marker m : markers) {
            if (getMarkerDistance(m, currLoc) < MARKER_RADIUS && !m.isVisible()) {
                m.setVisible(true);

                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }
        }
    }

    public static void disableMarker(Context context, Marker marker) {
        marker.setTag(new MarkerTag(((MarkerTag) marker.getTag()).getId(), false));
        marker.setIcon(MarkerFactory.getUpdateMarkerIcon(context, MarkerFactory.COOLDONW_MARKER));
    }

    public static boolean markerExists(LatLng latLng, ArrayList<Marker> markers) {
        boolean doesMarkerExist = false;

        for (Marker marker : markers) { //Check if marker has already been placed on map.
            if (marker.getPosition().equals(latLng)) {
                doesMarkerExist = true;
            }
        }
        return doesMarkerExist;
    }

    public static void checkForEndOfMarkerCooldown(Context context, ArrayList<Marker> markers) {
        GameManager gm = GameManager.getInstance();
        for (Marker marker : markers) {
            ConsumedLocation location = gm.hasUsedLocation(marker.getPosition());
            if (location != null) {
                if (location.shouldShow() && !((MarkerTag) marker.getTag()).isEnabled()) {
                    int markerId = ((MarkerTag) marker.getTag()).getId();
                    marker.setIcon(MarkerFactory.getUpdateMarkerIcon(context, markerId));
                    marker.setTag(new MarkerTag(markerId, true));
                }
            }
        }
    }

    public static boolean showUsedMarker(LatLng latLng) {
        boolean show = true;
        ConsumedLocation location = GameManager.getInstance().hasUsedLocation(latLng);
        if (location != null) {
            if (!location.shouldShow()) {
                show = false;
            }
        }
        return show;
    }
}
