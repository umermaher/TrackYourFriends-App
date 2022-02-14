package com.example.workwithmaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.workwithmaps.databinding.ActivityCurrLocationBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
//import android.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//onCreate -> Request permission
//checkLocationPermission -> check user's location permission
//onRequestPermissionLocation -> will be call when user allow or denied our permission
//BuildGoogleApiClient -> Build a client for connection, connection suspended and location service.
//onConnected() -> When your client has made connection with location services
//onConnectedSuspended() -> When your client is disconnected with location services
//onLocationChanged() -> Update your current location after five seconds
//EXTRA -> gpsDialog() -> show dialog to turn on location

public class CurrLocation extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    private ActivityCurrLocationBinding binding;
    private Location mLastLocation;
    private Marker mCurrLocationMarker,marker;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LatLng latLng;
    protected static final int REQUEST_CHECK_SETTINGS = 99;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    FusedLocationProviderClient mProviderClient;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    List<User> userList;
    Pref pref;
    int countForZoomCurrentLoc=0;
    String userName,phoneNo;
    LocationCallback locationCallBack=new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            onLocationChanged(locationResult.getLastLocation());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCurrLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        rootNode=FirebaseDatabase.getInstance();
        reference=rootNode.getReference("Users");

        userList=new ArrayList<>();
        //saved data of user
        pref=new Pref(CurrLocation.this);
        userName=pref.getUserName();
         phoneNo=pref.getPhoneNo();

        //Checking operating system
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
//        LocationListener;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //initialize google play services.
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }else {
                checkLocationPermission();
            }
        }else{
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    //Check location permission
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Asking user when explanation is needed

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Show an explanation to the user
                //this thread wait for the user's response! after the user sees the explanation, try again to request the permission
                //Prompt the user once the explanation has been shown.
                String[] s= new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this,s, MY_PERMISSIONS_REQUEST_LOCATION);
                //show a dialog for getting gps base permission
            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient= new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

//    When your application has successfully connected with Location Services
    @SuppressLint({"NewApi", "VisibleForTests"})
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(5000);  //After how much seconds u want to update location
        mLocationRequest.setFastestInterval(3000); //update location in background.

        gpsDialog();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            //start updating user current location
            mProviderClient=new FusedLocationProviderClient(this);
            mProviderClient.requestLocationUpdates(mLocationRequest, locationCallBack,getMainLooper());
//            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mGoogleApiClient,mLocationRequest, (LocationListener) this);
        }
    }

    private void gpsDialog() {
        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> responseTask=LocationServices
                .getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());
        responseTask.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response=task.getResult(ApiException.class);
                } catch (ApiException e) {
                    if(e.getStatusCode()==LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                        ResolvableApiException resolvableApiException=(ResolvableApiException)e;
                        try {
                            resolvableApiException.startResolutionForResult(CurrLocation.this,REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            Log.i("tag","PendingIntent unable to execute request.");
                        }
                    }
                    if(e.getStatusCode()==LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE){
                        Toast.makeText(CurrLocation.this, "Settings not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //call on every 5000 milliseconds
    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLastLocation =location;
        if(mCurrLocationMarker !=null){
            mCurrLocationMarker.remove();
        }
        //Place current location Marker
        latLng=new LatLng(location.getLatitude(),location.getLongitude());
        Toast.makeText(this, "Latitude: "+latLng.latitude+", Longitude: "+latLng.longitude,
                Toast.LENGTH_SHORT).show();
        MarkerOptions options=new MarkerOptions();
        options.position(latLng);
        options.title("Current location");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker=mMap.addMarker(options);
        //Move map camera
        if(countForZoomCurrentLoc==0){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo((float) 14.5));
            countForZoomCurrentLoc++;
        }


        User user=new User(userName,phoneNo,String.valueOf(latLng.latitude),String.valueOf(latLng.longitude));
        reference.child(user.getPhoneNo()).setValue(user);

        //other users
        otherUsersLocation();
//        //stop location updates
//        if(mGoogleApiClient!=null){
//            mProviderClient.removeLocationUpdates(locationCallBack);
////            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
//        }
    }

    private void otherUsersLocation(){
        if(marker!=null)
            marker.remove();
        userList.clear();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        userList.add(user);
                    }
                    for(User user:userList){
                        if(!userName.equals(user.getUserName())){
                            LatLng latLng=new LatLng(Double.parseDouble(user.getLatitude()),Double.parseDouble(user.getLongitude()));
                            String title=user.getUserName();
                            MarkerOptions options=new MarkerOptions();
                            options.title(title).position(latLng);
                            marker=mMap.addMarker(options);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                //if request is empty, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission was granted. Do the contacts related task you need to do
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }else{
//                    Permission denied, disable the functionality that depends on this permission
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient!=null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient!=null)
            if(mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();
        if(mProviderClient!=null)
            mProviderClient.removeLocationUpdates(locationCallBack);
    }
}