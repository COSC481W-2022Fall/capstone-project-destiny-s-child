package com.child.vibe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{
    Context context;
    private List<Chat> chatList;
    public static Button deleteButton;
    boolean oneChat = false;

    //create instance of firebase storage in order to access images on database
    FirebaseStorage storage = FirebaseStorage.getInstance();

    //create firebase user instance to access current user's information
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show;
        public ImageView pp;


        public ViewHolder(View view){
            super(view);
            show = view.findViewById(R.id.chatName1);
//            deleteButton = view.findViewById(R.id.deleteButton);
            pp = view.findViewById(R.id.profilePic);

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
        String senderName = chat.getIds().get(0);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ConversationView.class);
                intent.putExtra("userId", receivername);
                context.startActivity(intent);
            }
        });


//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                oneChat = false;
//                deleteChat(receivername, view);
//                System.out.println("chat list size = " + chatList.size());
//                if(chatList.size() == 1){
//                    Intent intent = new Intent(view.getContext(), ChatLog.class);
//                    context.startActivity(intent);
//                }
//
//
//            }
//        });
        System.out.println(chat.getIds());
        holder.show.setText(receivername);

        //display users profile pictures next to username in chat log
        db.collection("users")
                .whereEqualTo("username", receivername)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String uid = (String) document.get("id");
                                StorageReference photoReference = storageRef.child("images/" + uid + ".jpg");
                                final long ONE_MEGABYTE = 1024 * 1024;
                                photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        holder.pp.setImageBitmap(bmp);
                                    }
                                });
                            }
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

//    public void deleteChat(String username, View view){
//        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("chats");
//        Query ids = collectionReference.whereArrayContains("ids", username);
//        System.out.println(ids.toString());
//        ids.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if(ids != null) {
//                    for (QueryDocumentSnapshot queryDocumentSnapshot : value) {
//                        queryDocumentSnapshot.getReference().delete();
//                    }
//                }
//            }
//        });
//
//
//    }

}
