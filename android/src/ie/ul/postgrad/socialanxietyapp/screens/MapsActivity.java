package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.AndroidLauncher;
import ie.ul.postgrad.socialanxietyapp.MainGame;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.StepsService;
import ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.InventoryItemArray;
import ie.ul.postgrad.socialanxietyapp.game.item.MarkerFactory;
import ie.ul.postgrad.socialanxietyapp.game.quest.Quest;
import ie.ul.postgrad.socialanxietyapp.game.quest.QuestFactory;
import ie.ul.postgrad.socialanxietyapp.map.MapWrapperLayout;
import ie.ul.postgrad.socialanxietyapp.map.OnInfoWindowElemTouchListener;

import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.ACHIEVEMENTS;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.CRAFTING;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.INVENTORY;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.QUESTS;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.SETTINGS;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    public static boolean active = false;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GoogleApiClient mGoogleApiClient; //Entry point to Play Services (used by Places API).
    private LocationRequest mLocationRequest; //Request object for FusedLocationProviderApi.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085); //Default location.
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mCurrentLocation; //Current location of device.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    //Distance and steps display
    private TextView distanceText;
    private TextView stepsText;

    private ArrayList<Marker> markers;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private ImageView infoImg;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_maps);

        //Connect to Google API client
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        distanceText = (TextView) findViewById(R.id.distance_text);
        stepsText = (TextView) findViewById(R.id.steps_text);
        distanceText.setVisibility(View.INVISIBLE);
        stepsText.setVisibility(View.INVISIBLE);

        //Start step counter and location service
        Intent mStepsIntent = new Intent(getApplicationContext(), StepsService.class);
        startService(mStepsIntent);

        markers = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        findViewById(R.id.menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });

        findViewById(R.id.marker_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NearbyLocationsActivity.class);
                int[] markerIds = new int[9];
                float[] markerDistances = new float[9];

                if (markers.size() < 9) {
                    markerIds = new int[markers.size()];
                    markerDistances = new float[markers.size()];
                }

                for (int i = 0; i < markerIds.length; i++) {
                    markerIds[i] = (int) markers.get(i).getTag();
                    markerDistances[i] = getMarkerDistance(markers.get(i));
                }
                intent.putExtra(NearbyLocationsActivity.MARKER_IDS, markerIds);
                intent.putExtra(NearbyLocationsActivity.MARKER_DISTANCES, markerDistances);

                startActivity(intent);
            }
        });

        findViewById(R.id.updates_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChestViewActivity.class);
                startActivity(intent);
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;

        final String[] menuTitles = new String[]{"Inventory", "Quests", "Crafting", "Achievements", "Settings"};
        int[] menuIcons = new int[]{R.drawable.ic_backpack, R.drawable.ic_quest, R.drawable.ic_crafting, R.drawable.ic_achievements, R.drawable.ic_settings};

        mDrawerList.setAdapter(new NavigationDrawerListAdapter(this, menuTitles, menuIcons));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private void updateDistanceText() {
        stepsText.setText("Steps: " + GameManager.getDatabaseHelper().getSteps() + "");
        distanceText.setText("Distance: " + GameManager.getDatabaseHelper().getDistance() + "m");
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_wrapper);

        mapWrapperLayout.init(map, getPixelsFromDp(this, 59)); // Setup wrapper with default offset.

        // We want to reuse the info window for all the markers,
        // so create only one class member instance
        this.infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_info_window_quick, null);
        this.infoTitle = (TextView) infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView) infoWindow.findViewById(R.id.snippet);
        this.infoImg = (ImageView) infoWindow.findViewById(R.id.info_img);
        this.infoButton = (Button) infoWindow.findViewById(R.id.collect_button);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.info_button_default),
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.info_button_pressed)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here the marker id is passed to the result activity.
                int markerId = (int) marker.getTag();
                markers.remove(marker);
                marker.remove();

                float distance = getMarkerDistance(marker);

                if (distance < 500) {

                    if (markerId > 5) {
                        Intent intent = new Intent(getApplicationContext(), ResourceResultActivity.class);
                        intent.putExtra(ResourceResultActivity.MARKER_ID, markerId);
                        startActivity(intent);

                    } else if (markerId == MarkerFactory.ID_TREE) { //TEMP
                        Intent i = new Intent(getApplicationContext(), AndroidLauncher.class); //LibGDX Tree game Activity!
                        i.putExtra(AndroidLauncher.screenString, MainGame.TREE_GAME_SCREEN);
                        startActivity(i);
                    } else if (markerId == MarkerFactory.ID_ROCK) { //TEMP
                        Intent i = new Intent(getApplicationContext(), AndroidLauncher.class); //LibGDX Rock game Activity!
                        i.putExtra(AndroidLauncher.screenString, MainGame.ROCK_GAME_SCREEN);
                        startActivity(i);
                    } else if (markerId == MarkerFactory.ID_QUEST) {
                        Intent i = new Intent(getApplicationContext(), ConversationActivity.class); //Quest Activity!
                        startActivity(i);
                    } else if (markerId == MarkerFactory.ID_ENEMY) {
                        Intent i = new Intent(getApplicationContext(), BattleActivity.class); //Battle Activity!
                        startActivity(i);
                    }
                }
            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                Context c = getApplicationContext();
                infoImg.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), c.getResources().getIdentifier("marker_" + String.format("%04d", marker.getTag()), "drawable", c.getPackageName())));
                infoButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        applyStyleToMap();

        // Calculate ActionBar height
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        mMap.setPadding(0, actionBarHeight, 0, 0);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        Quest quest = GameManager.getInstance().getActiveQuest();
        if (quest != null) {
            markers.add(MarkerFactory.buildQuestMarker(getApplicationContext(), mMap, quest.getLocation(), quest));
        }

        // Add markers for nearby places.
        updateMarkers();

        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mCurrentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    // Customise the styling of the base map using a JSON object defined
    // in a raw resource file.
    private void applyStyleToMap() {
        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    private float getMarkerDistance(Marker marker) {
        Location location = new Location(marker.getId());
        location.setLatitude(marker.getPosition().latitude);
        location.setLongitude(marker.getPosition().longitude);
        return location.distanceTo(mCurrentLocation);
    }

    /**
     * Adds markers for places nearby the device and turns the My Location feature on or off,
     * provided location permission has been granted.
     */
    private void updateMarkers() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the businesses and other points of interest located
            // nearest to the device's current location.
            @SuppressWarnings("MissingPermission")
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        // Add a marker for each place near the device's current location, with an
                        // info window showing place information.
                        String attributions = (String) placeLikelihood.getPlace().getAttributions();
                        String snippet = (String) placeLikelihood.getPlace().getAddress();
                        if (attributions != null) {
                            snippet = snippet + "\n" + attributions;
                        }
                        LatLng latLng = placeLikelihood.getPlace().getLatLng();

                        //if (!player.hasUsedLocation(latLng)) {

                        boolean doesMarkerExist = false;

                        for (Marker marker : markers) { //Check if marker has already been placed on map.
                            if (marker.getPosition().equals(latLng)) {
                                doesMarkerExist = true;
                            }
                        }

                        if (!doesMarkerExist) {

                            if (!mapContainsQuestMarker()) {

                                Quest quest = QuestFactory.buildQuest(1, placeLikelihood.getPlace().getLatLng());
                                quest.updateQuest();
                                GameManager.getInstance().setActiveQuest(quest);

                                markers.add(MarkerFactory.buildQuestMarker(getApplicationContext(), mMap, placeLikelihood.getPlace().getLatLng(), quest));
                            }

                            if (placeLikelihood.getPlace().getPlaceTypes().size() > 2) {
                                int markerCount = getResources().getStringArray(R.array.marker_array_refs).length;
                                int id = 2;
                                while (id == 2) {
                                    id = 1 + (int) (Math.random() * (markerCount - 1));
                                }

                                String snip = snippet.toLowerCase();
                                if (snip.contains("river") || snip.contains("bridge") || snip.contains("sea") || snip.contains("ocean")) {
                                    id = 3;
                                }

                                Marker marker = MarkerFactory.buildMarker(getApplicationContext(), mMap, placeLikelihood.getPlace().getLatLng(), id);
                                //marker.setVisible(false);
                                int markerPos;

                                for (markerPos = 0; markerPos < markers.size(); markerPos++) {
                                    if (getMarkerDistance(marker) < getMarkerDistance(markers.get(markerPos))) {
                                        break;
                                    }
                                }
                                markers.add(markerPos, marker);
                            }
                        }
                    }
                    likelyPlaces.release(); // Release the place likelihood buffer.
                }
            });
        }

        //Cycle markers and check for nearby markers. Reveal if player is close.
        for (Marker m : markers) {
            if (getMarkerDistance(m) < 30 && !m.isVisible()) {
                m.setVisible(true);

                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }
        }
    }

    private boolean mapContainsQuestMarker() {
        for (Marker m : markers) {
            if ((int) m.getTag() == MarkerFactory.ID_QUEST) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    /**
     * Get the device location and nearby places when the activity is restored after a pause.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }
        updateMarkers();
    }

    /**
     * Stop location updates when the activity is no longer in focus, to reduce battery consumption.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    protected void onDestroy() {
        GameManager.getInstance().closeDatabase();
        super.onDestroy();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mCurrentLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Gets the device's current location and builds the map
     * when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        getDeviceLocation();
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, getString(R.string.play_services_failed_connection) + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, getString(R.string.play_services_suspended_connection));
    }

    /**
     * Handles the callback when location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateDistanceText();
        updateMarkers();
    }


    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Builds a GoogleApiClient.
     * Uses the addApi() method to request the Google Places API and the Fused Location Provider.
     */
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Gets the current location of the device and starts the location update notifications.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         * Also request regular updates about the device location.
         */
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mCurrentLocation = null;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i;
            switch (position) {
                case INVENTORY:
                    i = new Intent(getApplicationContext(), InventoryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("player_items", new InventoryItemArray(GameManager.getInstance().getInventory().getItems()));
                    i.putExtras(bundle);
                    startActivity(i);
                    break;
                case QUESTS:
                    i = new Intent(getApplicationContext(), ChestOpenActivity.class);
                    startActivity(i);
                    break;
                case CRAFTING:
                    i = new Intent(getApplicationContext(), CraftingActivity.class);
                    startActivity(i);
                    break;
                case ACHIEVEMENTS:
                    i = new Intent(getApplicationContext(), StepsGraphActivity.class);
                    startActivity(i);
                    break;
                case SETTINGS:
                    i = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(i);
                    break;
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}