package com.example.owner_test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.util.List;

public class MainScreen extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    LatLng loc;
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();

    public void search(View view){
        Toast.makeText(this,"Searching for nearby owners",Toast.LENGTH_SHORT).show();

        fStore.collection("ownerLocationData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.exists() && document!=null) {
                            double lat = document.getDouble("latitude");
                            double lng = document.getDouble("longitude");
                            loc = new LatLng(lat, lng);
                            locations.add(loc);
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

    public void goBack(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        fStore= FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

    }
}
