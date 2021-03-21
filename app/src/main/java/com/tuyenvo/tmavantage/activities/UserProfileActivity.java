package com.tuyenvo.tmavantage.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;
import com.tuyenvo.tmavantage.R;
import com.tuyenvo.tmavantage.utilities.Constants;
import com.tuyenvo.tmavantage.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity implements BottomSheetImagePicker.OnImagesSelectedListener {

    private static final int REQUEST_CODE_AND_CAMERA_PERMISSION = 101;
    private ImageView profilePicture;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profilePicture = findViewById(R.id.profilePicture);
        storageReference = FirebaseStorage.getInstance().getReference(Constants.PROFILE_PICTURE_COLLECTIONS);
        preferenceManager = new PreferenceManager(getApplicationContext());

        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
                int readStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                int writePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                List<String> permissionsNeeded = new ArrayList<>();
                if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(Manifest.permission.CAMERA);
                }
                if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (writePermission != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                if (!permissionsNeeded.isEmpty()) {
                    ActivityCompat.requestPermissions(UserProfileActivity.this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), REQUEST_CODE_AND_CAMERA_PERMISSION);
                } else {
                    openImagePicker();
                }
            }
        });
    }

    private void openImagePicker() {
        BottomSheetImagePicker bottomSheetImagePicker = new BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                .cameraButton(ButtonType.Button)
                .galleryButton(ButtonType.Button)
                .singleSelectTitle(R.string.bottom_image_picker_single)
                .peekHeight(R.dimen._260sdp)
                .columnSize(R.dimen._92sdp)
                .requestTag("single")
                .build();
        bottomSheetImagePicker.show(getSupportFragmentManager(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfilePictureFromSharedPreference();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_AND_CAMERA_PERMISSION && grantResults.length > 2) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    && (grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onImagesSelected(List<? extends Uri> list, String s) {
        uploadImage(list.get(0));
        Glide.with(UserProfileActivity.this).load(list.get(0)).into(profilePicture);
    }

    private void loadProfilePictureFromSharedPreference() {
        Glide.with(UserProfileActivity.this).load(preferenceManager.getString(Constants.KEY_PROFILE_PICTURE_ID)).into(profilePicture);
    }

    private String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(Uri uri) {
        progressDialog = new ProgressDialog(UserProfileActivity.this); // Display a progress bar before doing Authentication
        if (uri != null) {
            StorageReference fileReference = storageReference.child(preferenceManager.getString(Constants.KEY_USERNAME) + "/" + System.currentTimeMillis()
                    + "." + getImageExtension(uri));
            StorageReference folderReference = storageReference.child(preferenceManager.getString(Constants.KEY_USERNAME) + "/");
            folderReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    if(listResult.getItems() != null){
                        for(StorageReference item: listResult.getItems()){
                            item.delete();
                        }
                    }else {
                        Log.d("DELETEFILE", "Profile picture folder is empty or there is some error");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("DELETEFILE", "Delete the profile picture folder of this user failed");
                }
            });

            fileReference.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            }, 500);

                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            DocumentReference documentReference =
                                    database.collection(Constants.KEY_COLLECTIONS_USERS).document(
                                            preferenceManager.getString(Constants.KEY_USER_ID)
                                    );

                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    documentReference.update(Constants.KEY_PROFILE_PICTURE_ID
                                                            , uri.toString())
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    preferenceManager.putString(Constants.KEY_PROFILE_PICTURE_ID, uri.toString());
                                                                    Log.d("UPFILE", "Update Profile picture URL to FireStore successfully and add SharePreferences");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.e("UPFILE", "Update Profile picture URL to FireStore failed");
                                                                    Toast.makeText(UserProfileActivity.this, "Update Profile picture URL to FireStore failed", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("UPFILE", "Get Profile picture URL from Cloud Storage failed");
                                                    Toast.makeText(UserProfileActivity.this, "Get Profile picture URL from Cloud Storage failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Log.e("UPFILE", "taskSnapshot getReference is null");
                                }
                            } else {
                                Log.e("UPFILE", "taskSnapshot getMetaData is null");
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("UPFILE", "Upload Profile picture URL to Cloud Storage failed");
                            Toast.makeText(UserProfileActivity.this, "Upload Profile picture URL to Cloud Storage failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressDialog.show();
                            progressDialog.setProgress((int) progress);
                            progressDialog.setContentView(R.layout.progress_dialog);
                            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        }
                    });

        } else {
            Log.e("UPFILE", "Cannot get Uri of Image from local");
            Toast.makeText(this, "Cannot get Uri of Image from local", Toast.LENGTH_SHORT).show();
        }
    }
}