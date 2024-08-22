package com.example.myapplication.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Helpers.Appointment;
import com.example.myapplication.Helpers.GeneralFun_Helper;
import com.example.myapplication.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_Barber_Appointments extends AppCompatActivity {

    private static final String TAG = "Activity_Barber_Appointments";

    private String barberId;
    private String barberName;

    private Button backButton;
    private Button cancelButton;

    private TextView barberNameView;
    private TextView appointmentTimeText;
    private TextView customerNameText;

    private LinearLayout appointmentListLayout;

    private View appointmentView;

    private DatabaseReference DB_barber_appointmentsRef;

    public Activity_Barber_Appointments() {
        // Default constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_appointments);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DB_barber_appointmentsRef = database.getReference("appointments");

        findViews();
        getBarberInformation();
        allButtonListeners();

        loadAppointments();
    }

    public Activity_Barber_Appointments(String barberId, String name) {
        this.barberId = barberId;
        this.barberName = name;
    }

    private void getBarberInformation() {  // Receiving barber's info from Firebase DB
        Intent intent = getIntent(); // Gets the info from the place that appointmentsRef refers to
        barberId = intent.getStringExtra("barberId");
        barberName = intent.getStringExtra("barberName");
        barberNameView.setText(barberName);
    }

    private void allButtonListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void findViews() {
        backButton = findViewById(R.id.barberApp_BTN_backButton);
        barberNameView = findViewById(R.id.barberApp_TXT_name);
        appointmentListLayout = findViewById(R.id.barberApp_appointmentList);
    }

    private void loadAppointments() {
        // Search the database for appointments that match the barber's ID
        DB_barber_appointmentsRef.orderByChild("barberId").equalTo(barberId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                appointmentListLayout.removeAllViews(); // Clear existing appointments from the view

                // Go over all the appointments returned from the query
                for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {

                    // Convert each snapshot into an Appointment object
                    Appointment appointment = appointmentSnapshot.getValue(Appointment.class);

                    // If the appointment object is not null, extract its details
                    if (appointment != null) {
                        String appointmentId = appointmentSnapshot.getKey();
                        String appointmentTime = appointment.formatDateTime();
                        String customerName = appointment.getClientName();

                        Log.d(TAG, "Loaded appointment for barber: " + customerName);

                        // Add the appointment details to the view
                        addAppointmentToView(appointmentId, appointmentTime, customerName);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Log.w(TAG, "loadAppointments:onCancelled", databaseError.toException());
                GeneralFun_Helper.activities_showToast(Activity_Barber_Appointments.this, "Failed to load appointments.");
            }
        });
    }

    private void addAppointmentToView(final String appointmentId, String appointmentTime, String customerName) {
        // Set the relevant layout
        appointmentView = getLayoutInflater().inflate(R.layout.appointment_item, null);

        // Find views for the new layout
        appointmentTimeText = appointmentView.findViewById(R.id.barberAppItem_TXT_appointmentTime);
        customerNameText = appointmentView.findViewById(R.id.barberAppItem_TXT_customerName);
        cancelButton = appointmentView.findViewById(R.id.barberAppItem_BTN_cancelButton);

        // Set the appointment time and customer name in the TextViews
        appointmentTimeText.setText(appointmentTime);
        customerNameText.setText("Client: " + customerName);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAppointment(appointmentId);
            }
        });

        // Add the appointment view to the layout that holds the list of appointments
        appointmentListLayout.addView(appointmentView);
    }

    private void cancelAppointment(String appointmentId) {
        DB_barber_appointmentsRef.child(appointmentId).removeValue();
        loadAppointments(); // Refresh the list after cancellation
    }

    public String getBarberName() {
        return barberName;
    }

    public String getBarberId() {
        return barberId;
    }

    @Override
    public String toString() {
        return "Barber's Name: " + barberName;
    }
}
