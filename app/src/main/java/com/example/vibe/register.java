package com.example.vibe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {
    //widgets
    EditText userET, passET, emailET;
    Button registerBtn;
    TextView logIn;
    //access Firebase authentication
    FirebaseAuth auth;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initializing widgets
        userET = findViewById(R.id.fullname);
        passET = findViewById(R.id.password);
        emailET = findViewById(R.id.email);
        registerBtn = findViewById(R.id.registerBtn);
        logIn = (TextView)findViewById(R.id.createtext);


        //instantiating Firebase authentication
        auth = FirebaseAuth.getInstance();
        //instantiating Firestore Database
        db = FirebaseFirestore.getInstance();

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),ChatLog.class));
            finish();
        }

        //adding event listener to register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_text = emailET.getText().toString();
                String username_text = userET.getText().toString();
                String password_text = passET.getText().toString();

                //checks to see if all fields are filled (very basic check)
                if (TextUtils.isEmpty(email_text) || TextUtils.isEmpty(username_text) || TextUtils.isEmpty(password_text)){
                    Toast.makeText(register.this, "please fill all text fields" ,Toast.LENGTH_SHORT).show();
                }else{
                    //if all fields are filled, call RegisterNow method
                    RegisterNow(username_text, email_text, password_text);
                }
            }
        });
    }


    private void RegisterNow(final String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            //creates a user ID for an account that is to be authorized
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            userID = firebaseUser.getUid();

                            //in the users collection with the specific user ID, map the corresponding values and put them into the firestore database
                            DocumentReference documentReference = db.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("email",email);
                            user.put("username",username);
                            user.put("password",password);

                            //if successful, directs you to the login page
                            startActivity(new Intent(getApplicationContext(),ChatLog.class));

                        }

                    }
                });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),login.class));
            }
        });

    }

}
