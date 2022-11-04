package com.example.vibe;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Search utility class -- couldn't be static -- used to create a search popup dialog that
 */
public class SearchUtil extends AppCompatActivity {
    FirebaseFirestore db;
    QueryDocumentSnapshot userDocument;
    Context context;
    View searchDialog, addDialog;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText searchField;
    private Button search, cancelSearch, cancelAdd, add;

    public SearchUtil(Context context, LayoutInflater layoutInflater) {
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

        search = searchDialog.findViewById(R.id.addButton);
        cancelSearch = searchDialog.findViewById(R.id.cancelButton);

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
        dialogBuilder.setView(R.layout.popup_add);
        dialog = dialogBuilder.create();

        TextView username = addDialog.findViewById(R.id.resultUser);
        username.setText(userDocument.getId());

        add = addDialog.findViewById(R.id.addButton);
        cancelAdd = addDialog.findViewById(R.id.cancelButton);

        dialog.show();
    }

    // manage user dialog on click listeners
    private void addOnClicks() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO open conversation
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
                                    System.out.println("---KINDA FOUND---");
                                    userDocument = document;
                                    dialog.dismiss();
                                    beginShow();
                                    break;
                                }
                            }
                        }
                        else{
                            Log.d("", task.getException().toString());
                        }
                    }
                });
    }
}
