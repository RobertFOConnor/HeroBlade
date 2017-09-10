package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
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
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.MarkerManager;
import ie.ul.postgrad.socialanxietyapp.game.MarkerTag;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.XPLevels;
import ie.ul.postgrad.socialanxietyapp.game.item.MarkerFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;
import ie.ul.postgrad.socialanxietyapp.map.MapWrapperLayout;
import ie.ul.postgrad.socialanxietyapp.map.OnInfoWindowElemTouchListener;
import ie.ul.postgrad.socialanxietyapp.sync.SyncManager;

import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.ACHIEVEMENTS;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.CRAFTING;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.INDEX;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.INVENTORY;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.PROFILE_TEXT;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.SETTINGS;
import static ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter.WEAPONS;
import static ie.ul.postgrad.socialanxietyapp.game.GameManager.blacksmithXP;
import static ie.ul.postgrad.socialanxietyapp.game.GameManager.villageXP;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    public static boolean active = false;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private LocationRequest mLocationRequest; //Request object for FusedLocationProviderApi.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085); //Default location.
    private boolean mLocationPermissionGranted;
    private Location mCurrentLocation; //Current location of device.
    private ArrayList<Marker> markers;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private ImageView infoImg;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private GameManager gm;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final int markerRadius = 1000;
    public static final int markerUsableRadius = 60;
    private GoogleApiClient mGoogleApiClient;
    private boolean usingGames = true;

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

        buildGoogleGamesApiClient();
        mGoogleApiClient.connect();
        markers = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        SyncManager.getInstance().startSyncAdapter(this);

        //Start step counter and location service
        Intent mStepsIntent = new Intent(getApplicationContext(), StepsService.class);
        startService(mStepsIntent);

        if (gm.getPlayer().getXp() == 0) {//Show walking instructions only first time.
            Intent i = new Intent(getApplicationContext(), TimeToWalkActivity.class);
            startActivity(i);
        }

        findViewById(R.id.menu_button).setOnClickListener(this);
        findViewById(R.id.marker_button).setOnClickListener(this);
        findViewById(R.id.updates_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        setupNavDrawer();
    }

    /**
     * Sets up menu navigation drawer.
     */
    private void setupNavDrawer() {
        final String[] menuTitles = getResources().getStringArray(R.array.nav_items);
        int[] menuIcons = new int[]{R.drawable.ic_settings, R.drawable.ic_backpack, R.drawable.ic_quest, R.drawable.ic_crafting, R.drawable.ic_backpack, R.drawable.ic_achievements, R.drawable.ic_settings};
        mDrawerList.setAdapter(new NavigationDrawerListAdapter(this, menuTitles, menuIcons));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this));
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMinZoomPreference(16f);
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
                float distance = MarkerManager.getMarkerDistance(marker, mCurrentLocation);

                if (distance < markerUsableRadius || GameManager.TESTING) {//Check if user is close enough to marker.
                    if (MarkerManager.showUsedMarker(marker.getPosition())) {//Check if too soon since last visit.) {
                        switch (markerId) {
                            case MarkerFactory.ID_VILLAGE:
                                startVillage(marker);
                                break;
                            case MarkerFactory.ID_BLACKSMITH:
                                startBlacksmith(marker);
                                break;
                            case MarkerFactory.ID_ENEMY:
                                startBattle(marker);
                                break;
                        }
                    } else {
                        App.showToast(getApplicationContext(), getString(R.string.already_visited));
                    }
                } else {
                    App.showToast(getApplicationContext(), getString(R.string.too_far_away));
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
        MarkerManager.disableMarker(this, marker);
        startMarkerActivity(VillageActivity.class, marker, MarkerFactory.ID_VILLAGE);
    }

    private void startBlacksmith(Marker marker) {
        for (WeaponItem weaponItem : gm.getInventory().getWeapons()) {
            weaponItem.setCurrHealth(weaponItem.getMaxHealth());
            gm.updateWeaponInDatabase(weaponItem);
        }
        gm.awardXP(getApplicationContext(), blacksmithXP);
        MarkerManager.disableMarker(this, marker);
        startMarkerActivity(BlacksmithActivity.class, marker, MarkerFactory.ID_BLACKSMITH);
    }

    private void startBattle(Marker marker) {
        if (gm.getInventory().getWeapons().size() > 0 && gm.getPlayer().getCurrHealth() > 0 && gm.getInventory().hasUsableWeapons()) {
            MarkerManager.enemyCount--;
            marker.remove();
            markers.remove(marker);
            startMarkerActivity(BattleActivity.class, marker, MarkerFactory.ID_ENEMY);
        } else {
            App.showToast(getApplicationContext(), getString(R.string.no_battle));
        }
    }

    private void startMarkerActivity(Class activity, Marker marker, int type) {
        gm.addUsedLocation(marker.getPosition(), type);
        Intent i = new Intent(getApplicationContext(), activity);
        startActivity(i);
    }

    /**
     * Customise the styling of the base map using a JSON object defined
     * in a raw resource file.
     */
    private void applyStyleToMap() {
        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, getString(R.string.style_failed));
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, getString(R.string.style_not_found), e);
        }
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
                        MarkerManager.updateMarker(getApplicationContext(), mMap, placeLikelihood, markers, mCurrentLocation);
                    }
                    likelyPlaces.release(); // Release the place likelihood buffer.
                    MarkerManager.checkForEndOfMarkerCooldown(getApplicationContext(), markers);
                }
            });
        }
        MarkerManager.revealMarkers(this, markers, mCurrentLocation);
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

            if (mGoogleApiClient.hasConnectedApi(Games.API)) {
                AchievementManager.checkAllAchievements(this, mGoogleApiClient);
            }
        }
        updateMarkers();
        ((TextView) findViewById(R.id.level_num)).setText(String.valueOf(gm.getPlayer().getLevel()));
        ProgressBar xpBar = (ProgressBar) findViewById(R.id.xp_bar);
        xpBar.setMax(XPLevels.XP_LEVELS[gm.getPlayer().getLevel()]);
        xpBar.setProgress(gm.getPlayer().getXp());
        xpBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.menu_button):
                mDrawerLayout.openDrawer(Gravity.START);
                break;
            case (R.id.marker_button):
                MarkerManager.showNearbyMarkers(this, markers, mCurrentLocation);
                break;
            case (R.id.updates_button):
                Intent intent = new Intent(getApplicationContext(), ChestViewActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * Stop location updates when the activity is no longer in focus, to reduce battery consumption.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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
        // Request location permission, so that we can get the location of the
        // device. The result of the permission request is handled by a callback, onRequestPermissionsResult.
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        //Get the best and most recent location of the device, which may be null in rare cases when a location is not available.
        // Also request regular updates about the device location.
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
                //.addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        createLocationRequest();
    }

    private synchronized void buildGoogleGamesApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        createLocationRequest();
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

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        App.getInstance().setmGoogleApiClient(mGoogleApiClient);
        getDeviceLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        System.out.println("CONNECT SUCCESS");
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, getString(R.string.play_services_failed_connection) + result.getErrorCode());

        if (usingGames) {
            usingGames = false;
            buildGoogleApiClient();
            mGoogleApiClient.connect();
            Log.d(TAG, "Can't sign in to games. Using maps only." + result.getErrorCode());
        }
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, getString(R.string.play_services_suspended_connection));
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        private Context context;

        private DrawerItemClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i;
            switch (position) {
                case PROFILE_TEXT:
                    i = new Intent(context, PlayerAvatarActivity.class);
                    startActivity(i);
                    break;
                case INVENTORY:
                    i = new Intent(context, InventoryActivity.class);
                    startActivity(i);
                    break;
                case WEAPONS:
                    i = new Intent(context, WeaponActivity.class);
                    startActivity(i);
                    break;
                case CRAFTING:
                    i = new Intent(context, CraftingActivity.class);
                    startActivity(i);
                    break;
                case INDEX:
                    i = new Intent(context, IndexActivity.class);
                    startActivity(i);
                    break;
                case ACHIEVEMENTS:
                    if (mGoogleApiClient.hasConnectedApi(Games.API)) {
                        AchievementManager.showAchievements(context, mGoogleApiClient);
                    } else {
                        App.showToast(getApplicationContext(), getString(R.string.no_google_play_achievements));
                    }
                    break;
                case SETTINGS:
                    //i = new Intent(context, DeveloperSettingsActivity.class);
                    //context.startActivity(i);

                    if (mGoogleApiClient.hasConnectedApi(Games.API)) {
                        int totalXP = 0;
                        for (int j = 0; j < gm.getPlayer().getLevel(); j++) {
                            totalXP += XPLevels.XP_LEVELS[j];
                        }
                        totalXP += gm.getPlayer().getXp();
                        Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_top_players), totalXP);
                        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                                getString(R.string.leaderboard_top_players)), 122);
                    } else {
                        App.showToast(getApplicationContext(), getString(R.string.no_google_play_leaderboard));
                    }
                    break;
            }

            DrawerLayout drawer = (DrawerLayout) ((AppCompatActivity) context).findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    }
}