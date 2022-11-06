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

import com.google.android.gms.tasks.OnSuccessListener;
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

    MessagesProvider mMessageProvider;

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

        mMessageProvider = new MessagesProvider();

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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMessage();
            }
        });


    }

    private void createMessage() {
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String newID = CurrentUser.getUid() + userId;
        String textMessage = editMessage.getText().toString();
        if(!textMessage.equals("")){
            Message message = new Message();
            message.setIdChat(newID);
            message.setIdSender(CurrentUser.getUid());
            message.setIdReceiver(userId);
            message.setMessage(textMessage);

            mMessageProvider.create(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    editMessage.setText("");
                    Toast.makeText(ConversationView.this, "message sent", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(ConversationView.this, "cannot send empty messages", Toast.LENGTH_SHORT).show();
        }

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

}

