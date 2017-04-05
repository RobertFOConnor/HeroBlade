package ie.ul.postgrad.socialanxietyapp.game.item;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.SparseIntArray;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ie.ul.postgrad.socialanxietyapp.R;

/**
 * Created by Robert on 04-Apr-17.
 */

public class MarkerFactory {

    public static Marker buildMarker(Context context, GoogleMap googleMap, Place place, int id) {

        TypedArray ta = context.getResources().obtainTypedArray(R.array.marker_array_refs);
        int resId = ta.getResourceId(id, 0);
        TypedArray itemValues = context.getResources().obtainTypedArray(resId);


        String name = itemValues.getString(0);
        String description = itemValues.getString(1);
        int imageId = context.getResources().getIdentifier("marker_" + String.format("%04d", id), "drawable", context.getPackageName());

        ta.recycle();
        itemValues.recycle();


        final Marker m = googleMap.addMarker(new MarkerOptions()
                .position(place.getLatLng())
                .title(name)
                .icon(BitmapDescriptorFactory.fromResource(imageId))
                .snippet(description));

        m.setTag(id);
        return m;
    }
}
