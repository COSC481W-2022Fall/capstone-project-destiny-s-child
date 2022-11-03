package com.example.vibe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        addButton = (FloatingActionButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search search = new Search();
                search.createDialog();
//                Intent intent = new Intent(ChatLog.this, FriendSearch.class);
//                startActivity(intent);
            }
        });
    }

    private class Search {
        private AlertDialog.Builder dialogBuilder;
        private AlertDialog dialog;
        private EditText resultUsername;
        private Button add, cancel;

        public void createDialog() {
            dialogBuilder = new AlertDialog.Builder(ChatLog.this);
            final View popUpView = getLayoutInflater().inflate(R.layout.popup, null);
            resultUsername = popUpView.findViewById(R.id.userSearch);

            add = popUpView.findViewById(R.id.addButton);
            cancel = popUpView.findViewById(R.id.cancelButton);

            dialogBuilder.setView(popUpView);
            dialog = dialogBuilder.create();
            dialog.show();

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO
                }
            });
        }

        private void addFriend() {

        }
    }
}