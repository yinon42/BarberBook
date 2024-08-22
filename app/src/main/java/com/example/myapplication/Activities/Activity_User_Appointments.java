package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.Helpers.Appointment;
import com.example.myapplication.Helpers.GeneralFun_Helper;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_User_Appointments extends AppCompatActivity {

    private static final String TAG = "Activity_User_Appointments";


    private Button goBackButton;

    private TextView upcomingAppointments;

    private ListView listViewAppointments;

    private ArrayAdapter<String> adapter;

    private List<String> appointmentList;

    private String currentUserId;
    private String clientName;

    private DatabaseReference DB_user_appointmentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_appointments);

        findViews();
        allButtonListeners();

        // Receives the client's name from the previous activity --> Activity_User_Profile
        clientName = getIntent().getStringExtra("clientId");

        // Initializing Firebase DB
        DB_user_appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        // Get the user's unique ID from the Firebase database
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Loading appointments from Firebase
        loadAppointments();
    }

    private void allButtonListeners() {
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void findViews() {
        goBackButton = findViewById(R.id.userApp_BTN_goBack);
        listViewAppointments = findViewById(R.id.userApp_listView_appointments);
        upcomingAppointments = findViewById(R.id.userApp_LBL_upcomingApp);

        // ListView definition
        // Initialize the appointmentList as a new ArrayList to hold appointment data
        appointmentList = new ArrayList<>();

        // Create an ArrayAdapter to connect the appointmentList data with the ListView.
        // The ArrayAdapter uses a built-in layout for each item (simple_list_item_1) and takes the context (this),
        //    the layout ID, and the data list as arguments.
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointmentList);

        // Set the adapter for the ListView so that the ListView displays the data from appointmentList
        listViewAppointments.setAdapter(adapter);
    }

    private void loadAppointments() {
        // Search the database for appointments that match the client's ID
        DB_user_appointmentsRef.orderByChild("clientName").equalTo(clientName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        appointmentList.clear();// Clear existing appointments from the view

                        // Go over all the appointments returned from the query
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            // Convert each snapshot into an Appointment object
                            Appointment appointment = snapshot.getValue(Appointment.class);

                            // If the appointment object is not null, extract its details
                            if (appointment != null) {
                                String appointmentInfo = "Client: " + appointment.getClientName() + "\n " +
                                                appointment.formatDateTime() +"\nBarber: " + appointment.getBarberName();
                                appointmentList.add(appointmentInfo);
                                Log.d(TAG, "Loaded appointment for client: " + appointment.getClientName());
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors.
                        Log.w(TAG, "loadAppointments:onCancelled", databaseError.toException());
                        GeneralFun_Helper.activities_showToast(Activity_User_Appointments.this, "Failed to load appointments.");

                    }
                });
    }
}