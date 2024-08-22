package com.example.myapplication.Helpers;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import android.content.Context;

import com.example.myapplication.Model.Activity_Main;
import com.google.firebase.auth.FirebaseAuth;

public class GeneralFun_Helper {

    public static boolean activities_showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        return false;
    }


    public static void signOut(Context context) {
        // Show sign out message
        activities_showToast(context, "Signing out...");

        // Sign out the user
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(context, Activity_Main.class);
        context.startActivity(intent);

        // Finish the current activity
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

}
