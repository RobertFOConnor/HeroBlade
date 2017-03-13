package ie.ul.postgrad.socialanxietyapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ie.ul.postgrad.socialanxietyapp.game.InventoryItemArray;
import ie.ul.postgrad.socialanxietyapp.game.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.WorldItem;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
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


    // Firebase objects for login and database.
    private DatabaseReference mDatabase, mNameRef;
    private FirebaseAuth mAuth;

    public static Player player; // (TEMP)IMPORTANT (CHANGE FROM STATIC PLAYER PASSES!!!!)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_maps);
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        //Initialize firebase references
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mNameRef = mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("username");

        setupPlayer();

        //Set button listeners
        (findViewById(R.id.inventory_button)).setOnClickListener(this);
        (findViewById(R.id.player_button)).setOnClickListener(this);
        (findViewById(R.id.crafting_button)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.inventory_button:
                i = new Intent(this, InventoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("player_items", new InventoryItemArray(player.getInventory().getItems()));
                i.putExtras(bundle);
                startActivity(i);
                mMap.clear();
                break;
            case R.id.player_button:
                i = new Intent(this, PlayerAvatarActivity.class);
                startActivity(i);
                mMap.clear();
                break;
            case R.id.crafting_button:
                i = new Intent(this, CraftingActivity.class);
                startActivity(i);
                mMap.clear();
                break;
        }
    }

    private void setupPlayer() {

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String name = (String) dataSnapshot.getValue();
                player.setName(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mNameRef.addListenerForSingleValueEvent(postListener);
        player = new Player("");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Handles the callback when location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateMarkers();
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Add markers for nearby places.
        updateMarkers();

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

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

        mMap.setOnMarkerClickListener(this);

        try {
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setMyLocationEnabled(false);

            if (mCurrentLocation != null) {

                MarkerOptions playerMarker = new MarkerOptions()
                        .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                        .title(player.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.avatar))
                        .zIndex(1000)
                        .snippet("I'm feeling hungry.");

                mMap.addMarker(playerMarker);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
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
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         * Also request regular updates about the device location.
         */
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
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
                    boolean tradePost = false;
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        // Add a marker for each place near the device's current location, with an
                        // info window showing place information.
                        String attributions = (String) placeLikelihood.getPlace().getAttributions();
                        String snippet = (String) placeLikelihood.getPlace().getAddress();
                        if (attributions != null) {
                            snippet = snippet + "\n" + attributions;
                        }

                        //TEMP LOGIC FOR OBJECT PLACEMENT

                        WorldItem item;
                        if (snippet.toLowerCase().startsWith("a") || snippet.toLowerCase().startsWith("h") || snippet.toLowerCase().startsWith("u")) {
                            item = (WorldItem) ItemFactory.buildItem(301);
                        } else if (snippet.toLowerCase().startsWith("b") || snippet.toLowerCase().startsWith("d")) {
                            item = (WorldItem) ItemFactory.buildItem(302);
                        } else {
                            item = (WorldItem) ItemFactory.buildItem(300);
                        }

                        LatLng latLng = placeLikelihood.getPlace().getLatLng();

                        if (!player.hasUsedLocation(latLng)) {

                            mMap.addMarker(new MarkerOptions()
                                    .position(placeLikelihood.getPlace().getLatLng())
                                    .title("You are too far away from this " + item.getName() + ".")
                                    .icon(BitmapDescriptorFactory.fromResource(item.getMarkerIconID()))
                                    .snippet((String) placeLikelihood.getPlace().getName()))
                                    .setTag(item.getId());
                        }

                    }
                    // Release the place likelihood buffer.
                    likelyPlaces.release();
                }
            });
        } else {
            mMap.addMarker(new MarkerOptions()
                    .position(mDefaultLocation)
                    .title("Title")
                    .snippet("Info snippet."));
        }
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
    public boolean onMarkerClick(Marker marker) {

        if (marker.getTag() != "player") {
            if (marker.getTitle().equals(getString(R.string.click_to_inspect))) {

                if ((int) marker.getTag() != 500) {
                    player.addUsedLocation(marker.getPosition());
                    Intent i = new Intent(this, ActionActivity.class);
                    i.putExtra(ActionActivity.ACTIVE_ITEM_ID, (int) marker.getTag());
                    startActivity(i);
                } else {
                    Intent i = new Intent(getApplicationContext(), TradeListActivity.class);
                    if (marker.getSnippet().contains(",")) {
                        i.putExtra(TradeListActivity.TRADE_LOCATION_ID, marker.getSnippet().substring(0, marker.getSnippet().indexOf(',')));
                    } else {
                        i.putExtra(TradeListActivity.TRADE_LOCATION_ID, marker.getSnippet());
                    }
                    startActivity(i);
                }

                mMap.clear();
            }

            Location location = new Location(marker.getId());
            location.setLatitude(marker.getPosition().latitude);
            location.setLongitude(marker.getPosition().longitude);

            float distance = location.distanceTo(mCurrentLocation);

            if (distance > 0) {
                marker.setTitle(getString(R.string.click_to_inspect));
            } else {
                marker.setTitle("You are too far away!");
            }
        }

        return false;
    }
}
