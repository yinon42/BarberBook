package com.example.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.Helpers.GeneralFun_Helper;
import com.example.myapplication.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_Barber_Profile extends AppCompatActivity {

    private static final String TAG = "Activity_Barber_Profile";

    private String currentUserId;
    private String barberName;
    private String barberEmail;
    private String barberPhone;
    private String barberRating;

    private Button catalog;
    private Button existingAppointmentsButton;
    private Button signOutButton;

    private TextView barberNameTextView;
    private TextView barberEmailTextView;
    private TextView barberPhoneTextView;
    private TextView barberRatingTextView;

    private DatabaseReference DB_usersRef;

    private Float ratingValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_profile);

        DB_usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Get the user's unique ID from the Firebase database
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        findViews();
        setBarberInformation();
        allButtonListeners();
    }

    private void setBarberInformation() {

        // Attach a listener to the barber's data in Firebase using their unique ID
        DB_usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Check if the user's data exists in the database
                if (snapshot.exists()) {

                    // Retrieve the barber's details from the database
                    barberName = snapshot.child("name").getValue(String.class);
                    barberEmail = snapshot.child("email").getValue(String.class);
                    barberPhone = snapshot.child("phoneNumber").getValue(String.class);
                    ratingValue = snapshot.child("rating").getValue(Float.class);

                    // If the rating is not null, convert it to a string, otherwise set it to "No Rating"
                    barberRating = ratingValue != null ? String.valueOf(ratingValue) : "No Rating";

                    // Set the retrieved values to the corresponding TextViews in the UI
                    barberNameTextView.setText(barberName != null ? barberName : "");
                    barberEmailTextView.setText(barberEmail != null ? barberEmail : "");
                    barberPhoneTextView.setText(barberPhone != null ? barberPhone : "");
                    barberRatingTextView.setText(barberRating != null ? barberRating : "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
                Log.w(TAG, "Failed to read user data.", error.toException());
                GeneralFun_Helper.activities_showToast(Activity_Barber_Profile.this, "Failed to load barber's details.");
            }
        });
    }

    private void allButtonListeners() {
        catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Barber_Profile.this, Activity_Catalog.class);
                startActivity(intent);
            }
        });

        existingAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Barber_Profile.this, Activity_Barber_Appointments.class);
                intent.putExtra("barberId", currentUserId); // Sends the barber's ID to the next activity
                intent.putExtra("barberName", barberName); // Sends the barber's name to the next activity
                startActivity(intent);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFun_Helper.signOut(Activity_Barber_Profile.this);
            }
        });
    }

    private void findViews() {
        catalog = findViewById(R.id.barberProfile_BTN_catalog);
        barberNameTextView = findViewById(R.id.barberProfile_TXT_name);
        barberEmailTextView = findViewById(R.id.barberProfile_TXT_email);
        barberPhoneTextView = findViewById(R.id.barberProfile_TXT_phone);
        barberRatingTextView = findViewById(R.id.barberProfile_TXT_rating);
        existingAppointmentsButton = findViewById(R.id.barberProfile_BTM_bookNewAppointment);
        signOutButton = findViewById(R.id.barberProfile_BTN_signOut);
    }
}
