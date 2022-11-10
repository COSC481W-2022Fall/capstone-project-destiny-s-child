package com.emich.vibe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.emich.vibe.R;

public class SplashScreen extends AppCompatActivity {

    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        logo = (ImageView) findViewById(R.id.image);
        //logo.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in_animation));
        logo.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_animation));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, Login.class));
                finish();
            }
        }, 2000);



    }
}