package becker.andy.map2018;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import becker.andy.map2018.fragments.AppointmentFragmentStudent;
import becker.andy.map2018.fragments.MapFragmentStudent;
import becker.andy.map2018.fragments.TeachersFragment;
import becker.andy.map2018.models.Requests;
import becker.andy.map2018.models.Teacher;
import becker.andy.map2018.models.UserLocation;
import becker.andy.map2018.models.UserLocationStudent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityStudent extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    private static final String TAG = MainActivityStudent.class.getSimpleName();
    private static final int ERROR_DIALOG_REQUEST = 9003;
    private boolean mLocationPermissionGranted = false;

    private ArrayList<Teacher> mTeachers=new ArrayList<>();
    private ArrayList<UserLocationStudent>mUserLocations=new ArrayList<>();

    private FusedLocationProviderClient mFusedLocationProviderClient;
    FirebaseFirestore mDb;

    private BottomNavigationView mMainNav;
    private FrameLayout mFragmentContainer;
    //fragments
    public MapFragmentStudent mapFragmentStudent;
    public TeachersFragment teachersFragment;
    public AppointmentFragmentStudent appointmentFragmentStudent;


    private ProgressBar mMainProgressBar;
    //refresh
    SwipeRefreshLayout mSwipeRefreshLayout;

    Bundle msavedInstanceState;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);
        msavedInstanceState=savedInstanceState;
        mDb = FirebaseFirestore.getInstance();
        mMainNav=findViewById(R.id.main_nav_student);
        mToolbar=findViewById(R.id.student_main_toolbar);
        setSupportActionBar(mToolbar);
        mFragmentContainer=findViewById(R.id.fragment_container_student);
        mMainProgressBar=findViewById(R.id.main_progressBar_student);
        mMainProgressBar.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout=findViewById(R.id.swiprefresh_student);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                initUser(new Bundle());
            }
        });





        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_places_student:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_student, mapFragmentStudent).commit();
                        return true;
                    case R.id.nav_teacher:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_student, teachersFragment).commit();
                        return true;
                    case R.id.nav_appointments_student:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_student, appointmentFragmentStudent).commit();
                        return true;
                    default:
                        return false;
                }
            }
        });

    }
    private synchronized void init(Bundle savedInstanceState) {

        mapFragmentStudent = new MapFragmentStudent();
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList(getString(R.string.userlocations_array),  mUserLocations);
        mapFragmentStudent.setArguments(bundle);

        teachersFragment=new TeachersFragment();
        Bundle bundle2=new Bundle();
        bundle2.putParcelableArrayList(getString(R.string.userlocations_array),  mUserLocations);
        teachersFragment.setArguments(bundle2);

        appointmentFragmentStudent=new AppointmentFragmentStudent();

        mMainProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);

        if (savedInstanceState != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_student, mapFragmentStudent).commit();
            return;
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_student, mapFragmentStudent).commit();







    }

    private synchronized void initUser(final Bundle savedInstanceState) {

        String insti=LoginActivity.prefConfig.readInsti();

        Call<List<Teacher>> call=LoginActivity.apiInterface.getTeachers(insti);

        call.enqueue(new Callback<List<Teacher>>() {
            @Override
            public void onResponse(Call<List<Teacher>> call, Response<List<Teacher>> response) {
                if(response.isSuccessful()){
                    mTeachers = (ArrayList<Teacher>) response.body();
                    mUserLocations=new ArrayList<>();
                    if(mTeachers.size()>0){
                        for(int i = 0; i< mTeachers.size(); i++){
                            synchronized (this){
                                DocumentReference locationRef=mDb.collection(getString(R.string.userLocations_teachers))
                                        .document(Integer.toString( mTeachers.get(i).getTeacher_id()));
                                final int finalI = i;
                                final int finalI1 = i;
                                locationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            if(task.getResult()!= null){
                                                Log.d(TAG, "Location onComplete: ");
                                                UserLocationStudent userLocation=task.getResult().toObject(UserLocationStudent.class);
                                                UserLocationStudent u=new UserLocationStudent();
                                                u.setTeacher(mTeachers.get(finalI1));
                                                u.setGeo_point(userLocation.getGeo_point());
                                                mUserLocations.add(u);
                                                Log.d(TAG, "initUser: in user array");
                                                if(finalI == mTeachers.size()-1){
                                                    //notify();
                                                    init(savedInstanceState);

                                                }

                                            }else {
                                                Log.d(TAG, "onComplete: result is empty");
                                            }
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivityStudent.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
                                    }
                                });

                            }

                        }
                    }else {
                        init(savedInstanceState);

                    }

                }
            }

            @Override
            public void onFailure(Call<List<Teacher>> call, Throwable t) {
                    Toast.makeText(MainActivityStudent.this,"Database Error: "+t.getMessage(),Toast.LENGTH_LONG).show();
                    init(savedInstanceState);
            }
        });




    }







    private void saveUserLocation(final UserLocation mUserLocation){
        if(mUserLocation != null){

            DocumentReference locationRef=mDb.collection(getString(R.string.collection_user_location_student))
                    .document(Integer.toString(LoginActivity.prefConfig.readUserId()));
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
            getLastKnownLocation(new UserLocation());
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivityStudent.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivityStudent.this, available, ERROR_DIALOG_REQUEST);
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
                    getLastKnownLocation(new UserLocation());
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


    @Override
    public void onRefresh() {
        initUser(msavedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_activity_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.student_logout:
                LoginActivity.prefConfig.writeUser("none");
                LoginActivity.prefConfig.writeInsti("none");
                LoginActivity.prefConfig.writeEmail("none");
                LoginActivity.prefConfig.writeLoginStatus(false);
                LoginActivity.prefConfig.writeUserId(0);
                finish();
                startActivity(new Intent(MainActivityStudent.this,LoginActivity.class));
                return  true;
                default:
                    return true;

        }
    }
}

