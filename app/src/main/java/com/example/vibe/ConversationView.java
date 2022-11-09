package com.example.vibe;

import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationView extends AppCompatActivity {
    // TODO: replace with user object
    String userId;
    TextView username;
    ImageButton send;
    EditText editMessage;

    MessagesProvider mMessageProvider;
    FirebaseUser CurrentUser;
    FirebaseFirestore db;
    DocumentReference documentReference;
    MessageAdapter messageAdapter;
    List<Message> mList;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_view);

        username = findViewById(R.id.convoUsername);
        send = findViewById(R.id.send);
        editMessage = findViewById(R.id.edit_message);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

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
        if (getSupportActionBar() != null) {
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

        CollectionReference collectionReference = db.collection("messages");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                displayMessage(CurrentUser.getUid(), userId);
            }
        });
    }

    private void createMessage() {
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String newID = CurrentUser.getUid() + userId;
        String textMessage = editMessage.getText().toString();
        if (!textMessage.equals("")) {
            Message message = new Message(CurrentUser.getUid(), userId, newID, textMessage);
            db.collection("messages").add(message).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(ConversationView.this, "Message Sent", Toast.LENGTH_SHORT).show();
                    editMessage.setText("");
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

    public void displayMessage(String id, String userId) {
        mList = new ArrayList<>();
        CollectionReference collectionReference = db.collection("messages");
        collectionReference
                .whereEqualTo("idSender", id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        mList.clear();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : value){
                            Message message = queryDocumentSnapshot.toObject(Message.class);
                            if(message.getIdReceiver().equals(userId)){
                                mList.add(message);
                            }
                            MessageAdapter messageAdapter = new MessageAdapter(mList, ConversationView.this);
                            recyclerView.setAdapter(messageAdapter);

                        }
                    }
                });
    }

    protected void onResume(){
        super.onResume();
        username = findViewById(R.id.convoUsername);
        send = findViewById(R.id.send);
        editMessage = findViewById(R.id.edit_message);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

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
        if (getSupportActionBar() != null) {
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

        CollectionReference collectionReference = db.collection("messages");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                displayMessage(CurrentUser.getUid(), userId);
            }
        });
    }
}

