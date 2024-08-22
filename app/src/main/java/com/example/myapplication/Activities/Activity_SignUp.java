package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.myapplication.Helpers.GeneralFun_Helper;
import com.example.myapplication.Helpers.Singing_Helper;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Activity_SignUp extends AppCompatActivity {

    private Button submitButton;
    private Button goBackButton;

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneNumberEditText;

    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String userType;

    private RadioGroup userTypeRadioGroup;

    private FirebaseAuth mAuth;

    private Singing_Helper singingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Authentication object
        mAuth = FirebaseAuth.getInstance();

        singingHelper = new Singing_Helper();

        findViews();
        allButtonListeners();
    }

    private void allButtonListeners() {
        goBackButton.setOnClickListener(v -> finish());

        submitButton.setOnClickListener(v -> {
            // Get the input from the user
            // .trim() ignores whitespace
            name = nameEditText.getText().toString().trim();
            email = emailEditText.getText().toString().trim();
            password = passwordEditText.getText().toString().trim();
            phoneNumber = phoneNumberEditText.getText().toString().trim();
            userType = ((RadioButton) findViewById(userTypeRadioGroup.getCheckedRadioButtonId())).getText().toString().toLowerCase();

            // Check that the user's input is not empty
            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !phoneNumber.isEmpty()) {
                registerUser(name, email, password, phoneNumber, userType);
            } else {
                GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Some Info Is Missing");
            }
        });
    }

    private boolean validateUserInput() {
        // Get the input from the user
        name = nameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        phoneNumber = phoneNumberEditText.getText().toString();

        // Check that the user's input is valid
        return checkFullName(name) && checkEmail(email) && checkPassword(password) && checkPhone(phoneNumber);
    }

    private void registerUser(String name, String email, String password, String phoneNumber, String userType) {
        // Check if the user's input is valid
        if (validateUserInput()) {

            // Save the email and password of the new user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Send the details to saveUserData function in Singing_Helper class
                            assert user != null;
                            singingHelper.saveUserData(Activity_SignUp.this, user, name, email, phoneNumber, password, userType);
                        } else {
                            GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Sign Up Failed. Enter valid details and try again");
                        }
                    });
        } else {
            GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Sign Up Failed. Enter valid details and try again");
        }
    }

    private boolean checkFullName(String fullName) {
        String regex = "^(?!.{51})[a-zA-Z-]+(?: [a-zA-Z]+(?: [a-zA-Z-]+)?)?$";
        return fullName.matches(regex) || GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Invalid Name, Try Again");
    }

    private boolean checkEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex) || GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Invalid Email, Please Enter a Valid Email Address");
    }

    private boolean checkPassword(String password) {
        String regex = "^[a-zA-Z0-9\\W_]{6,20}$";
        return password.matches(regex) || GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Invalid Password. Password must contain between 6-20 characters.");
    }

    private boolean checkPhone(String phoneNumber) {
        String regex = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";
        return phoneNumber.matches(regex) || GeneralFun_Helper.activities_showToast(Activity_SignUp.this, "Invalid Phone Number. Please enter a valid 10 digits number.");
    }

    private void findViews() {
        goBackButton = findViewById(R.id.signup_BTN_goBack);
        nameEditText = findViewById(R.id.signup_BOX_name);
        emailEditText = findViewById(R.id.signup_BOX_email);
        passwordEditText = findViewById(R.id.signup_BOX_password);
        phoneNumberEditText = findViewById(R.id.signup_BOX_phoneNumber);
        submitButton = findViewById(R.id.signup_BTN_submit);
        userTypeRadioGroup = findViewById(R.id.signup_radioGroup_userType);
    }
}
