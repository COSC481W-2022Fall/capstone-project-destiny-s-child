package com.example.vibe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatLog extends AppCompatActivity {
    FloatingActionButton addButton;
    ImageView settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_log);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        //do not display title of app
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // settings button with intent
        settingsButton = (ImageView)findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatLog.this, Settings_View.class);
                startActivity(intent);
            }
        });

        // add button with intent
        addButton = (FloatingActionButton) findViewById(R.id.searchAddButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchAdapter search = new SearchAdapter(ChatLog.this, getLayoutInflater());
                search.beginSearch();
            }
        });
    }
}