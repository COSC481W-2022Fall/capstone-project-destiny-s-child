package com.example.vibe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vibe.messages.User;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{
    Context context;
    private List<Chat> chatList;

//    FirebaseUser fUser;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show;

        public ViewHolder(View view){
            super(view);
            show = view.findViewById(R.id.chatName1);
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
    public void onBindViewHolder(@NonNull ChatsAdapter.ViewHolder holder, int position) {
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

        holder.show.setText(receivername);

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

}
