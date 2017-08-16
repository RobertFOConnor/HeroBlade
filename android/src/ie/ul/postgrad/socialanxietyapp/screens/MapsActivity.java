package ie.ul.postgrad.socialanxietyapp.screens;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
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

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.StepsService;
import ie.ul.postgrad.socialanxietyapp.TimeToWalkActivity;
import ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.AchievementManager;
import ie.ul.postgrad.socialanxietyapp.game.ConsumedLocation;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.MarkerTag;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.item.MarkerFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;
import ie.ul.postgrad.socialanxietyapp.map.MapWrapperLayout;
import ie.ul.postgrad.socialanxietyapp.map.OnInfoWindowElemTouchListener;

import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.ACHIEVEMENTS;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.CRAFTING;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.INDEX;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.INVENTORY;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.SETTINGS;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.WEAPONS;
import static ie.ul.postgrad.socialanxietyapp.game.GameManager.blacksmithXP;
import static ie.ul.postgrad.socialanxietyapp.game.GameManager.villageXP;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

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

    private ArrayList<Marker> markers;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private ImageView infoImg;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private int markerRadius = 1000;
    private int markerUsableRadius = 30;
    private int enemyCount = 0;
    private GameManager gm;

    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        gm = GameManager.getInstance();

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mGoogleApiClient = App.getInstance().getGoogleApiHelperInstance().getGoogleApiClient();
        createLocationRequest();
        if (mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }
        startTime = System.nanoTime();
        markers = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Start step counter and location service
        Intent mStepsIntent = new Intent(getApplicationContext(), StepsService.class);
        startService(mStepsIntent);

        if (gm.getPlayer().getXp() == 0) {//Show walking instructions only first time.
            Intent i = new Intent(getApplicationContext(), TimeToWalkActivity.class);
            startActivity(i);
        }

        AchievementManager.checkAllAchievements(this);
        findViewById(R.id.menu_button).setOnClickListener(this);
        findViewById(R.id.marker_button).setOnClickListener(this);
        findViewById(R.id.updates_button).setOnClickListener(this);
    }

    private void showNearbyMarkers() {
        Intent intent = new Intent(getApplicationContext(), NearbyLocationsActivity.class);
        int[] markerIds = new int[9];
        float[] markerDistances = new float[9];

        if (markers.size() < 9) {
            markerIds = new int[markers.size()];
            markerDistances = new float[markers.size()];
        }

        for (int i = 0; i < markerIds.length; i++) {
            markerIds[i] = ((MarkerTag) markers.get(i).getTag()).getId();
            markerDistances[i] = getMarkerDistance(markers.get(i));
        }
        intent.putExtra(NearbyLocationsActivity.MARKER_IDS, markerIds);
        intent.putExtra(NearbyLocationsActivity.MARKER_DISTANCES, markerDistances);

        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;

        final String[] menuTitles = new String[]{"Items", "Weapons", "Crafting", "Weapon Index", "Achievements", "Settings"};
        int[] menuIcons = new int[]{R.drawable.ic_backpack, R.drawable.ic_quest, R.drawable.ic_crafting, R.drawable.ic_backpack, R.drawable.ic_achievements, R.drawable.ic_settings};

        mDrawerList.setAdapter(new NavigationDrawerListAdapter(this, menuTitles, menuIcons));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMinZoomPreference(14f);

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
                int markerId = ((MarkerTag) marker.getTag()).getId();
                float distance = getMarkerDistance(marker);

                if (distance < markerUsableRadius) {//Check if user is close enough to marker.
                    if (showUsedMarker(marker.getPosition())) {//Check if too soon since last visit.) {
                        if (markerId == MarkerFactory.ID_VILLAGE) {
                            startVillage(marker);
                        } else if (markerId == MarkerFactory.ID_BLACKSMITH) {
                            startBlacksmith(marker);
                        } else if (markerId == MarkerFactory.ID_ENEMY) {
                            if (gm.getInventory().getWeapons().size() > 0 && gm.getPlayer().getCurrHealth() > 0 && gm.getInventory().hasUsableWeapons()) {
                                startBattle(marker);
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.no_battle), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.already_visited), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.too_far_away), Toast.LENGTH_SHORT).show();
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
                infoImg.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), c.getResources().getIdentifier("marker_" + String.format("%04d", ((MarkerTag) marker.getTag()).getId()), "drawable", c.getPackageName())));
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

        // Add markers for nearby places.
        updateMarkers();

        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mCurrentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, getString(R.string.null_location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void startVillage(Marker marker) {
        Player player = gm.getPlayer();
        player.setCurrHealth(player.getMaxHealth());
        gm.updatePlayerInDatabase(player);
        gm.awardXP(getApplicationContext(), villageXP);
        disableMarker(marker);
        startMarkerActivity(VillageActivity.class, marker, MarkerFactory.ID_VILLAGE);
    }

    private void startBlacksmith(Marker marker) {
        for (WeaponItem weaponItem : gm.getInventory().getWeapons()) {
            weaponItem.setCurrHealth(weaponItem.getMaxHealth());
            gm.updateWeaponInDatabase(weaponItem.getUUID(), weaponItem.getId(), weaponItem.getCurrHealth(), weaponItem.isEquipped());
            gm.awardXP(getApplicationContext(), blacksmithXP);
        }
        disableMarker(marker);
        startMarkerActivity(BlacksmithActivity.class, marker, MarkerFactory.ID_BLACKSMITH);
    }

    private void startBattle(Marker marker) {
        enemyCount--;
        marker.remove();
        markers.remove(marker);
        startMarkerActivity(BattleActivity.class, marker, MarkerFactory.ID_ENEMY);
    }

    private void startMarkerActivity(Class activity, Marker marker, int type) {
        gm.addUsedLocation(marker.getPosition(), type);
        AchievementManager.checkMarkerAchievements(this);
        Intent i = new Intent(getApplicationContext(), activity);
        startActivity(i);
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
    private boolean showUsedMarker(LatLng latLng) {
        boolean show = true;
        ConsumedLocation location = gm.hasUsedLocation(latLng);
        if (location != null) {
            if (!location.shouldShow()) {
                show = false;
            }
        }
        return show;
    }

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
                        String snippet = (String) placeLikelihood.getPlace().getName();
                        if (attributions != null) {
                            snippet = snippet + "\n" + attributions;
                        }
                        LatLng latLng = placeLikelihood.getPlace().getLatLng();

                        boolean doesMarkerExist = false;

                        for (Marker marker : markers) { //Check if marker has already been placed on map.
                            if (marker.getPosition().equals(latLng)) {
                                doesMarkerExist = true;
                            }
                        }

                        if (!doesMarkerExist) {

                            if (placeLikelihood.getPlace().getPlaceTypes().size() > 2) {
                                int markerCount = getResources().getStringArray(R.array.marker_array_refs).length;
                                int id = MarkerFactory.ID_VILLAGE;
                                while (id == MarkerFactory.ID_VILLAGE) {
                                    id = 1 + (int) (Math.random() * (markerCount - 1));
                                }

                                if (getString(R.string.alphabet_half).contains(snippet.toLowerCase().charAt(0) + "")) {
                                    id = MarkerFactory.ID_BLACKSMITH;
                                } else {
                                    id = MarkerFactory.ID_VILLAGE;
                                }

                                Marker marker = MarkerFactory.buildMarker(getApplicationContext(), mMap, latLng, snippet, id);
                                marker.setVisible(false);
                                int markerPos;

                                for (markerPos = 0; markerPos < markers.size(); markerPos++) {
                                    if (getMarkerDistance(marker) < getMarkerDistance(markers.get(markerPos))) {
                                        break;
                                    }
                                }

                                if (!showUsedMarker(marker.getPosition())) {
                                    disableMarker(marker);
                                }

                                markers.add(markerPos, marker);
                            } else if (enemyCount < 3) {
                                Marker marker = MarkerFactory.buildMarker(getApplicationContext(), mMap, latLng, snippet, MarkerFactory.ID_ENEMY);
                                marker.setVisible(false);
                                int markerPos;

                                for (markerPos = 0; markerPos < markers.size(); markerPos++) {
                                    if (getMarkerDistance(marker) < getMarkerDistance(markers.get(markerPos))) {
                                        break;
                                    }
                                }
                                markers.add(markerPos, marker);
                                enemyCount++;
                            }
                        }
                    }
                    likelyPlaces.release(); // Release the place likelihood buffer.

                    for (Marker marker : markers) {
                        ConsumedLocation location = gm.hasUsedLocation(marker.getPosition());
                        if (location != null) {
                            if (location.shouldShow() && !((MarkerTag) marker.getTag()).isEnabled()) {
                                int markerId = ((MarkerTag) marker.getTag()).getId();
                                marker.setIcon(MarkerFactory.getUpdateMarkerIcon(getApplicationContext(), markerId));
                                marker.setTag(new MarkerTag(markerId, true));
                            }
                        }
                    }
                }
            });
        }

        //Cycle markers and check for nearby markers. Reveal if player is close.
        for (Marker m : markers) {
            if (getMarkerDistance(m) < markerRadius && !m.isVisible()) {
                m.setVisible(true);

                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }
        }
    }

    private void disableMarker(Marker marker) {
        marker.setTag(new MarkerTag(((MarkerTag) marker.getTag()).getId(), false));
        marker.setIcon(MarkerFactory.getUpdateMarkerIcon(this, MarkerFactory.COOLDONW_MARKER));
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

        if (gm.getPlayer().getCurrHealth() <= 5) {
            new LowHealthDialogFragment().show(getSupportFragmentManager(), "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.menu_button):
                mDrawerLayout.openDrawer(Gravity.START);
                break;
            case (R.id.marker_button):
                showNearbyMarkers();
                break;
            case (R.id.updates_button):
                Intent intent = new Intent(getApplicationContext(), ChestViewActivity.class);
                startActivity(intent);
                break;
        }
    }

    public static class LowHealthDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your health is in critical condition. Find a nearby village to heal you.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
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
        gm.closeDatabase();
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
     * Handles the callback when location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateMarkers();
    }


    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Sets up the location request.
     */
    private synchronized void createLocationRequest() {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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
        private static final int REQUEST_ACHIEVEMENTS = 101;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i;
            switch (position) {
                case INVENTORY:
                    i = new Intent(getApplicationContext(), InventoryActivity.class);
                    startActivity(i);
                    break;
                case WEAPONS:
                    i = new Intent(getApplicationContext(), WeaponActivity.class);
                    startActivity(i);
                    break;
                case CRAFTING:
                    i = new Intent(getApplicationContext(), CraftingActivity.class);
                    startActivity(i);
                    break;
                case INDEX:
                    i = new Intent(getApplicationContext(), IndexActivity.class);
                    startActivity(i);
                    break;
                case ACHIEVEMENTS:
                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && App.getInstance().getGoogleApiHelperInstance().isGAMES()) {
                        startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_ACHIEVEMENTS);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_google_play_achievements), Toast.LENGTH_SHORT).show();
                    }
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