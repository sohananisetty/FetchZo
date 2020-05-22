package com.example.owner_test;

import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ProgressBar;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.FirebaseFirestore;

        import java.util.HashMap;
        import java.util.Map;


public class OwnerLoginActivity extends AppCompatActivity {
    Button loginButton;
    Button registerButton;
    EditText name;
    EditText password;
    EditText email;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;





    public void login(View view){

        String oPassword = password.getText().toString().trim();
        String oEmail = email.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        fAuth.signInWithEmailAndPassword(oEmail,oPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(OwnerLoginActivity.this,"User successfully login",Toast.LENGTH_LONG).show();
                    //Intent intent = new Intent(getApplicationContext(),MainScreen.class);
                    Intent intent = new Intent(getApplicationContext(),main.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(OwnerLoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void register(View view) {
        String oPassword = password.getText().toString();
        String oEmail = email.getText().toString();
        String oName = name.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
        intent.putExtra("name",oName);
        intent.putExtra("email",oEmail);
        intent.putExtra("password",oPassword);
        startActivity(intent);
        finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_login);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        name = findViewById(R.id.nameEditText);
        password = findViewById(R.id.passwordEditText);
        email = findViewById(R.id.emailEditText);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        fAuth = FirebaseAuth.getInstance();



        /*if(fAuth.getCurrentUser() != null){
          //startActivity(new Intent(getApplicationContext(),MainScreen.class));
            //startService(new Intent(getApplicationContext(),BluetoothService.class));
            startActivity(new Intent(getApplicationContext(),main.class));


        }*/


    }
}
