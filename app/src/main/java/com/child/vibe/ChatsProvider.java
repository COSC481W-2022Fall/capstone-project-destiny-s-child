package com.child.vibe;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ChatsProvider {

    CollectionReference mCollection;

    public ChatsProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("chats");
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
