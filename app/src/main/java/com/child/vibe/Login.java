package com.child.vibe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.child.vibe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {

    // firebase fields
    FirebaseFirestore db;
    FirebaseAuth auth;
    public static Users user;

    // widget fields
    EditText emailET, passET;
    Button loginBtn;
    TextView newUserRedirectText, forgotPassword;
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
            setUserObject();
            startActivity(new Intent(getApplicationContext(),ChatLog.class));
//            finish();
        }

        //forgot password, firebase email
        //getting email address
        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //basically same as line 83. can we create local variable??
                String email = emailET.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "We sent an email with instructions on how to reset your password.", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(Login.this, "Check spam if not seeing email.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Login.this, "Failed to send email. Try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


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
                    Toast.makeText(Login.this, "please fill all text fields" ,Toast.LENGTH_SHORT).show();
                } else {
                    attemptLogin(emailText, passwordText);
                }
            }
        });

        // Redirecting to registration on pressing of the "New Account" text
        newUserRedirectText = (TextView)findViewById(R.id.newAccount);
        newUserRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
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
                            setUserObject();
                            startActivity(new Intent(getApplicationContext(),ChatLog.class));
                        } else {
                            Toast.makeText(Login.this, "Incorrect email or password" ,Toast.LENGTH_SHORT).show();
                            //TODO: throw exception
                        }
                    }
                });
    }

    private void setUserObject() {
        db.collection("users").whereEqualTo("id", auth.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().isEmpty())
                                return;
                        if(task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            user = document.toObject(Users.class);
                            user.setUid(auth.getUid());
                        }
                    }
                });
    }
}