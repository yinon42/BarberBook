package com.example.myapplication.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.myapplication.Activities.Activity_Barber_Appointments;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Rating_Helper {

    private static final String TAG = "Rating_Helper";

    private String userName;
    private String selectedBarberId;
    private String selectedBarberName;

    private Context context;

    private List<Activity_Barber_Appointments> barberList;

    private DatabaseReference DB_usersRef;

    public Rating_Helper(Context context, String userName) {
        this.context = context;
        this.userName = userName;
        this.DB_usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        this.barberList = new ArrayList<>();
    }

    public void loadBarbersForRating() {
        DB_usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                barberList.clear(); // Clear any existing barbers in the list

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userType = snapshot.child("userType").getValue(String.class);

                    if ("barber".equals(userType)) { // Only add users who are barbers
                        String barberId = snapshot.getKey();
                        String name = snapshot.child("name").getValue(String.class);
                        if (barberId != null && name != null) {
                            // Add barber to list
                            barberList.add(new Activity_Barber_Appointments(barberId, name));
                        }
                    }
                }
                showBarberSelectionDialogForRating(); // Show dialog to select a barber for rating
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log errors
                Log.w(TAG, "loadBarbers:onCancelled", databaseError.toException());
            }
        });
    }

    private void showBarberSelectionDialogForRating() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_select_barber); // Use the select barber layout

        ListView listViewBarbers = dialog.findViewById(R.id.selectBarber_listView_barbers);

        // Adapter to display the barbers in the ListView
        ArrayAdapter<Activity_Barber_Appointments> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1, barberList);
        listViewBarbers.setAdapter(adapter);

        // Handle selection of a barber
        listViewBarbers.setOnItemClickListener((parent, view, position, id) -> {
            Activity_Barber_Appointments selectedBarber = barberList.get(position);
            selectedBarberId = selectedBarber.getBarberId(); // Save the selected barber's ID
            selectedBarberName = selectedBarber.getBarberName(); // Save the selected barber's name
            Toast.makeText(context, "Selected: " + selectedBarber.getBarberName(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            showRatingDialog(); // Show the rating dialog for the selected barber
        });

        dialog.show();
    }

    private void showRatingDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_rate_barber);

        RatingBar ratingBar = dialog.findViewById(R.id.rating_bar); // Reference to the rating bar
        Button submitRatingButton = dialog.findViewById(R.id.rating_BTN_submit); // Reference to the submit button

        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = ratingBar.getRating(); // Get the selected rating
                Log.d(TAG, "Rating: " + rating);
                saveRating(rating); // Save the rating to the database
                dialog.dismiss(); // Close the dialog
            }
        });

        Button closeButton = dialog.findViewById(R.id.rating_BTN_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveRating(float rating) {
        Log.d(TAG, "Saving rating for barber: " + selectedBarberName);
        DB_usersRef.child(selectedBarberId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "Barber exists: " + selectedBarberName);
                    String userType = snapshot.child("userType").getValue(String.class);
                    if ("barber".equals(userType)) {
                        int numOfRatings = snapshot.child("numOfRatings")
                                .getValue(Integer.class) != null ? snapshot.child("numOfRatings")
                                .getValue(Integer.class) : 0;
                        float currentRating = snapshot.child("rating")
                                .getValue(Float.class) != null ? snapshot.child("rating")
                                .getValue(Float.class) : 0.0f;

                        // Calculate the new rating
                        float newRating = ((currentRating * numOfRatings) + rating) / (numOfRatings + 1);

                        // Update the rating and number of ratings in the database
                        snapshot.getRef().child("rating").setValue(newRating);
                        snapshot.getRef().child("numOfRatings").setValue(numOfRatings + 1);

                        Toast.makeText(context, "Rating saved successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Cannot rate non-barber user.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Barber not found: " + selectedBarberName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "saveRating:onCancelled", error.toException());
            }
        });
    }
}
