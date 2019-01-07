package becker.andy.map2018.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import becker.andy.map2018.R;
import becker.andy.map2018.SetAppointmentActivity;
import becker.andy.map2018.classes.UserClient;
import becker.andy.map2018.models.User;
import becker.andy.map2018.models.UserLocation;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragments";
    private Context globalContext = null;
    private boolean mBackThread=false;

    //googleMap
    private GoogleMap mGoogleMap;

    private LatLngBounds latLngBoundary;

    FusedLocationProviderClient mFusedLocationProviderClient;
    //UserLocation
    UserLocation mUserPosition;
    //firebase
    FirebaseFirestore mDb;
    //array
    private ArrayList<User> mUserList = new ArrayList<>();
    private List<UserLocation> mUserLocations = new ArrayList<>();
    private ArrayList<MarkerOptions>mMarkerOptions=new ArrayList<>();

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mUserLocations=getArguments().getParcelableArrayList(getString(R.string.userlocations_array));
        mDb=FirebaseFirestore.getInstance();
        globalContext = this.getActivity();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastKnownLocation();


        return view;
    }
    





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                for(UserLocation u: mUserLocations){
                    if(u.getRequests().getStudentName().equals(marker.getTitle())){
                        //marker.setVisible(false);
                        Log.d(TAG, "onMarkerDragStart: "+u.getRequests().getStudentName());
                        Intent intent=new Intent(getActivity(),SetAppointmentActivity.class);
                        intent.putExtra(getString(R.string.appointment_extra),u);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });
    }




    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: ");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLastKnownLocation: inside");
            return;
        }
        Log.d(TAG, "getLastKnownLocation: outside");
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Log.d(TAG, "onComplete: inside");
                        if(task.isSuccessful()) {
                            if (task.getResult() != null) {


                                GeoPoint geoPoint = new GeoPoint(task.getResult().getLatitude(), task.getResult().getLongitude());
                                Log.d(TAG, "onComplete: latitude " + geoPoint.getLatitude());
                                Log.d(TAG, "onComplete: longitude " + geoPoint.getLongitude());
                                mUserPosition=new UserLocation();
                                mUserPosition.setGeo_point(geoPoint);
                                mUserPosition.setTimestamp(null);
                                setCameraView();
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


    private void setCameraView(){
        if(mUserPosition!= null){
            Log.d(TAG, "setCameraView: user position got");
            double bottomboundary=mUserPosition.getGeo_point().getLatitude()-.005;
            double leftboundary = mUserPosition.getGeo_point().getLongitude()-.005;
            double upboundary = mUserPosition.getGeo_point().getLatitude()+.005;
            double rightboundary = mUserPosition.getGeo_point().getLongitude()+.005;
            latLngBoundary=new LatLngBounds(new LatLng(bottomboundary,leftboundary),
                    new LatLng(upboundary,rightboundary));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundary,0));

        }else {
            Log.d(TAG, "setCameraView: user position is null");
        }

        if(mGoogleMap != null){
            if(mUserLocations.size()>0){
                for(UserLocation userLocation: mUserLocations){
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(userLocation.getRequests().getStudentName());
                    Log.d(TAG, "onComplete: " + userLocation.getRequests().getStudentName());
                    Log.d(TAG, "onComplete: " + userLocation.getGeo_point().toString());
                    markerOptions.snippet(userLocation.getRequests().toString());
                    markerOptions.draggable(true);
                    markerOptions.position(new LatLng(userLocation.getGeo_point().getLatitude(), userLocation.getGeo_point().getLongitude()));
                    mGoogleMap.addMarker(markerOptions);
                }
            }else {
                Log.d(TAG, "setCameraView: marker size zero");
            }
        }else {
            Log.d(TAG, "setCameraView: map is null");
        }
    }

    private void initUser() {
        User user=new User();
        user.setEmail("nobeld@gmail.com");
        user.setResponse("ok");
        user.setUser("student");
        user.setUserId(5);
        user.setUserName("nobel");
        ((UserClient)(getActivity().getApplicationContext())).setUser(user);
        mUserList.add(user);
        User user1=new User();
        user1.setEmail("rahuld@gmail.com");
        user1.setResponse("ok");
        user1.setUser("student");
        user1.setUserId(6);
        user1.setUserName("rahul");
        User user2=new User();
        user2.setEmail("milond@gmail.com");
        user2.setResponse("ok");
        user2.setUser("student");
        user2.setUserId(7);
        user2.setUserName("milon");
        mUserList.add(user1);
        mUserList.add(user2);

        for(User u: mUserList){
            getUserLocation(u);
            Log.d(TAG, "initUser: in user array");
        }
    }

    private void getUserLocation(User user){
        Log.d(TAG, "getUserLocation: ");
        try {
            CollectionReference locationRef=mDb.collection(getString(R.string.collection_user_location_student));
            locationRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot documentSnapshot: task.getResult()){
                            UserLocation userLocation1=documentSnapshot.toObject(UserLocation.class);
                            mUserLocations.add(userLocation1);
                        }
                    }
                }
            });

        }catch (NullPointerException n){
            Log.d(TAG, "getUserLocation: "+n.getMessage());
        }
    }

}
