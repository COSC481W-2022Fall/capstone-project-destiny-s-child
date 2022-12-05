package com.example.vibe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    //widgets
    EditText userET, passET, emailET;
    Button registerBtn;
    TextView accountAlreadyExists;
    //access Firebase authentication
    FirebaseAuth auth;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;
    DocumentReference documentReference;
    String userID;
    ArrayList<String> usernames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initializing widgets
        userET = findViewById(R.id.fullname);
        passET = findViewById(R.id.password);
        emailET = findViewById(R.id.email);
        registerBtn = findViewById(R.id.registerBtn);
        accountAlreadyExists = (TextView)findViewById(R.id.createtext);

        //instantiating Firebase authentication
        auth = FirebaseAuth.getInstance();
        //instantiating Firestore Database
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        usernames.add(document.getId());
                                    }
                                }
                                else{
                                    Log.d("", task.getException().toString());
                                }
                            }
                        });

        // Will redirect to chat log on success
        // adding event listener to register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_text = emailET.getText().toString().trim();
                String username_text = userET.getText().toString().trim();
                String password_text = passET.getText().toString().trim();
                userID = username_text;
                // return if values missing
                if(missingValueError(email_text, username_text, password_text)) {
                    Toast.makeText(Register.this, "please fill all text fields" ,Toast.LENGTH_SHORT).show();
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
                if(username.length() > 20) {
                    userET.setError("Username is too long. 20 characters maximum.");
                    valuesMissing = true;
                }
                if(username.length() < 4) {
                    userET.setError("Username is too short. 4 characters minimum.");
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
        accountAlreadyExists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
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
                            //This block of code verifies username
                            if(usernames.contains(userID)){
                                Toast.makeText(Register.this, "Username is already in use", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                //in the users collection with the specific user ID, map the corresponding values and put them into the firestore database
                                documentReference = db.collection("users").document(userID);
                                CollectionReference collectionReference = db.collection("users");
                                Map<String, Object> user = new HashMap<>();
                                user.put("email", email);
                                user.put("username", username);
                                user.put("password", password);
                                //getting the Authentication Uid to store with that user's info
                                user.put("id", auth.getUid());

                                //creating collection to be referenced for blocked users
                                Map<String, Object> blocklist = new HashMap<>();
                                blocklist.put("name", "test");

                                Users users = new Users(auth.getUid(), email, username, password);
                                //adds user input into Firestore database
                                documentReference.set(user);
                                documentReference.collection("blocklist").document("test").set(blocklist);
                                //if successful, directs you to the login page
                                Toast.makeText(Register.this, "Registration Success!", Toast.LENGTH_SHORT).show();
                                Login.user = users;
                                startActivity(new Intent(getApplicationContext(), ChatLog.class));
                            }
                        }else{
                            //Toast.makeText(register.this, "Email is already in use, please register using a different email address." ,Toast.LENGTH_LONG).show();
                            Toast.makeText(Register.this, "Invalid email, please register using a different email address." ,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
