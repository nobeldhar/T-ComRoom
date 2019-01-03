package becker.andy.map2018;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import becker.andy.map2018.classes.UserClient;
import becker.andy.map2018.models.ClusterMarker;
import becker.andy.map2018.models.User;
import becker.andy.map2018.models.UserLocation;
import becker.andy.map2018.utils.MyClusterManagerRenderer;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mMapView;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9003;
    private boolean mLocationPermissionGranted = false;

    private List<User>mUserList=new ArrayList<>();
    private List<UserLocation>mUserLocations=new ArrayList<>();

    private FusedLocationProviderClient mFusedLocationProviderClient;
    FirebaseFirestore mDb;

    private GoogleMap mGoogleMap;
    private UserLocation mUserPosition=null;
    private LatLngBounds latLngBoundary;

    private ClusterManager mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDb = FirebaseFirestore.getInstance();
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = findViewById(R.id.user_list_map);
        mMapView.onCreate(mapViewBundle);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        initUser();
        getLastKnownLocation(new User(),new UserLocation());

        mMapView.getMapAsync(this);
        
    }

    private void initUser() {
        User user=new User();
        user.setEmail("nobeld@gmail.com");
        user.setResponse("ok");
        user.setUser("student");
        user.setUserId("5");
        user.setUserName("nobel");
        ((UserClient)(getApplicationContext())).setUser(user);
        mUserList.add(user);
        User user1=new User();
        user1.setEmail("rahuld@gmail.com");
        user1.setResponse("ok");
        user1.setUser("student");
        user1.setUserId("6");
        user1.setUserName("rahul");
        User user2=new User();
        user2.setEmail("milond@gmail.com");
        user2.setResponse("ok");
        user2.setUser("student");
        user2.setUserId("7");
        user2.setUserName("milon");
        mUserList.add(user1);
        mUserList.add(user2);
        for(User u: mUserList){
            getUserLocation(u);
            Log.d(TAG, "initUser: in user array");
        }



    }

    private void setCameraView(){
        if(mUserPosition!= null){
            Log.d(TAG, "setCameraView: user position got");
            double bottomboundary=mUserPosition.getGeo_point().getLatitude()-.05;
            double leftboundary = mUserPosition.getGeo_point().getLongitude()-.05;
            double upboundary = mUserPosition.getGeo_point().getLatitude()+.05;
            double rightboundary = mUserPosition.getGeo_point().getLongitude()+.05;
            latLngBoundary=new LatLngBounds(new LatLng(bottomboundary,leftboundary),
                    new LatLng(upboundary,rightboundary));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundary,0));

        }else {
            Log.d(TAG, "setCameraView: user position is null");
        }
    }
    private void getUserLocation(User user){
        Log.d(TAG, "getUserLocation: ");
        DocumentReference locationRef=mDb.collection(getString(R.string.collection_user_location_student))
                .document(user.getUserId());
        locationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().toObject(UserLocation.class)!= null){
                        Log.d(TAG, "Location onComplete: ");
                        UserLocation u=task.getResult().toObject(UserLocation.class);
                        mUserLocations.add(u);

                    }else {
                        Log.d(TAG, "onComplete: result is empty");
                    }
                }
            }
        });

    }

    private void addMapMarkers(){
        Log.d(TAG, "addMapMarkers: inside");
        if(mGoogleMap != null){
            if(mClusterManager == null){
                Log.d(TAG, "addMapMarkers: cluster manager intiated");
                mClusterManager=new ClusterManager(this,mGoogleMap);
            }
            if(mClusterManagerRenderer == null){
                Log.d(TAG, "addMapMarkers: renderer initiated");
                mClusterManagerRenderer =new MyClusterManagerRenderer(
                        this,
                        mGoogleMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
            for(UserLocation userLocation: mUserLocations){
                try{
                    String snippet=userLocation.getGeo_point().toString();
                    int image=R.drawable.marker_png;

                    ClusterMarker newClusterMarker=new ClusterMarker(
                            new LatLng(userLocation.getGeo_point().getLatitude(),userLocation.getGeo_point().getLatitude()),
                            userLocation.getUser().getUserName(),
                            snippet,
                            image,
                            userLocation.getUser()
                    );
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);
                    Log.d(TAG, "addMapMarkers: marker added");
                }catch (NullPointerException e){
                    Log.d(TAG, "addMapMarkers: "+e.getMessage().toString());
                }
            }
            mClusterManager.cluster();
        }
    }

    private void getUserDetails(User user){
         UserLocation mUserLocation=null;
        if(mUserLocation == null){
            mUserLocation=new UserLocation();
            mUserLocation.setUser(user);
            getLastKnownLocation(user,mUserLocation);
        }
        else {
            getLastKnownLocation(user,mUserLocation);
        }
    }

    private void saveUserLocation(User user, final UserLocation mUserLocation){
        if(mUserLocation != null){

            DocumentReference locationRef=mDb.collection(getString(R.string.collection_user_location_student))
                    .document(user.getUserId());
            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "saveUserLocation:\ninserted user location into database" +
                                "\n latitude: "+mUserLocation.getGeo_point().getLatitude() +
                                "\n longitude: "+mUserLocation.getGeo_point().getLongitude());

                    }
                }
            });

        }
    }

    private void getLastKnownLocation(final User user, final UserLocation mUserLocation) {
        Log.d(TAG, "getLastKnownLocation: ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLastKnownLocation: inside");
            return;
        }
        Log.d(TAG, "getLastKnownLocation: outside");
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Log.d(TAG, "onComplete: inside");
                if(task.isSuccessful()) {
                    if (task.getResult() != null) {


                        GeoPoint geoPoint = new GeoPoint(task.getResult().getLatitude(), task.getResult().getLongitude());
                        Log.d(TAG, "onComplete: latitude " + geoPoint.getLatitude());
                        Log.d(TAG, "onComplete: longitude " + geoPoint.getLongitude());
                        mUserPosition=new UserLocation();
                        User user1=((UserClient)(getApplicationContext())).getUser();
                        mUserPosition.setUser(user1);
                        mUserPosition.setGeo_point(geoPoint);
                        mUserPosition.setTimestamp(null);
                        //saveUserLocation(user,mUserLocation);
                        Log.d(TAG, "onComplete: eeee");
                    }
                    else {
                        Log.d(TAG, "onComplete: task is null");
                    }
                }else {
                    Log.d(TAG, "onComplete: task fail");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage().toString());
            }
        });
        Log.d(TAG, "getLastKnownLocation: last");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        setCameraView();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mGoogleMap=googleMap;
        for(UserLocation userLocation: mUserLocations) {
            Log.d(TAG, "onMapReady: marker added");
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(userLocation.getUser().getUserName());
            markerOptions.snippet(userLocation.getGeo_point().toString());
            markerOptions.position(new LatLng(userLocation.getGeo_point().getLatitude(),userLocation.getGeo_point().getLongitude()));
            googleMap.addMarker(markerOptions);
        }

        googleMap.setMyLocationEnabled(true);

    }


    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if(checkMapServices()){
            if(mLocationPermissionGranted){
                Log.d(TAG, "onResume: ");


                //addMapMarkers();


            }else{

                getLocationPermission();

            }
        }
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
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
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }


}
