package com.example.myapplication.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Activity_Catalog extends AppCompatActivity{

    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hairstyles);

        findViews();
        allButtonListeners();
    }

    private void allButtonListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity and return to the previous activity
            }
        });
    }

    private void findViews() {
        backButton = findViewById(R.id.catalog_BTN_backButton);
    }
}
