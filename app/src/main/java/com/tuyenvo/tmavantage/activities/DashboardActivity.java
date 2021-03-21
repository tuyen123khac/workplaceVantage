package com.tuyenvo.tmavantage.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tuyenvo.tmavantage.R;
import com.tuyenvo.tmavantage.utilities.Constants;
import com.tuyenvo.tmavantage.utilities.PreferenceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    private PreferenceManager preferenceManager;
    private ConstraintLayout constraintLayout;
    private CardView newsFeed, googleMap, settings, messenger, traveling, more;
    private ImageView profilePicture, presenceStatus;
    private LinearLayout dashboardProfileName;
    private Animation fromBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        preferenceManager = new PreferenceManager(getApplicationContext());
        dashboardProfileName = findViewById(R.id.dashboardProfileName);
        profilePicture = findViewById(R.id.dashboardProfilePicture);
        newsFeed = findViewById(R.id.newsFeed); // Think how to reduce lines of code
        googleMap = findViewById(R.id.googleMap);
        settings = findViewById(R.id.settings);
        messenger = findViewById(R.id.messenger);
        traveling = findViewById(R.id.traveling);
        presenceStatus = findViewById(R.id.presenceStatus);
        more = findViewById(R.id.more);

        dashboardProfileName.setOnClickListener(this);
        newsFeed.setOnClickListener(this);
        googleMap.setOnClickListener(this);
        settings.setOnClickListener(this);
        messenger.setOnClickListener(this);
        traveling.setOnClickListener(this);
        more.setOnClickListener(this);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpMenu(v);
            }
        });

        initialDashboardAnimation();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.d("DashFCM", "Send token to FireStore " + task.getResult());
                sendFCMTokenToDatabase(task.getResult());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("DashFCM", "Error when getting token: " + e.getMessage());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndLoadProfilePicture();
        updatePresenceStatus();
    }

    @SuppressLint("RestrictedApi")
    private void showPopUpMenu(View v) {
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.pop_up_menu_dash_board, menuBuilder);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTIONS_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, v);
        optionsMenu.setForceShowIcon(true);

        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logOut:
                        showLogOutDialog();
                        return true;
                    case R.id.availableStatus:
                        documentReference.update(Constants.KEY_USER_STATUS, getResources().getString(R.string.available_status));
                        preferenceManager.putString(Constants.KEY_USER_STATUS, getResources().getString(R.string.available_status));
                        presenceStatus.setBackgroundResource(R.drawable.ic_online);
                        return true;
                    case R.id.busyStatus:
                        documentReference.update(Constants.KEY_USER_STATUS, getResources().getString(R.string.busy_status));
                        preferenceManager.putString(Constants.KEY_USER_STATUS, getResources().getString(R.string.busy_status));
                        presenceStatus.setBackgroundResource(R.drawable.ic_busy);
                        return true;
                    case R.id.awayStatus:
                        documentReference.update(Constants.KEY_USER_STATUS, getResources().getString(R.string.away_status));
                        preferenceManager.putString(Constants.KEY_USER_STATUS, getResources().getString(R.string.away_status));
                        presenceStatus.setBackgroundResource(R.drawable.ic_away);
                        return true;
                    case R.id.offlineStatus:
                        documentReference.update(Constants.KEY_USER_STATUS, getResources().getString(R.string.offline_status));
                        preferenceManager.putString(Constants.KEY_USER_STATUS, getResources().getString(R.string.offline_status));
                        presenceStatus.setBackgroundResource(R.drawable.ic_offline);
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {
                Toast.makeText(DashboardActivity.this, "ABC", Toast.LENGTH_SHORT).show();
            }
        });
        optionsMenu.show();
    }

    private void updatePresenceStatus() {
        String status = preferenceManager.getString(Constants.KEY_USER_STATUS);
        if (status != null) {
            if (status.equals(getResources().getString(R.string.available_status))) {
                presenceStatus.setBackgroundResource(R.drawable.ic_online);
            } else if (status.equals(getResources().getString(R.string.busy_status))) {
                presenceStatus.setBackgroundResource(R.drawable.ic_busy);
            } else if (status.equals(getResources().getString(R.string.away_status))) {
                presenceStatus.setBackgroundResource(R.drawable.ic_away);
            } else if (status.equals(getResources().getString(R.string.offline_status))) {
                presenceStatus.setBackgroundResource(R.drawable.ic_offline);
            } else {
                Log.d("STATUS", "An invalid status");
            }
        } else {
            Log.d("STATUS", "KEY_USER_STATUS is empty. Maybe this is the first time, status is available");
        }
    }

    private void showLogOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DashboardActivity.this).inflate(
                R.layout.logout_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );

        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Log Out");
        ((TextView) view.findViewById(R.id.textMessage)).setText("Are you sure you want to Log Out ?");
        ((TextView) view.findViewById(R.id.buttonYes)).setText("Yes");
        ((TextView) view.findViewById(R.id.buttonNo)).setText("No");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_exit);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Toast.makeText(DashboardActivity.this, "Signing Out ...", Toast.LENGTH_SHORT).show();
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                DocumentReference documentReference =
                        database.collection(Constants.KEY_COLLECTIONS_USERS).document(
                                preferenceManager.getString(Constants.KEY_USER_ID)
                        );
                Map<String, Object> updates = new HashMap<>();
                updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
                documentReference.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                preferenceManager.clearPreferences();
                                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("DashFCM", "Something wrong when clear FCM");
                                Toast.makeText(DashboardActivity.this, "Unable to sign out", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private void sendFCMTokenToDatabase(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTIONS_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DashboardActivity.this, "Unale to send token", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndLoadProfilePicture() {
        if (preferenceManager.getString(Constants.KEY_PROFILE_PICTURE_ID) == null) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            DocumentReference documentReference = database.collection(Constants.KEY_COLLECTIONS_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
            documentReference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Map<String, Object> documentMap = new HashMap<>();
                            documentMap = documentSnapshot.getData();
                            if (documentMap.containsKey(Constants.KEY_PROFILE_PICTURE_ID)) {
                                if (documentMap.get(Constants.KEY_PROFILE_PICTURE_ID) != null) {
                                    Log.d("PROPIC", "Load profile picture URL from FireStore");
                                    preferenceManager.putString(Constants.KEY_PROFILE_PICTURE_ID, documentMap.get(Constants.KEY_PROFILE_PICTURE_ID).toString());
                                    Glide.with(DashboardActivity.this).load(documentMap.get(Constants.KEY_PROFILE_PICTURE_ID)).into(profilePicture);
                                } else {

                                    Log.d("PROPIC", "Value of " + Constants.KEY_PROFILE_PICTURE_ID + " is null");
                                }
                            } else {
                                Glide.with(DashboardActivity.this).load(getResources().getDrawable(R.drawable.avatar)).into(profilePicture);
                                Log.d("PROPIC", Constants.KEY_PROFILE_PICTURE_ID + " is null");
                            }
                        }
                    });
        } else {
            Glide.with(DashboardActivity.this).load(preferenceManager.getString(Constants.KEY_PROFILE_PICTURE_ID)).into(profilePicture);
        }
    }

    private void initialDashboardAnimation() {
        constraintLayout = findViewById(R.id.myConstraint);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.fall_down);
        constraintLayout.startAnimation(fromBottom);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.dashboardProfileName:
                intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.newsFeed:
                Toast.makeText(this, "Have not implemented yet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.googleMap:
                Toast.makeText(this, "Have not implemented yet1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "Have not implemented yet3", Toast.LENGTH_SHORT).show();
                break;
            case R.id.messenger:
                intent = new Intent(DashboardActivity.this, MessengerActivity.class);
                startActivity(intent);
                break;
            case R.id.traveling:
                Toast.makeText(this, "Have not implemented yet4", Toast.LENGTH_SHORT).show();
                break;
            case R.id.more:
                Toast.makeText(this, "Have not implemented yet5", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}