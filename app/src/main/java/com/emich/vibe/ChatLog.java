package com.emich.vibe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.emich.vibe.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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

public class ChatLog extends AppCompatActivity {
    FloatingActionButton addButton;
    ImageView settingsButton;
    RecyclerView chatRecyclerView;
    ChatsProvider chatsProvider;
    FirebaseUser CurrentUser;
    String userId;
    FirebaseFirestore db;
    List<Chat> chatList;

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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // currentUserName must be final before being passed to inner classes
        String temp = "";
        if(currentUser != null)
            temp = currentUser.getUid();
        final String currentUserName = temp;

        //RecyclerView
        chatRecyclerView = findViewById(R.id.messagesRecyclerView);

        //copying from video
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ChatsProvider chatsProvider = new ChatsProvider();
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("chats");

        // Slide to delete functionality
        ItemTouchHelper.SimpleCallback ith = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String receiverId = chatList.get(viewHolder.getAdapterPosition()).getIds().get(1);
                deleteChat(receiverId);
            }
        };
        new ItemTouchHelper(ith).attachToRecyclerView(chatRecyclerView);

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                displayChats(currentUserName);
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

    public void displayChats(String currentUserName){
        chatList = new ArrayList<>();
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("chats");
        collectionReference.whereArrayContains("ids", currentUserName).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    return;
                }
                if(!chatList.isEmpty())
                    chatList.clear();
                for(QueryDocumentSnapshot queryDocumentSnapshot : value){
                    Chat chat = queryDocumentSnapshot.toObject(Chat.class);
                    chatList.add(chat);
                    ChatsAdapter chatsAdapter = new ChatsAdapter(chatList, ChatLog.this);
                    chatRecyclerView.setAdapter(chatsAdapter);

                }
            }
        });
    }

    public void deleteChat(String username){
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("chats");
        Query ids = collectionReference.whereArrayContains("ids", username);
        ids.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                    return;
                if(ids != null) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : value) {
                        queryDocumentSnapshot.getReference().delete();
                    }
                }
            }
        });
    }
}