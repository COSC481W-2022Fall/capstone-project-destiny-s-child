package com.example.vibe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScrren extends AppCompatActivity {

    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_scrren);
        getSupportActionBar().hide();
        logo = (ImageView) findViewById(R.id.image);
        //logo.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in_animation));
        logo.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_animation));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScrren.this, login.class));
                finish();
            }
        }, 2000);



    }
}