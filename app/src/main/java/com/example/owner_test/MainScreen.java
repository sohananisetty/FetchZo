package com.example.owner_test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MainScreen extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    LatLng loc;
    Button searchButton;
    TextView statusTextView;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<String> blDevice = new ArrayList<String>();
    //static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    //static ArrayList<Location> locations = new ArrayList<Location>();
   /* private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i("Bluetooth",deviceName+" "+ deviceHardwareAddress);
            }

        }
    };
*/
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                searchButton.setEnabled(true);
                statusTextView.setText("finished");
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.addAll(blDevice);
                DocumentReference documentReference = fStore.collection("ownerLocationData").document(userId);
                final Map<String,Object> deviceCount = new HashMap<>();

                deviceCount.put("count",hashSet.size());

                documentReference.update(deviceCount).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG","count "+ userId + " "+ deviceCount);
                        Toast.makeText(MainScreen.this,"count uploaded "+ userId + " "+ deviceCount,Toast.LENGTH_SHORT).show();


                    }

                });
                Toast.makeText(MainScreen.this,"Number of devices present  "+hashSet.size(),Toast.LENGTH_LONG).show();
                searchButton.setEnabled(false);
                statusTextView.setText("Searching...");
                bluetoothAdapter.startDiscovery();

                //unregisterReceiver(broadcastReceiver);

            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                blDevice.add(deviceHardwareAddress);
                Log.i("Bluetooth",deviceName+" "+ deviceHardwareAddress);
            }
        }
    };

    public void searchBluetooth(View view){
        searchButton.setEnabled(false);
        statusTextView.setText("Searching...");
        userId = fAuth.getCurrentUser().getUid();
        bluetoothAdapter.startDiscovery();

    }

   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        searchButton.setEnabled(true);
        statusTextView.setText("Finished");

    }
    */

  /*  public void search(View view){
        Toast.makeText(this,"Searching for nearby owners",Toast.LENGTH_SHORT).show();

        fStore.collection("ownerLocationData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.exists() && document!=null) {
                            double lat = document.getDouble("latitude");
                            double lng = document.getDouble("longitude");
                            //loc = new LatLng(lat, lng);
                            //locations.add(loc);
                            Location temp = new Location(LocationManager.GPS_PROVIDER);
                            temp.setLatitude(lat);
                            temp.setLongitude(lng);
                            locations.add(temp);
                            String location = Double.toString(lat) + " " + Double.toString(lng);
                            Log.i("loc", location);
                            Toast.makeText(MainScreen.this,location,Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Log.i("TAG", "Error getting documents: ");
                }
            }
        });

        startActivity(new Intent(getApplicationContext(),CustomerMapActivity.class));
        finish();




    }

   */

    public void goBack(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(),OwnerLoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        fStore= FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        searchButton = findViewById(R.id.bluetoothButton);
        statusTextView = findViewById(R.id.bluetoothSearchTextView);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver,intentFilter);



        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(receiver, filter);
        //bluetoothAdapter.startDiscovery();
    }
}
