package com.child.vibe.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.child.vibe.R;

import java.util.List;

public class MessagesAdapter  extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private final List<User> messagesList;
    private final Context context;

    public MessagesAdapter(List<User> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
    }

    //Adapter class to hold the name of the friend in the chat log, upon clicking will display the conversations view
    @NonNull
    @Override
    public MessagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_adapter, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    static class MyViewHolder extends  RecyclerView.ViewHolder {

        private ImageView profilePic;
        private TextView name;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.chatName1);

        }
    }

}
