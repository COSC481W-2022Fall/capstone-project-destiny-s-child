package com.emich.vibe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emich.vibe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePassword extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText PwdCurr, PwdNew, PwdConfirm;
    private TextView ViewAuthenticate;
    private Button ChangePwd, ConAuthenticate;
    private ProgressBar progressBar;
    private String userCurrPwd;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setTitle("Change password");

        PwdNew = findViewById(R.id.newpassword);
        PwdCurr = findViewById(R.id.currentpass);
        PwdConfirm = findViewById(R.id.conPwd);
        ConAuthenticate = findViewById(R.id.authenticatepass);
        ViewAuthenticate = findViewById(R.id.Viewauthenticate);
        progressBar = findViewById(R.id.progressBar);
        ChangePwd = findViewById(R.id.update_button);

        //provides new password, confirm password and the update button from being clickable till current pass is authenticated
        PwdNew.setEnabled(false);
        PwdConfirm.setEnabled(false);
        ChangePwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser.equals("")){
            Toast.makeText(ChangePassword.this, "Something went wrong! User's details are not avaiable",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePassword.this, Settings_View.class);
            startActivity(intent);
            finish();
        }
        else {
            reAuthenticateUser(firebaseUser);
        }
    }

    //reAuthenticate user before the changing password process
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        ConAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userCurrPwd = PwdCurr.getText().toString();

                if(TextUtils.isEmpty(userCurrPwd)){
                    Toast.makeText(ChangePassword.this, "Password is required", Toast.LENGTH_SHORT).show();
                    PwdCurr.setError("Please enter your current password to authenticate");
                    PwdCurr.requestFocus();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);

                    //reauthenticate
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userCurrPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);

                                //Disable for current pass but enabled for new password and confirm pass
                                PwdCurr.setEnabled(false);
                                PwdNew.setEnabled(true);
                                PwdConfirm.setEnabled(true);


                                //Enable update button, disables authentication button
                                ConAuthenticate.setEnabled(false);
                                ChangePwd.setEnabled(true);

                                ViewAuthenticate.setText("Your are authenticated/verified." + "You can change password now!");
                                Toast.makeText(ChangePassword.this, "Password has been verified." + "change password now", Toast.LENGTH_SHORT).show();

                                ChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(ChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = PwdNew.getText().toString();
        String userConPwd = PwdConfirm.getText().toString();

        if(TextUtils.isEmpty(userPwdNew)) {
            Toast.makeText(ChangePassword.this, "New password needed", Toast.LENGTH_SHORT).show();
            PwdNew.setError("Please enter your new password");
            PwdNew.requestFocus();
        }else if(PwdNew.length() < 6){
            Toast.makeText(ChangePassword.this, "Password needs to be 6 characters or longer", Toast.LENGTH_SHORT).show();
            PwdNew.setError("Please re-enter your new password");
            PwdNew.requestFocus();
        }else if(TextUtils.isEmpty(userConPwd)){
            Toast.makeText(ChangePassword.this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            PwdConfirm.setError("Please re-enter your new password");
            PwdConfirm.requestFocus();
        }else if(!userPwdNew.matches(userConPwd)){
            Toast.makeText(ChangePassword.this, "Passsword did not match", Toast.LENGTH_SHORT).show();
            PwdConfirm.setError("Please re-enter same password");
            PwdConfirm.requestFocus();
        }else if(userCurrPwd.matches(userPwdNew)){
            Toast.makeText(ChangePassword.this, "New password cannot be same as old password", Toast.LENGTH_SHORT).show();
            PwdNew.setError("Please enter a new password");
            PwdNew.requestFocus();
        }else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChangePassword.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePassword.this, Settings_View.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try{
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(ChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }


}
