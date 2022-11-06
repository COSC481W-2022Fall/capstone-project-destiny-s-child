package com.example.vibe;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ConversationView extends AppCompatActivity {
    // TODO: replace with user object
    String userId;
    TextView username;
    ImageButton send;
    EditText editMessage;

    FirebaseFirestore db;
    DocumentReference documentReference;
    //FirebaseAuth CurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_view);

        username = findViewById(R.id.convoUsername);
        send = findViewById(R.id.send);
        editMessage = findViewById(R.id.edit_message);

        //instantiating Firestore Database
        db = FirebaseFirestore.getInstance();

        //getting the current user
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //do not show title of app
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        username.setText(userId);

        //send button handling
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editMessage.getText().toString();
                if (!msg.equals("")){
                    sendMessage(CurrentUser.getUid(), userId, msg);
                } else {
                    Toast.makeText(ConversationView.this, "You can't send empty messages" ,Toast.LENGTH_SHORT).show();
                }
                //clears the typed out message in the EditText after hitting send
                editMessage.setText("");
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ConversationView.this, ChatLog.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    //This method is adding the newly created chats into the database to store the messages
    private void sendMessage(String sender, String receiver, String message){
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String newChatId = CurrentUser.getUid() + userId;
        documentReference = db.collection("chats").document(newChatId);
        Map<String, Object> chat = new HashMap<>();
        chat.put("sender", sender);
        chat.put("receiver", receiver);
        chat.put("message", message);

        //adds user input into Firestore database
        documentReference.set(chat);
    }

}

