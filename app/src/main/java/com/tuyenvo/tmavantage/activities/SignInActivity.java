package com.tuyenvo.tmavantage.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tuyenvo.tmavantage.R;
import com.tuyenvo.tmavantage.utilities.Constants;
import com.tuyenvo.tmavantage.utilities.PreferenceManager;

public class SignInActivity extends AppCompatActivity {
    EditText username, password;
    TextView errorMessage;
    Button logIn;
    ProgressDialog progressDialog;
    PreferenceManager preferenceManager;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        logIn = findViewById(R.id.login);
        errorMessage = findViewById(R.id.errorMessage);

        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
            finish();
        }

        /* Login button */
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().trim().isEmpty()) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.enter_username);
                } else if (password.getText().toString().trim().isEmpty()) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.enter_password);
                } else {
                    signIn();
                }
            }
        });

    }

    public void openSignUpPage(View view) {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }

    private void signIn() {
        progressDialog = new ProgressDialog(SignInActivity.this); // Display a progress bar before doing Authentication
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog_login);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTIONS_USERS)
                .whereEqualTo(Constants.KEY_USERNAME, username.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, password.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                            Log.d("ID", documentSnapshot.getId());

                            preferenceManager.putString(Constants.KEY_USERNAME, documentSnapshot.getString(Constants.KEY_USERNAME));
                            preferenceManager.putString(Constants.KEY_PASSWORD, documentSnapshot.getString(Constants.KEY_PASSWORD));
                            Intent intent = new Intent(SignInActivity.this, DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            progressDialog.dismiss();
                            errorMessage.setVisibility(View.VISIBLE);
                            errorMessage.setText(R.string.wrong_username_password);
                        }
                    }
                });
    }
}