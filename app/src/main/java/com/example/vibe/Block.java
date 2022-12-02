package com.example.vibe;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Block {
    //Two different ways the blocklist can be implemented:

    //1st : if you want to create a separate collection for the block list
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colRef = db.collection("BlockList");

    //2nd : if you want to add the blocked names inside of that user’s collection without creating a whole new collection
//    documentReference = db.collection("users").document(); //put username of logged in user in the 'document()”
//
//    Map<String, Object> block = new HashMap<>();
//    //adding the blacked users into the database as an arrayList
//    ArrayList<String> BlockList = new ArrayList<>();
//    //adding in the person im texting into the database, with index 0
//        BlockList.add(0, userId);
//    //adding to blocklist collection
//        block.put("blocked", BlockList);
}
