package com.example.vibe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;


public class Settings_View extends AppCompatActivity {

    ImageView img;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skeleton_view);
        getSupportActionBar().hide();

        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img = (ImageView) findViewById(R.id.imageView);
        //This is something that will need to be changed later
        img.setImageResource(R.drawable.ic_launcher_background);

        //setting initialized button logout button
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


}