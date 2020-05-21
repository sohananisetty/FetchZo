package com.example.owner_test;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    static LatLng loc;
    static String ownerAddress;
    String userId;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String oEmail;
    String oPassword;
    String oName;

    public void confirmLocation(View view){
        Intent rec = getIntent();
         oName  = rec.getStringExtra("name");
         oEmail  = rec.getStringExtra("email");
         oPassword  = rec.getStringExtra("password");
        fAuth.createUserWithEmailAndPassword(oEmail,oPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MapsActivity.this,"User created",Toast.LENGTH_LONG).show();
                    userId = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("Owners").document(userId);
                    DocumentReference documentReference2 = fStore.collection("ownerLocationData").document(userId);

                    Map<String,Object> owner = new HashMap<>();
                    Map<String,Object> ownerLocation = new HashMap<>();

                    owner.put("name",oName);
                    owner.put("email",oEmail);
                    owner.put("address",ownerAddress);
                    ownerLocation.put("longitude",loc.longitude);
                    ownerLocation.put("latitude",loc.latitude);
                   // ownerLocation.put("count",0);
                    //ownerLocation.put("location",loc);
                    documentReference.set(owner).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG","Success "+ userId);
                        }

                    });
                    documentReference2.set(ownerLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Owner Location","Success "+ userId);
                            Toast.makeText(MapsActivity.this,"Owner location saved in database",Toast.LENGTH_SHORT).show();
                        }

                    });



                }
                else{
                    Toast.makeText(MapsActivity.this,"failed to create database",Toast.LENGTH_SHORT).show();

                    Toast.makeText(MapsActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
        Intent intent = new Intent(getApplicationContext(),main.class);
        startActivity(intent);
        finish();
    }

    public void centerMapOnLocation(Location location, String title){
        if (location != null ) {

            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
        }
    }

    public String getAddress(LatLng latLng){
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try{
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(listAddresses != null && listAddresses.size()>0){
                if (listAddresses.get(0).getThoroughfare()!= null) {
                    if (listAddresses.get(0).getSubThoroughfare()!= null) {

                        address += listAddresses.get(0).getSubThoroughfare()+" ";
                    }
                    address += listAddresses.get(0).getThoroughfare();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(address.equals("")){
            address += Double.toString(latLng.latitude)+" "+Double.toString(latLng.longitude);
        }
        loc = latLng;
        ownerAddress = address;
        return address;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                try {
                    if(lastKnownLocation!=null) {
                        LatLng ownerLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        centerMapOnLocation(lastKnownLocation, getAddress(ownerLocation));
                    }
                    else{
                        Toast.makeText(this,"Location NULL",Toast.LENGTH_SHORT).show();
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, locationListener);
                        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        LatLng ownerLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        centerMapOnLocation(lastKnownLocation, getAddress(ownerLocation));
                    }
                }
                catch(Exception e){
                    e.printStackTrace();;
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                    //centerMapOnLocation(location, "your location");
                  //  Log.i("info","update_onLocationChanged");

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.i("info","update_permission");
            try {
                if(lastKnownLocation!=null) {
                    LatLng ownerLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    centerMapOnLocation(lastKnownLocation, getAddress(ownerLocation));
                }
                else{
                    Toast.makeText(this,"Location NULL",Toast.LENGTH_SHORT).show();
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, locationListener);
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    LatLng ownerLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    centerMapOnLocation(lastKnownLocation, getAddress(ownerLocation));
                }
            }
            catch(Exception e){
                e.printStackTrace();;
            }
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        mMap.clear();
        loc = latLng;
        mMap.addMarker(new MarkerOptions().position(latLng).title(getAddress(latLng)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }
}
