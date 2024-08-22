package com.example.myapplication.Model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Activities.Activity_SignIn;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_Main extends AppCompatActivity {

    private ImageView profileImage;

    private MaterialButton readyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        findViews();
        allButtonListeners();
    }

    private void allButtonListeners() {
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Main.this, Activity_SignIn.class);
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        profileImage = findViewById(R.id.main_IMG_appLogo);
        readyButton = findViewById(R.id.main_BTN_ready);
    }
}