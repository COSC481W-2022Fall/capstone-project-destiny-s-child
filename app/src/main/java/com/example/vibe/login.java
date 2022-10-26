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

    // firebase fields
    FirebaseFirestore db;
    FirebaseAuth auth;

    // widget fields
    EditText emailET, passET;
    Button loginBtn;
    TextView newUserRedirectText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        // initializing widgets
        emailET = findViewById(R.id.email);
        passET = findViewById(R.id.password);

        // getting Firebase authentication and Database instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //if user is already logged in, direct user to chatLog view
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),ChatLog.class));
            finish();
        }

        // Begin attempt to login by getting email and password
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getting text field values
                String emailText = emailET.getText().toString();
                String passwordText = passET.getText().toString();

                // displays toast if value missing -- attempts login
                if(emailText.isEmpty() || passwordText.isEmpty()) {
                    Toast.makeText(login.this, "please fill all text fields" ,Toast.LENGTH_SHORT).show();
                } else {
                    attemptLogin(emailText, passwordText);
                }
            }
        });

        // Redirecting to registration on pressing of the "New Account" text
        newUserRedirectText = (TextView)findViewById(R.id.createtext);
        newUserRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),register.class));
            }
        });
    }

    /**
     * Will attempt to sign in with email and password via firebase authorization
     * and display a toast if either email or password is incorrect.
     * @param emailText the email entered into the email text field
     * @param passwordText the password entered into the password text field
     */
    private void attemptLogin(String emailText, String passwordText) {
        auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(),ChatLog.class));
                        } else {
                            Toast.makeText(login.this, "Incorrect email or password" ,Toast.LENGTH_SHORT).show();
                            //TODO: throw exception
                        }
                    }
                });
    }
}