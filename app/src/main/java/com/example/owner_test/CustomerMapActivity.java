package com.example.owner_test;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    int searchRadius = 2000;
    Button searchButton;

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

        return address;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                try {
                    if(lastKnownLocation!=null) {
                        LatLng ownerLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        centerMapOnLocation(lastKnownLocation, "your location"/*getAddress(ownerLocation)*/);
                    }
                    else{
                        Toast.makeText(this,"Location NULL",Toast.LENGTH_SHORT).show();
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, locationListener);
                        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        LatLng ownerLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        centerMapOnLocation(lastKnownLocation, "your location"/*getAddress(ownerLocation)*/);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();;
                }
            }
        }
    }

    public void showNearest(View view){
        for(int i=0;i<CustomerMainScreen.locations.size();i++) {
            LatLng ownerLocation = new LatLng(CustomerMainScreen.locations.get(i).getLatitude(), CustomerMainScreen.locations.get(i).getLongitude());
            //Toast.makeText(this,"adding marker " + i+"/"+MainScreen.locations.size(),Toast.LENGTH_SHORT).show();
            Log.i("TAG","adding marker " + i +"/"+ CustomerMainScreen.locations.size());
            //mMap.addMarker(new MarkerOptions().position(MainScreen.locations.get(i)).title(getAddress(MainScreen.locations.get(i))));
           if(lastKnownLocation.distanceTo(CustomerMainScreen.locations.get(i)) < searchRadius){
                //mMap.addMarker(new MarkerOptions().position(ownerLocation).title(getAddress(ownerLocation)));
               mMap.addMarker(new MarkerOptions().position(ownerLocation).title("Number of customers - "+Long.toString(CustomerMainScreen.blCount.get(i))));
           }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        searchButton = findViewById(R.id.searchButton);





    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
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
                    centerMapOnLocation(lastKnownLocation, "your location"/*getAddress(ownerLocation)*/);
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

}
