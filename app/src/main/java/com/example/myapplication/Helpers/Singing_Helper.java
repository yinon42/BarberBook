package com.example.myapplication.Helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.myapplication.Activities.Activity_Barber_Profile;
import com.example.myapplication.Activities.Activity_User_Profile;
import com.example.myapplication.Activities.Activity_SignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Singing_Helper {

    private static final String TAG = "Singing_Helper";

    private FirebaseAuth mAuth;

    private DatabaseReference DB_usersRef;

    public Singing_Helper() {

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize reference to "users" node
        DB_usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public void signInUser(Context context, String email, String password) {

        // Check if email or password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Email and Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in the user with email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity_SignIn) context, task -> {

                    if (task.isSuccessful()) {
                        // Get the currently signed-in user
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            // Check the user's type and redirect accordingly
                            checkUserType(context, user.getUid());
                        }
                    } else {
                        Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Sign-in failed: ", task.getException());
                    }
                });
    }

    public void checkUserType(Context context, String userId) {
        // Access the user's data from Firebase Database
        DB_usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userType = dataSnapshot.child("userType").getValue(String.class);

                    if ("barber".equals(userType)) {
                        // Redirect to barber profile if the user is a barber
                        Intent intent = new Intent(context, Activity_Barber_Profile.class);
                        context.startActivity(intent);

                    } else if ("client".equals(userType)) {
                        // Redirect to client profile if the user is a client
                        Intent intent = new Intent(context, Activity_User_Profile.class);
                        context.startActivity(intent);
                    } else {
                        // Display error message if the user type is unknown
                        Toast.makeText(context, "User type is unknown.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Display error message if the user is not found in the database
                    Toast.makeText(context, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur when accessing the database
                Toast.makeText(context, "Database error.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error: ", databaseError.toException());
            }
        });
    }

    public void saveUserData(Context context, FirebaseUser user, String name, String email,
                                            String phoneNumber, String password, String userType) {

        String userId = user.getUid(); // Get the unique ID of the user
        DatabaseReference userRef = DB_usersRef.child(userId); // Create a reference to the user's data in Firebase

        // Save the user's data to Firebase
        userRef.child("email").setValue(email);
        userRef.child("name").setValue(name);
        userRef.child("phoneNumber").setValue(phoneNumber);
        userRef.child("password").setValue(password);
        userRef.child("userType").setValue(userType);

        Log.d(TAG, "User data saved: Name - " + name + ", Email - " + email + ", Phone - "
                                + phoneNumber + ", Password - " + password + ", UserType - " + userType);

        checkUserType(context, userId);  // Redirect to the appropriate profile after registration
    }
}