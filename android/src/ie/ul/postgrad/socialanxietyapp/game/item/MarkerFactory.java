package ie.ul.postgrad.socialanxietyapp.game.item;

import android.content.Context;
import android.content.res.TypedArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.MarkerTag;

/**
 * Created by Robert on 04-Apr-17.
 * <p>
 * Class for creating Google map markers.
 */

public class MarkerFactory {

    public static final int ID_ENEMY = 1;
    public static final int ID_VILLAGE = 2;
    public static final int ID_BLACKSMITH = 3;
    public static final int COOLDONW_MARKER = 100;

    public static Marker buildMarker(Context context, GoogleMap googleMap, LatLng latlng, String snippet, int id) {

        TypedArray ta = context.getResources().obtainTypedArray(R.array.marker_array_refs);
        int resId = ta.getResourceId(id, 0);
        TypedArray itemValues = context.getResources().obtainTypedArray(resId);


        @SuppressWarnings("ResourceType")
        String name = itemValues.getString(0);
        @SuppressWarnings("ResourceType")
        String desc = itemValues.getString(1);
        int imageId = context.getResources().getIdentifier("marker_" + String.format("%04d", id), "drawable", context.getPackageName());

        ta.recycle();
        itemValues.recycle();

        if (id != ID_ENEMY) {
            if (snippet.contains(" ")) {
                name = snippet.substring(0, snippet.indexOf(" ")) + " " + name;
            } else {
                name = snippet + " " + name;
            }
        }

        final Marker m = googleMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title(name)
                .icon(BitmapDescriptorFactory.fromResource(imageId))
                .snippet(desc));

        m.setTag(new MarkerTag(id, true));
        return m;
    }

    public static BitmapDescriptor getUpdateMarkerIcon(Context context, int id) {
        return BitmapDescriptorFactory.fromResource(context.getResources().getIdentifier("marker_" + String.format("%04d", id), "drawable", context.getPackageName()));
    }

}
