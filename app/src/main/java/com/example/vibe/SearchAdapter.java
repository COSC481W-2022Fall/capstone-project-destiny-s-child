package com.example.vibe;

import static android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Search adapter class used to create a search popup dialog that
 * takes search string, accesses database, creates another popup dialog with user info
 */
public class SearchAdapter extends AppCompatActivity {
    FirebaseFirestore db;
    StorageReference sr = FirebaseStorage.getInstance().getReference();
    QueryDocumentSnapshot userDocument;
    Context context;
    View searchDialog, addDialog;

    DocumentReference documentReference;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText searchField;
    private Button search, cancelSearch, add, cancelAdd;

    public SearchAdapter(Context context, LayoutInflater layoutInflater) {
        db = FirebaseFirestore.getInstance();
        searchDialog = layoutInflater.inflate(R.layout.popup, null);
        addDialog = layoutInflater.inflate(R.layout.popup_add, null);
        this.context = context;
    }

    // shows search dialog, sets on click listeners
    public void beginSearch() {
        createSearchDialog();
        searchOnClicks();
    }

    // shows user dialog with info
    private void beginShow() {
        createAddDialog();
        addOnClicks();
    }

    // develop search dialog
    public void createSearchDialog() {
        dialogBuilder = new AlertDialog.Builder(context);
        searchField = searchDialog.findViewById(R.id.userSearch);

        // dialog buttons
        search = searchDialog.findViewById(R.id.searchAddButton);
        cancelSearch = searchDialog.findViewById(R.id.searchCancelButton);

        dialogBuilder.setView(searchDialog);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    // manage search dialog on click listeners
    private void searchOnClicks() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userSearch = searchField.getText().toString();
                search(userSearch);

                dialog.dismiss();
            }
        });
        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    // develop user dialog
    private void createAddDialog() {
        dialogBuilder.setView(addDialog);
        dialog = dialogBuilder.create();

        // dialog user info TODO: user profile pic
        TextView username = addDialog.findViewById(R.id.resultUser);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            username.setAutoSizeTextTypeWithDefaults(AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
        username.setText(userDocument.getId());
        String imageID = null;
        try {
            imageID = userDocument.get("image").toString();
        }catch(NullPointerException npe){
            System.out.println("ImageId is null, no image was uploaded");
        }
        System.out.println("this is the image url: " + imageID);
        ImageView profilePicture = addDialog.findViewById(R.id.searchProPic);
        profilePicture.getBackground().setAlpha(255);
        if(imageID != null) {
            profilePicture.getBackground().setAlpha(0);
            Glide.with(addDialog).load(imageID).into(profilePicture);
        }


        // dialog buttons
        add = addDialog.findViewById(R.id.addAddButton);
        cancelAdd = addDialog.findViewById(R.id.addCancelButton);

        dialog.show();
    }

    // manage user dialog on click listeners
    private void addOnClicks() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //create a chats collection in the database to store chats
                FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                String newChatId = CurrentUser.getUid() + userDocument.getId();
                documentReference = db.collection("chats").document();
                Map<String, Object> chat = new HashMap<>();
                //this is the unique ID created for the chat between these 2 users
                chat.put("id", newChatId);
                //adding the user ID's into the database as an arrayList
                ArrayList<String> ids = new ArrayList<>();
                ids.add(0, CurrentUser.getUid());
                ids.add(1, userDocument.getId());
                chat.put("ids", ids);


                //adds user input into Firestore database
                documentReference.set(chat);


                Intent intent = new Intent(context, ConversationView.class);
                intent.putExtra("userId", userDocument.getId());
                context.startActivity(intent);
            }
        });

        cancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    // search database for user
    private void search(String userSearch) {
        db.collection("users")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            if (userSearch.equals(document.getId())) {
                                userDocument = document;
                                dialog.dismiss();
                                beginShow();
                                break;
                            }
                        }
                        if (userDocument == null) {
                            handleUserNotFound();
                        }
                    }
                    else{
                        dialog.dismiss();
                        Log.d("", task.getException().toString());
                    }
                }
            });
    }

    // user not found toast
    private void handleUserNotFound() {
        dialog.dismiss();
        Toast.makeText(context, "User not found" ,Toast.LENGTH_SHORT).show();
    }
}