package com.example.vibe;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vibe.R;

public class FriendSearch extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsearch);
        getSupportActionBar().hide();
    }
}
