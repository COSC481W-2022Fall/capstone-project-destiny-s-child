package com.emich.vibe;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emich.vibe.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class Settings_View extends AppCompatActivity {

    ImageView profilePic;
    Button logout, move;
    QueryDocumentSnapshot userDocument;
    TextView username;
    String usernameFromCollection;

    //create instance of firebase storage in order to access images on database
    FirebaseStorage storage = FirebaseStorage.getInstance();

    //create firebase user instance to access current user's information
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_view);

        System.out.println("storage ref" + storageRef);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getSupportActionBar().setDisplayShowHomeEnabled(false);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable color = new ColorDrawable(Color.parseColor("#6D37AE"));
        this.getSupportActionBar().setBackgroundDrawable(color);

        //get user's current profile picture and display
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                userDocument = documentSnapshot;
                                getProfilePic();
                            }
                        }
                    }
                });

        //open photos on android device
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        //code from firebase storage documentation to upload photo to storage
                        //storage reference to save photo under uid.jpg in firebase storage
                        StorageReference ref = storageRef.child("images/" + user.getUid() + ".jpg");

                        //upload photo to storage
                        UploadTask uploadTask = ref.putFile(uri);
                        uploadTask.pause();

                        //dialog box to allow user to cancel upload
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
                                Toast.makeText(getApplicationContext(), "Failed to upload. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //progressDialog.dismiss();
                                profilePic.setImageURI(uri);
                                //new profile change request to update image uri in storage
                                String uid = user.getUid();
                                db.collection("users")
                                        .document(uid)
                                        .update("image", "images/" + user.getUid() + ".jpg");
                            }
                        });
                    }
                });

        profilePic = (ImageView) findViewById(R.id.profilePicture);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        username = findViewById(R.id.displayUsername);
        db.collection("users")
                        .whereEqualTo("id", user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for(DocumentSnapshot document : task.getResult()) {
                                        username.setText((CharSequence) document.get("username"));
                                    }
                                }
                            }
                        });

        //initializing logout button
        logout = findViewById(R.id.logout);

        //logout button and redirection to login page
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();//signs you out
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        move = findViewById(R.id.change_pass);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings_View.this,ChangePassword.class);
                startActivity(intent);
            }
        });
    }

    //This method is what makes the back button work
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getProfilePic() {

        StorageReference photoReference = storageRef.child("images/" + user.getUid() + ".jpg");
        final long ONE_MEGABYTE = 1024*1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePic.setImageBitmap(bmp);
            }
        });
    }
}