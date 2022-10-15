package com.example.vibe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class login extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth auth;

    EditText emailET, passET;
    Button loginBtn;
    TextView newUserRedirectText;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        // temporary redirect to chat log view
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getting text field values
                String emailText = emailET.getText().toString();
                String passwordText = passET.getText().toString();

                // checks for missing value -- attempts login
                if(emailText.isEmpty() || passwordText.isEmpty()) {
                    Toast.makeText(login.this, "please fill all text fields" ,Toast.LENGTH_SHORT).show();
                } else {
                    attemptLogin(emailText, passwordText);
                }
            }
        });

        // initializing widgets
        emailET = findViewById(R.id.email);
        passET = findViewById(R.id.password);

        // instantiating Firebase authentication and Database
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        newUserRedirectText = (TextView)findViewById(R.id.createtext);
        newUserRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),register.class));
            }
        });
    }

    private void attemptLogin(String emailText, String passwordText) {
        auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(),ChatLog.class));
                        } else {
                            Toast.makeText(login.this, "Incorrect email or password" ,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}