package com.example.vibe;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.UUID;


public class Settings_View extends AppCompatActivity {

    ImageView profilePic;
    Button logout;

    //create instance of firebase storage in order to access images on database
    FirebaseStorage storage = FirebaseStorage.getInstance();

    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_view);
//        getSupportActionBar().hide();

        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable color = new ColorDrawable(Color.parseColor("#6D37AE"));
        this.getSupportActionBar().setBackgroundDrawable(color);

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        //code from firebase storage documentation to upload photo to storage
                        final String randomKey = UUID.randomUUID().toString();
                        StorageReference riversRef = storageRef.child("images/" + randomKey);

                        UploadTask uploadTask = riversRef.putFile(uri);
                        uploadTask.pause();

                        AlertDialog alertDialog = new AlertDialog.Builder(Settings_View.this).create();
                        alertDialog.setTitle("Upload profile picture");
                        alertDialog.setMessage("Do you wish to continue?");
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        uploadTask.cancel();
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        uploadTask.resume();
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.show();
                                // Register observers to listen for when the download is done or if it fails
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        //progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Failed to upload. Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //progressDialog.dismiss();
                                        profilePic.setImageURI(uri);
                                        Snackbar.make(findViewById(android.R.id.content), "Image uploaded successfully!", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                    }
                });

        profilePic = (ImageView) findViewById(R.id.profilePicture);
        //This is something that will need to be changed later
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });



        //initializing logout button
        logout = findViewById(R.id.logout);

        //logout button and redirection to login page
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();//signs you out
                startActivity(new Intent(getApplicationContext(),login.class));
                finish();
            }
        });
    }

}