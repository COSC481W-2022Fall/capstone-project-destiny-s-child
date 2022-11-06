package com.example.vibe;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class MessagesProvider {

    CollectionReference mCollection;

    public MessagesProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("messages");
    }

    public Task<Void> create(Message message) {
        return mCollection.document().set(message);
    }

    public Query getUserChats(String idUser) {
        return mCollection.whereArrayContains("ids", idUser);
    }


    public Query getChatsByUser1AndUser2(String idUser1, String idUser2) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUser1 + idUser2);
        ids.add(idUser2 + idUser1);
        return mCollection.whereIn("id", ids);
    }
}
