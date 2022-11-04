package com.example.vibe;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

/**
 * Search adapter class used to create a search popup dialog that
 * takes search string, accesses database, creates another popup dialog with user info
 */
public class SearchAdapter extends AppCompatActivity {
    FirebaseFirestore db;
    StorageReference sr;
    QueryDocumentSnapshot userDocument;
    Context context;
    View searchDialog, addDialog;

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
        username.setText(userDocument.getId());

//        String imageID = userDocument.get("image").toString();
//        sr = FirebaseStorage.getInstance().getReference("images/" + imageID + ".jpg");

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
