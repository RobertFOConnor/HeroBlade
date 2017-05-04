package ie.ul.postgrad.socialanxietyapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NearbyLocationsActivity extends AppCompatActivity {

    private GridLayout markerGrid;
    public static final String MARKER_IDS = "marker_ids";
    public static final String MARKER_DISTANCES = "marker_distances";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_locations);

        markerGrid = (GridLayout) findViewById(R.id.marker_grid);

        Context c = getApplicationContext();

        LinearLayout markerView;
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int[] markerIds;
        float[] markerDistances;

        Bundle bundle = getIntent().getExtras();
        markerIds = bundle.getIntArray(MARKER_IDS);
        markerDistances = bundle.getFloatArray(MARKER_DISTANCES);

        for (int i = 0; i < markerIds.length; i++) {
            System.out.println("VAL: " + i + " " + markerIds[i]);
        }

        for (int i = 0; i < markerIds.length; i++) {
            markerView = (LinearLayout) inflater.inflate(R.layout.nearby_marker_display, null);
            ((ImageView) markerView.findViewById(R.id.marker_image)).setImageDrawable(c.getResources().getDrawable(c.getResources().getIdentifier("marker_" + String.format("%04d", markerIds[i]), "drawable", c.getPackageName()), getTheme()));
            ((TextView) markerView.findViewById(R.id.marker_text)).setText((int) markerDistances[i] + "m");

            markerGrid.addView(markerView);
        }

        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
