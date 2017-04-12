package ie.ul.postgrad.socialanxietyapp.game.item;

import android.content.Context;
import android.content.res.TypedArray;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.quest.Quest;

/**
 * Created by Robert on 04-Apr-17.
 */

public class MarkerFactory {

    public static Marker buildMarker(Context context, GoogleMap googleMap, LatLng latlng, int id) {

        TypedArray ta = context.getResources().obtainTypedArray(R.array.marker_array_refs);
        int resId = ta.getResourceId(id, 0);
        TypedArray itemValues = context.getResources().obtainTypedArray(resId);


        String name = itemValues.getString(0);
        String description = itemValues.getString(1);
        int imageId = context.getResources().getIdentifier("marker_" + String.format("%04d", id), "drawable", context.getPackageName());

        ta.recycle();
        itemValues.recycle();


        final Marker m = googleMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title(name)
                .icon(BitmapDescriptorFactory.fromResource(imageId))
                .snippet(description));

        m.setTag(id);
        return m;
    }

    public static Marker buildQuestMarker(Context context, GoogleMap googleMap, LatLng latlng, Quest quest) {

        String name = quest.getTitle();
        String description = quest.getDescription();
        int imageId = context.getResources().getIdentifier("marker_" + String.format("%04d", 2), "drawable", context.getPackageName());

        final Marker m = googleMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title(name)
                .icon(BitmapDescriptorFactory.fromResource(imageId))
                .snippet(description));

        m.setTag(2);
        return m;
    }
}
