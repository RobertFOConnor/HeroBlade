package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;

public class NearbyLocationsActivity extends AppCompatActivity {

    public static final String MARKER_IDS = "marker_ids";
    public static final String MARKER_DISTANCES = "marker_distances";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_locations);
        addMarkersToGrid();
        setupButtonListener();
    }

    private void addMarkersToGrid() {
        GridLayout markerGrid = findViewById(R.id.marker_grid);
        Context c = getApplicationContext();
        LinearLayout markerView;
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int[] markerIds;
        float[] markerDistances;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            markerIds = bundle.getIntArray(MARKER_IDS);
            markerDistances = bundle.getFloatArray(MARKER_DISTANCES);

            try {
                for (int i = 0; i < markerIds.length; i++) {
                    markerView = (LinearLayout) inflater.inflate(R.layout.nearby_marker_display, null);
                    ((ImageView) markerView.findViewById(R.id.marker_image)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), (c.getResources().getIdentifier("marker_" + String.format("%04d", markerIds[i]), "drawable", c.getPackageName()))));
                    String distanceText = (int) markerDistances[i] + "m";
                    ((TextView) markerView.findViewById(R.id.marker_text)).setText(distanceText);
                    markerGrid.addView(markerView);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupButtonListener() {
        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                SoundManager.getInstance(getApplicationContext()).playSound(SoundManager.Sound.BACK);
            }
        });
    }
}
