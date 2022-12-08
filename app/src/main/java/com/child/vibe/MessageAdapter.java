package com.child.vibe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.child.vibe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    Context context;
    private List<Message> mChat;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    FirebaseUser fUser;
    //String myUsername;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show, date;

        public ViewHolder(View view){
            super(view);
            show = view.findViewById(R.id.textViewMessage);
            date = view.findViewById(R.id.date);
        }

    }

    public MessageAdapter(List<Message> data, Context context){
        mChat = data;
        this.context = context;
    }
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message = mChat.get(position);
        holder.show.setText(message.getMessage());
        holder.date.setText(message.getDate(message.getTimePosted()));
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position){
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        List<Users> usersList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(mChat.get(position).getIdSender().equals(ConversationView.myUsername)){
            return RIGHT;
        }
        else{
            return LEFT;
        }
    }
}
