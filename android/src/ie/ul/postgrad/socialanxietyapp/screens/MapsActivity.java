package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Locale;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.DrawerItemClickListener;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.adapter.NavigationDrawerListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.AchievementManager;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.MarkerManager;
import ie.ul.postgrad.socialanxietyapp.game.MarkerTag;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.game.XPLevels;
import ie.ul.postgrad.socialanxietyapp.game.factory.MarkerFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;
import ie.ul.postgrad.socialanxietyapp.map.MapWrapperLayout;
import ie.ul.postgrad.socialanxietyapp.map.OnInfoWindowElemTouchListener;
import ie.ul.postgrad.socialanxietyapp.service.StepsService;
import ie.ul.postgrad.socialanxietyapp.sync.SyncManager;

import static ie.ul.postgrad.socialanxietyapp.game.GameManager.blacksmithXP;
import static ie.ul.postgrad.socialanxietyapp.game.GameManager.villageXP;
import static ie.ul.postgrad.socialanxietyapp.game.MarkerManager.MARKER_USABLE_RADIUS;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.INFO_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.MAP_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.REVIEW_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.TRANSPARENT_KEY;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AndroidFragmentApplication.Callbacks, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static boolean active = false;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private LocationRequest mLocationRequest; //Request object for FusedLocationProviderApi.
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation; //Current location of device.
    private ArrayList<Marker> markers;
    private DrawerLayout mDrawerLayout;
    private GameManager gm;
    private GoogleApiClient mGoogleApiClient;
    private int signInAttempts = 0;

    //Marker views.
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private ImageView infoImg;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;

    //Constants
    private static final String TAG = MapsActivity.class.getSimpleName();
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085); //Default location.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        retrieveBundleInfo(savedInstanceState);
        setupGameManager();
        setupLocationCallback();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        markers = new ArrayList<>();
        SyncManager.getInstance().startSyncAdapter(this);
        startStepService();
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMarkers();
        ((TextView) findViewById(R.id.level_num)).setText(String.valueOf(gm.getPlayer().getLevel()));
        ProgressBar xpBar = findViewById(R.id.xp_bar);
        xpBar.setMax(XPLevels.XP_LEVELS[gm.getPlayer().getLevel()]);
        xpBar.setProgress(gm.getPlayer().getXp());
        xpBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        signInSilently();
        getDeviceLocation();
    }

    private void initUI() {
        setupNavDrawer();
        setupToolbar();
        showLoadingIcon(true);
    }

    private void setupGameManager() {
        gm = GameManager.getInstance();
        gm.initDatabase(this);
        gm.setMoodRatingAlarm(this);
    }

    private void showLoadingIcon(boolean show) {
        if (show) {
            Glide.with(this).load(R.drawable.loading_map).into((ImageView) findViewById(R.id.loading_gif));
        } else {
            findViewById(R.id.loading).setVisibility(View.GONE);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        findViewById(R.id.menu_button).setOnClickListener(this);
        findViewById(R.id.marker_button).setOnClickListener(this);
        findViewById(R.id.updates_button).setOnClickListener(this);
    }

    private void retrieveBundleInfo(Bundle savedInstanceState) {
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
    }

    private void setupLocationCallback() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mCurrentLocation = locationResult.getLastLocation();
                if (mMap != null) {
                    moveUser(mCurrentLocation);
                    updateMarkers();
                }
            }
        };
    }

    private void startStepService() {
        //Start step counter and location service
        Intent mStepsIntent = new Intent(getApplicationContext(), StepsService.class);
        startService(mStepsIntent);
    }

    /**
     * Sets up menu navigation drawer.
     */
    private void setupNavDrawer() {
        final String[] menuTitles = getResources().getStringArray(R.array.nav_items);
        int[] menuIcons = new int[]{R.drawable.ic_prof, R.drawable.ic_backpack, R.drawable.ic_weapons, R.drawable.ic_crafting, R.drawable.ic_index, R.drawable.ic_achievements, R.drawable.ic_leaderboard, R.drawable.ic_quest};
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ListView mDrawerList = findViewById(R.id.left_drawer);
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
        final MapWrapperLayout mapWrapperLayout = findViewById(R.id.map_wrapper);
        mapWrapperLayout.init(map, getPixelsFromDp(this, 59)); // Setup wrapper with default offset.
        setupMarkerViews();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                SoundManager.getInstance(getApplicationContext()).playSound(SoundManager.Sound.MARKER);
                return false;
            }
        });

        setMarkerClickListener();
        map.setInfoWindowAdapter(getInfoWindowAdapter(mapWrapperLayout));
        applyStyleToMap();
        setupMapLayout();
        updateLocationUI();
        updateMarkers();
        setCameraPos();
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        showLoadingIcon(false);
    }

    private void updateMarkers() {
        MarkerManager.updateMarkers(this, mMap, mLocationPermissionGranted, mGoogleApiClient, markers, mCurrentLocation);
    }

    private void setCameraPos() {
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mCurrentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, getString(R.string.null_location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
        }
    }

    private void setupMapLayout() {
        // Calculate ActionBar height
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        mMap.setPadding(0, actionBarHeight, 0, 0);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private void setMarkerClickListener() {
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
                Context context = getApplicationContext();

                if (distance < MARKER_USABLE_RADIUS || GameManager.TESTING) {//Check if user is close enough to marker.
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
                        playClick();
                    } else {
                        App.showToast(context, getString(R.string.already_visited));
                    }
                } else {
                    App.showToast(context, getString(R.string.too_far_away));
                }
            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);
    }

    private void setupMarkerViews() {
        // We want to reuse the info window for all the markers,
        // so create only one class member instance
        this.infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_info_window_quick, null);
        this.infoTitle = infoWindow.findViewById(R.id.title);
        this.infoSnippet = infoWindow.findViewById(R.id.snippet);
        this.infoImg = infoWindow.findViewById(R.id.info_img);
        this.infoButton = infoWindow.findViewById(R.id.collect_button);
    }

    private GoogleMap.InfoWindowAdapter getInfoWindowAdapter(final MapWrapperLayout mapWrapperLayout) {
        return new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                Context c = getApplicationContext();
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                infoImg.setImageDrawable(ContextCompat.getDrawable(c, c.getResources().getIdentifier("marker_" + String.format(Locale.ENGLISH, "%04d", ((MarkerTag) marker.getTag()).getId()), "drawable", c.getPackageName())));
                infoButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        };
    }

    private void startVillage(Marker marker) {
        Player player = gm.getPlayer();
        player.setCurrHealth(player.getMaxHealth());
        gm.updatePlayerInDatabase(player);
        gm.awardXP(this, villageXP);
        MarkerManager.disableMarker(this, marker);
        startMarkerActivity(VillageActivity.class, marker, MarkerFactory.ID_VILLAGE);
    }

    private void startBlacksmith(Marker marker) {
        for (WeaponItem weaponItem : gm.getInventory().getWeapons()) {
            weaponItem.setCurrHealth(weaponItem.getMaxHealth());
            gm.updateWeaponInDatabase(weaponItem);
        }
        gm.awardXP(this, blacksmithXP);
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
            App.showToast(this, getString(R.string.no_battle));
        }
    }

    private void startMarkerActivity(Class activity, Marker marker, int type) {
        gm.addUsedLocation(marker.getPosition(), type);
        Intent i = new Intent(this, activity);
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

    private void signInSilently() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            // The signed in account is stored in the task's result.
                            GoogleSignInAccount signedInAccount = task.getResult();
                            AchievementManager.checkAllAchievements(getApplicationContext());
                        } else {
                            // Player will need to sign-in explicitly using via UI
                            startSignInIntent();
                        }
                    }
                });
    }

    final int RC_SIGN_IN = 1004;

    private void startSignInIntent() {
        if (signInAttempts < 1) {
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                    GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
            Intent intent = signInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
            signInAttempts++;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.menu_button):
                mDrawerLayout.openDrawer(Gravity.START);
                playClick();
                break;
            case (R.id.marker_button):
                MarkerManager.showNearbyMarkers(this, markers, mCurrentLocation);
                playClick();
                break;
            case (R.id.updates_button):
                Intent intent = new Intent(getApplicationContext(), ChestViewActivity.class);
                startActivity(intent);
                playClick();
                break;
        }
    }

    /**
     * Stop location updates when the activity is no longer in focus, to reduce battery consumption.
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
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
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                mCurrentLocation = location;
                            }
                        }
                    });
            startLocationUpdates();
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
                    showHelpInfo();
                }
            }
        }
        updateLocationUI();
    }

    private void showHelpInfo() {
        SharedPreferences prefs = this.getSharedPreferences("ie.ul.postgrad.socialanxietyapp", Context.MODE_PRIVATE);

        final String firstTimeMapKey = "firstTimeMap";
        boolean firstTimeMap = prefs.getBoolean(firstTimeMapKey, true);
        if (firstTimeMap) {
            Intent tutorialIntent = new Intent(this, HelpActivity.class);
            //bundle here...
            tutorialIntent.putExtra(INFO_KEY, MAP_INFO);
            tutorialIntent.putExtra(REVIEW_KEY, false);
            tutorialIntent.putExtra(TRANSPARENT_KEY, true);
            startActivity(tutorialIntent);
            prefs.edit().putBoolean(firstTimeMapKey, false).apply();
        }
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
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        mMap.setMyLocationEnabled(mLocationPermissionGranted);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (!mLocationPermissionGranted) {
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

    private void startLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getDeviceLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void moveUser(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(latLng)
                .bearing(location.getBearing()).tilt(65.5f).zoom(18f).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
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

    @Override
    public void exit() {

    }

    private void playClick() {
        SoundManager.getInstance(this).playSound(SoundManager.Sound.CLICK);
    }
}