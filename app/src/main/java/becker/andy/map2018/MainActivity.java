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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
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
import becker.andy.map2018.fragments.AppointmentsFragment;
import becker.andy.map2018.fragments.MapFragment;
import becker.andy.map2018.fragments.RequestsFragment;
import becker.andy.map2018.models.ClusterMarker;
import becker.andy.map2018.models.User;
import becker.andy.map2018.models.UserLocation;
import becker.andy.map2018.utils.MyClusterManagerRenderer;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {



    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9003;
    private boolean mLocationPermissionGranted = false;

    private List<User>mUserList=new ArrayList<>();
    private ArrayList<UserLocation>mUserLocations=new ArrayList<>();

    private FusedLocationProviderClient mFusedLocationProviderClient;
    FirebaseFirestore mDb;

    private GoogleMap mGoogleMap;
    private UserLocation mUserPosition=null;
    private LatLngBounds latLngBoundary;

    private ClusterManager mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers=new ArrayList<>();

    private BottomNavigationView mMainNav;
    private FrameLayout mFragmentContainer;

    //fragments
    public MapFragment mapFragment;
    public RequestsFragment requestsFragment;
    public AppointmentsFragment appointmentsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDb = FirebaseFirestore.getInstance();
        mMainNav=findViewById(R.id.main_nav);
        mFragmentContainer=findViewById(R.id.fragment_container);

        synchronized (this){
            initUser(savedInstanceState);
        }

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_places:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, mapFragment).commit();
                        return true;
                    case R.id.nav_requests:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, requestsFragment).commit();
                        return true;
                    case R.id.nav_appointments:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, appointmentsFragment).commit();
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    private synchronized void init() {

        mapFragment = new MapFragment();
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList(getString(R.string.userlocations_array),  mUserLocations);
        mapFragment.setArguments(bundle);

        requestsFragment=new RequestsFragment();

        appointmentsFragment=new AppointmentsFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mapFragment).commit();







    }

    private synchronized void initUser(final Bundle savedInstanceState) {
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
        for(int i=0; i<mUserList.size();i++){
            synchronized (this){
                DocumentReference locationRef=mDb.collection(getString(R.string.collection_user_location_student))
                        .document(mUserList.get(i).getUserId());
                final int finalI = i;
                locationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().toObject(UserLocation.class)!= null){
                                Log.d(TAG, "Location onComplete: ");
                                UserLocation u=task.getResult().toObject(UserLocation.class);
                                mUserLocations.add(u);
                                Log.d(TAG, "initUser: in user array");
                                if(finalI ==mUserList.size()-1){
                                    //notify();
                                    init();
                                }

                            }else {
                                Log.d(TAG, "onComplete: result is empty");
                            }
                        }
                    }
                });

            }

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
    private  void getUserLocation(User user){
        Log.d(TAG, "getUserLocation: ");

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
    protected void onResume() {
        super.onResume();

        if(checkMapServices()){
            if(mLocationPermissionGranted){
                Log.d(TAG, "onResume: ");

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
