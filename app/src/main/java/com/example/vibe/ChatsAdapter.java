package com.example.vibe;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SymbolTable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vibe.messages.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{
    Context context;
    private List<Chat> chatList;
    public static Button deleteButton;
    boolean oneChat = false;

//    FirebaseUser fUser;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show;


        public ViewHolder(View view){
            super(view);
            show = view.findViewById(R.id.chatName1);
            deleteButton = view.findViewById(R.id.deleteButton);

        }

    }


    public ChatsAdapter(List<Chat> chats, Context context){
        chatList = chats;
        this.context = context;
    }
    @NonNull
    @Override
    public ChatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_adapter, parent, false);
            return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        String receivername = chat.getIds().get(1);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ConversationView.class);
                intent.putExtra("userId", receivername);
                context.startActivity(intent);
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneChat = false;
                deleteChat(receivername, view);
                System.out.println("chat list size = " + chatList.size());
                if(chatList.size() == 1){
                    Intent intent = new Intent(view.getContext(), ChatLog.class);
                    context.startActivity(intent);
                }


            }
        });

        holder.show.setText(receivername);

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void deleteChat(String username, View view){
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("chats");
        Query ids = collectionReference.whereArrayContains("ids", username);
        System.out.println(ids.toString());
        ids.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(ids != null) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : value) {
                        queryDocumentSnapshot.getReference().delete();
                    }
                }
            }
        });


    }


}
