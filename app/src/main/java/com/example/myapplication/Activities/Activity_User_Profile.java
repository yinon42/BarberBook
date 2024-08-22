package com.example.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.Helpers.Appointment_Helper;
import com.example.myapplication.Helpers.GeneralFun_Helper;
import com.example.myapplication.Helpers.Rating_Helper;
import com.example.myapplication.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_User_Profile extends AppCompatActivity {

    private static final String TAG = "Activity_User_Profile";

    private String currentUserId;
    private String userName;
    private String userEmail;
    private String userPhone;

    private Button catalog;
    private Button rateBarberButton;
    private Button bookNewAppointmentButton;
    private Button existingAppointmentsButton;
    private Button signOutButton;

    private TextView userProfile_dateText;
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userPhoneTextView;

    private DatabaseReference DB_user_appointmentsRef;

    private Appointment_Helper appointmentHelper;

    private Rating_Helper ratingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initializing Firebase DB
        DB_user_appointmentsRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Get the user's unique ID from the Firebase database
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        findViews();
        setUserInformation();
        allButtonListeners();
    }

    private void setUserInformation() {

        // Attach a listener to the user's data in Firebase using their unique ID
        DB_user_appointmentsRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Check if the user's data exists in the database
                if (snapshot.exists()) {

                    // Retrieve the user's details from the database
                    userName = snapshot.child("name").getValue(String.class);
                    userEmail = snapshot.child("email").getValue(String.class);
                    userPhone = snapshot.child("phoneNumber").getValue(String.class);

                    // Set the retrieved values to the corresponding TextViews in the UI
                    userNameTextView.setText(userName != null ? userName : "");
                    userEmailTextView.setText(userEmail != null ? userEmail : "");
                    userPhoneTextView.setText(userPhone != null ? userPhone : "");

                    // Send the user's details to the helpers objects for future usage
                    appointmentHelper = new Appointment_Helper(Activity_User_Profile.this, userName, message -> userProfile_dateText.setText(message));
                    ratingHelper = new Rating_Helper(Activity_User_Profile.this, userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
                Log.w(TAG, "Failed to read user data.", error.toException());
                GeneralFun_Helper.activities_showToast(Activity_User_Profile.this, "Failed to load barber's details.");

            }
        });
    }

    private void allButtonListeners() {
        catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_User_Profile.this, Activity_Catalog.class);
                startActivity(intent);
            }
        });

        bookNewAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointmentHelper.loadBarbers();
            }
        });

        existingAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_User_Profile.this, Activity_User_Appointments.class);
                intent.putExtra("clientId", userName);
                startActivity(intent);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFun_Helper.signOut(Activity_User_Profile.this);

            }
        });

        rateBarberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingHelper.loadBarbersForRating();
            }
        });
    }

    private void findViews() {
        catalog = findViewById(R.id.userProfile_BTN_catalog);
        rateBarberButton = findViewById(R.id.userProfile_BTN_rateBarber);
        userProfile_dateText = findViewById(R.id.userProfile_TXT_date_text);
        userNameTextView = findViewById(R.id.userProfile_TXT_name);
        userEmailTextView = findViewById(R.id.userProfile_TXT_email);
        userPhoneTextView = findViewById(R.id.userProfile_TXT_phone);
        bookNewAppointmentButton = findViewById(R.id.userProfile_BTM_bookNewAppointment);
        existingAppointmentsButton = findViewById(R.id.userProfile_BTN_existingAppointments);
        signOutButton = findViewById(R.id.userProfile_BTN_signOut);
    }
}
