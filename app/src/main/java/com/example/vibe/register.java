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

//        if(auth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(),ChatLog.class));
//            finish();
//        }

        // Will redirect to chat log on success
        // adding event listener to register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_text = emailET.getText().toString().trim();
                String username_text = userET.getText().toString();
                String password_text = passET.getText().toString().trim();

                // return if values missing
                if(missingValueError(email_text, username_text, password_text)) {
                    Toast.makeText(register.this, "please fill all text fields" ,Toast.LENGTH_SHORT).show();
                    return;
                }

                //if all fields are filled, call RegisterNow method
                RegisterNow(username_text, email_text, password_text);
            }

            // set error message for fields with missing values
            private boolean missingValueError(String email, String username, String password) {
                boolean valuesMissing = false;

                if(TextUtils.isEmpty(email)){
                    emailET.setError("Email is required.");
                    valuesMissing = true;
                }

                if(TextUtils.isEmpty(username)){
                    userET.setError("Username is required.");
                    valuesMissing = true;
                }

                if(TextUtils.isEmpty(password)){
                    passET.setError("Password is required.");
                    valuesMissing = true;
                }else if(password.length() < 6){
                    passET.setError("Must be longer than 6 characters.");
                    valuesMissing = true;
                }

                return valuesMissing;
            }
        });

        // Will redirect to Login
        // Redirecting to login on pressing of the "Already have an account" text
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),login.class));
            }
        });
    }

    /**
     * Will register a new user with their username, email, and password.
     * @param username the username entered into the username text field
     * @param email the email entered into the email text field
     * @param password the password entered into the password text field
     */
    private void RegisterNow(final String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            // TODO: handle email already existing

                            //creates a user ID for an account that is to be authorized
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            userID = firebaseUser.getUid();

                            //in the users collection with the specific user ID, map the corresponding values and put them into the firestore database
                            DocumentReference documentReference = db.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("email",email);
                            user.put("username",username);
                            user.put("password",password);

                            //adds user input into Firestore database
                            documentReference.set(user);
                            //if successful, directs you to the login page
                            Toast.makeText(register.this, "Registration Success!" ,Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),ChatLog.class));

                        }else{
                            //Toast.makeText(register.this, "Email is already in use, please register using a different email address." ,Toast.LENGTH_LONG).show();
                            Toast.makeText(register.this, "Invalid email, please register using a different email address." ,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
