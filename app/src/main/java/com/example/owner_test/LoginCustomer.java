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


public class LoginCustomer extends AppCompatActivity {
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
                    Toast.makeText(LoginCustomer.this,"User successfully login",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),CustomerMainScreen.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginCustomer.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void register(View view) {
        String oPassword = password.getText().toString();
        final String oEmail = email.getText().toString();
        final String oName = name.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        fAuth.createUserWithEmailAndPassword(oEmail,oPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginCustomer.this, "User created", Toast.LENGTH_LONG).show();
                    userId = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("Customers").document(userId);
                    Map<String,Object> customer = new HashMap<>();
                    customer.put("name",oName);
                    customer.put("email",oEmail);
                    documentReference.set(customer).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG","Success "+ userId);
                        }

                    });
                }
                else{
                    Toast.makeText(LoginCustomer.this,"failed to create database",Toast.LENGTH_SHORT).show();

                    Toast.makeText(LoginCustomer.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });


        Intent intent = new Intent(getApplicationContext(),CustomerMainScreen.class);
        startActivity(intent);
        finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_customer);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        name = findViewById(R.id.nameEditText);
        password = findViewById(R.id.passwordEditText);
        email = findViewById(R.id.emailEditText);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        fAuth = FirebaseAuth.getInstance();



        //if(fAuth.getCurrentUser() != null){
        //  startActivity(new Intent(getApplicationContext(),MainScreen.class));

        //}


    }
}
