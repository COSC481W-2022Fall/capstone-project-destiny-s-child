package com.example.vibe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.*;

public class ConversationView extends AppCompatActivity {
    // TODO: replace with user object
    String userId;
    public static String myUsername;
    TextView username;
    ImageButton send;
    EditText editMessage;
    long millis;

    Users reciever;
    ArrayList<String> blockList = new ArrayList<>();
    List<Users> usersList;
    List<Users> current;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersReference = db.collection("users");
    CollectionReference collectionReference = db.collection("messages");
    MessagesProvider mMessageProvider;
    FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
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

        mMessageProvider = new MessagesProvider();

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


        // User Info -- tapping username displays option to block
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View userInfoDialog = getLayoutInflater().inflate(R.layout.popup_user_info, null);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ConversationView.this);
                Button block = userInfoDialog.findViewById(R.id.blockButton);
                Button close = userInfoDialog.findViewById(R.id.userInfoCloseButton);
                TextView username = userInfoDialog.findViewById(R.id.userInfoName);

                dialogBuilder.setView(userInfoDialog);
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                dialog.getWindow().setLayout(900, 600);

                username.setText(userId);
                block.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ConversationView.this, "BLOCKED", Toast.LENGTH_SHORT).show();
                        Map<String, Object> blockedUser = new HashMap<>();
                        blockedUser.put("name", userId);
                        db.collection("users").document(Login.user.getUsername()).collection("blocklist").document(userId).set(blockedUser);
                        dialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), ChatLog.class));
                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMessage();
            }
        });

        usersList = new ArrayList<>();
        usersReference
                .whereEqualTo("username", userId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot document : task.getResult()){
                    usersList.add(document.toObject(Users.class));
                }
                reciever = usersList.get(0);
            }
        });


        // display method only if contact isn't in user's blocklist
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                db.collection("users").document(Login.user.getUsername()).collection("blocklist")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        blockList.add(document.get("name").toString());
                                    }
                                    if (!blockList.contains(userId)) {
                                        displayMessage(myUsername, userId);
                                    }
                                }
                            }
                        });
            }
        });
    }

    private void createMessage() {
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String newID = CurrentUser.getUid() + userId;
        String textMessage = editMessage.getText().toString();
        if (!textMessage.equals("")) {
            millis = System.currentTimeMillis();
            Message message = new Message(myUsername, userId, newID, textMessage, millis);
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
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getReciver(String username){
        List<Users> usersList = new ArrayList<>();
        db.collection("users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()){
                            usersList.add(document.toObject(Users.class));
                        }
                    }
                });

        reciever = usersList.get(0);
    }


    public void displayMessage(String id, String userId) {
        mList = new ArrayList<>();

        current = new ArrayList<>();
        usersReference.whereEqualTo("id", CurrentUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()){
                            current.add(document.toObject(Users.class));
                        }
                        myUsername = current.get(0).getUsername();
                    }
                });

        CollectionReference collectionReference = db.collection("messages");
        //Query query = collectionReference.orderBy("timePosted", Query.Direction.ASCENDING);
        collectionReference
                .orderBy("timePosted", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            return;
                        }
                        mList.clear();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : value){
                            Message message = queryDocumentSnapshot.toObject(Message.class);
                            if(message.getIdReceiver() != null || message.getIdSender() != null) {
                                if ((message.getIdReceiver().equals(userId) && message.getIdSender().equals(myUsername))
                                        || (message.getIdReceiver().equals(myUsername) && message.getIdSender().equals(userId))) {
                                    mList.add(message);

                                }
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

        // display method only if contact isn't in user's blocklist
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                db.collection("users").document(Login.user.getUsername()).collection("blocklist")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(QueryDocumentSnapshot document : task.getResult()) {
                                    blockList.add(document.get("name").toString());
                                }
                                if(!blockList.contains(userId)) {
                                    displayMessage(myUsername, userId);
                                }
                            }
                        });
            }
        });
    }
}

