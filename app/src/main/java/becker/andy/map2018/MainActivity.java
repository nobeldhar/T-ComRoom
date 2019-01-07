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
import android.telecom.Call;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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

import becker.andy.map2018.classes.PrefConfig;
import becker.andy.map2018.classes.UserClient;
import becker.andy.map2018.fragments.AppointmentsFragment;
import becker.andy.map2018.fragments.MapFragment;
import becker.andy.map2018.fragments.RequestsFragment;
import becker.andy.map2018.models.ClusterMarker;
import becker.andy.map2018.models.Requests;
import becker.andy.map2018.models.User;
import becker.andy.map2018.models.UserLocation;
import becker.andy.map2018.retrofit.ApiClient;
import becker.andy.map2018.retrofit.ApiInterface;
import becker.andy.map2018.utils.MyClusterManagerRenderer;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {



    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9003;
    private boolean mLocationPermissionGranted = false;

    private ArrayList<Requests>mRequestsList=new ArrayList<>();
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
    public MapFragment mapFragment=new MapFragment();
    public RequestsFragment requestsFragment=new RequestsFragment();
    public AppointmentsFragment appointmentsFragment=new AppointmentsFragment();
    //widgets
    private ProgressBar mMainProgressBar;
    //prefconfig



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDb = FirebaseFirestore.getInstance();
        mMainNav=findViewById(R.id.main_nav);
        mFragmentContainer=findViewById(R.id.fragment_container);
        mMainProgressBar=findViewById(R.id.main_progressBar);
        mMainProgressBar.setVisibility(View.VISIBLE);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
        Bundle bundle2=new Bundle();
        bundle2.putParcelableArrayList(getString(R.string.userlocations_array),  mUserLocations);
        requestsFragment.setArguments(bundle2);

        appointmentsFragment=new AppointmentsFragment();

        mMainProgressBar.setVisibility(View.GONE);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mapFragment).commit();







    }

    private synchronized void initUser(final Bundle savedInstanceState) {

        retrofit2.Call<List<Requests>> call=RegisterActivity.apiInterface.getRequests(RegisterActivity.prefConfig.readUserId());
        call.enqueue(new Callback<List<Requests>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Requests>> call, Response<List<Requests>> response) {
                if(response.isSuccessful()){
                    mRequestsList= (ArrayList<Requests>) response.body();
                    for(int i=0; i<mRequestsList.size();i++){
                        synchronized (this){
                            DocumentReference locationRef=mDb.collection(getString(R.string.collection_user_location_student))
                                    .document(Integer.toString( mRequestsList.get(i).getStudent_id()));
                            final int finalI = i;
                            final int finalI1 = i;
                            locationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        if(task.getResult()!= null){
                                            Log.d(TAG, "Location onComplete: ");
                                            UserLocation userLocation=task.getResult().toObject(UserLocation.class);
                                            UserLocation u=new UserLocation();
                                                    u.setGeo_point(userLocation.getGeo_point());
                                            u.setRequests(mRequestsList.get(finalI1));
                                            mUserLocations.add(u);
                                            Log.d(TAG, "initUser: in user array");
                                            if(finalI ==mRequestsList.size()-1){
                                                //notify();
                                                init();
                                                getLastKnownLocation(new UserLocation());
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
            }

            @Override
            public void onFailure(retrofit2.Call<List<Requests>> call, Throwable t) {

            }
        });




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



    private void getUserDetails(){
         UserLocation mUserLocation=null;
        if(mUserLocation == null){
            mUserLocation=new UserLocation();
            getLastKnownLocation(mUserLocation);
        }
        else {
            getLastKnownLocation(mUserLocation);
        }
    }

    private void saveUserLocation(final UserLocation mUserLocation){
        if(mUserLocation != null){

            DocumentReference locationRef=mDb.collection(getString(R.string.userLocations_teachers))
                    .document(Integer.toString(RegisterActivity.prefConfig.readUserId()));
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

    private void getLastKnownLocation( final UserLocation UserLocation) {
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
                        UserLocation.setGeo_point(geoPoint);
                        UserLocation.setTimestamp(null);
                        saveUserLocation(UserLocation);
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
            markerOptions.title(userLocation.getRequests().getStudentName());
            markerOptions.snippet(userLocation.getRequests().toString());
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
