package com.example.team_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class MainActivity extends AppCompatActivity {
    Button button;
    TextView textView;
    EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /**
         * setting up the variables to the given id of the object
         */
        button= findViewById(R.id.btn);
        textView= findViewById(R.id.textview);
        name = findViewById(R.id.userName);


        // Animation for the title "VIBE"
//        YoYo.with(Techniques.ZoomIn).duration(3000).repeat(0).playOn(textView);


        /**
         * Button clicker 'Save' Button
         */
        button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //this is where the code for the name will be don
                    textView.setText("Hi "+ name.getText());
                    
                }
            });

        }
    }

