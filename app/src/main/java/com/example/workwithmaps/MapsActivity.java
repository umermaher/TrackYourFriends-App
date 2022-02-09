package com.example.workwithmaps;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.workwithmaps.databinding.ActivityMapsBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, InputDialog.InputDialogListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Marker marker;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        List<LatLng> myLocations=new ArrayList<>();
        LatLng unit6=new LatLng(25.3681,68.3503);
        myLocations.add(unit6);
        LatLng unit5=new LatLng(25.3615,68.3444);
        myLocations.add(unit5);

        addMarker(unit6,"unit 6");
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                addMarker(latLng,"Custom marker");
                Toast.makeText(MapsActivity.this, "Latitude: "+latLng.latitude+", Longitude: "+latLng.longitude,
                        Toast.LENGTH_SHORT).show();
            }
        });
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private void addMarker(LatLng latLng,String title){
        if(marker!=null)
            marker.remove();
        MarkerOptions options=new MarkerOptions();
        options.title(title);
        options.position(latLng);

//        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round));
        marker=mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    public void satellite(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void hybrid(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    public void terrain(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void normal(View view) {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void currLocation(View view) {
        SharedPreferences sp=getSharedPreferences("MyData",MODE_PRIVATE);
        count=sp.getInt("counter",0);
        if(count==0) {
            InputDialog dialog = new InputDialog();
            dialog.show(getSupportFragmentManager(), "inputDialog");
        }else{
            Intent i=new Intent(MapsActivity.this,CurrLocation.class);
            startActivity(i);
        }
    }

    @Override
    public void getData(String userName, String phoneNo) {
//        Toast.makeText(this, userName+" "+phoneNo, Toast.LENGTH_SHORT).show();

        SharedPreferences sp=getSharedPreferences("MyData",MODE_PRIVATE);
        count=sp.getInt("counter",0);
        count++;
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor=sp.edit();
        editor.putInt("counter",count);
        editor.putString("username",userName);
        editor.putString("phoneno",phoneNo);
        editor.commit();  //save changes
//        editor.remove("counter");  //remove data having key counter
//        editor.clear();  // clear all data

        Intent i=new Intent(MapsActivity.this,CurrLocation.class);
        startActivity(i);
    }
}