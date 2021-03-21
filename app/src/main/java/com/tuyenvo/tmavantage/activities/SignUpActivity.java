package com.tuyenvo.tmavantage.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tuyenvo.tmavantage.R;
import com.tuyenvo.tmavantage.utilities.Constants;
import com.tuyenvo.tmavantage.utilities.PreferenceManager;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    ImageView backButton, errorIcon;
    EditText inputUsername, inputPassword, inputConfirmPassword;
    TextView errorMessage;
    Button signIn;
    ProgressDialog progressDialog;
    FirebaseFirestore database;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        backButton = findViewById(R.id.backButton);
        inputUsername = findViewById(R.id.username);
        inputPassword = findViewById(R.id.password);
        inputConfirmPassword = findViewById(R.id.confirmPassword);
        signIn = findViewById(R.id.signIn);
        errorIcon = findViewById(R.id.errorIcon);
        errorMessage = findViewById(R.id.errorMessage);

        preferenceManager = new PreferenceManager(getApplicationContext());

        backButton.setOnClickListener(v -> onBackPressed());

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Pop up progress Dialog */
                if (inputUsername.getText().toString().trim().isEmpty()) {
                    errorIcon.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.enter_username);
                } else if (inputPassword.getText().toString().trim().isEmpty()) {
                    errorIcon.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.enter_password);
                } else if (inputConfirmPassword.getText().toString().trim().isEmpty()) {
                    errorIcon.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.confirm_password);
                } else if (!inputPassword.getText().toString().equals(inputConfirmPassword.getText().toString())) {
                    errorIcon.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.confirm_password_is_wrong);
                } else {
                    signUp();
                }
            }
        });
    }

    /* Register to Firebase Realtime database and login*/
    private void signUp() {
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog_sign_up);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_USERNAME, inputUsername.getText().toString());
        user.put(Constants.KEY_PASSWORD, inputPassword.getText().toString());

        database.collection(Constants.KEY_COLLECTIONS_USERS)
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                        preferenceManager.putString(Constants.KEY_USERNAME, inputUsername.getText().toString());
                        preferenceManager.putString(Constants.KEY_PASSWORD, inputPassword.getText().toString());
                        Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Toast.makeText(SignUpActivity.this, R.string.user_created_successfully, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        errorIcon.setVisibility(View.VISIBLE);
                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText(R.string.error_cannot_create_user);
                    }
                });
    }
}