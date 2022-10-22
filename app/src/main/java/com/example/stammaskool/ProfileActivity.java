package com.example.stammaskool;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ProfileActivity extends AppCompatActivity {

    private final int AUDIO_REQUEST = 200;
    private final String POSTS_COLLECTION = "Posts";

    RecyclerView recyclerView;
    ImageView imageViewPostDialogImage, profileImage;
    private final int SELECT_FILE = 1, PROFILE_PIC = 2;
    private Uri filePath = null;

    EditText editName, editPhone, editEmail;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference userProfileReference;
    FirebaseAuth auth;
    FirebaseUser user;

    EditText editTextCaption;
    Button buttonSubmit;
    ImageView buttonCancel;
    ProgressBar postProgressBar;



    TextView textViewWarning;
    ProgressBar progressBar;
    TextView textViewUserName, textViewUserEmail, textViewPhoneNumber;
    SharedPrefs sharedPrefs;
    Dialog updateDialog;


    boolean imageSelected = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPrefs = new SharedPrefs(this);

        initView();
        getUserProfile();

    }


    private void getUserProfile() {

        textViewUserName.setText(sharedPrefs.userName());
        textViewUserEmail.setText(sharedPrefs.userEmail());
        textViewPhoneNumber.setText(sharedPrefs.userPhone());

        if (!sharedPrefs.userImage().isEmpty()) {

            Picasso.get().load(sharedPrefs.userImage()).
                    placeholder(R.drawable.default_profile).
                    into(profileImage);
        }

    }

    private void initView() {

        textViewPhoneNumber = findViewById(R.id.textViewUserPhone);
        profileImage = findViewById(R.id.profile_pic);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        textViewUserName = findViewById(R.id.textViewUserName);
        textViewUserEmail = findViewById(R.id.textViewUserEmail);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.editBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                galleryIntent(PROFILE_PIC);
            }
        });
    }


    private void uploadImage() {

        StorageReference bucketReference = storageReference.child("image" + System.currentTimeMillis() + ".png");
        bucketReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                postProgressBar.setVisibility(View.GONE);
                toast("Application Failed to upload your image : " + e.getMessage());
            }
        });

    }

    private void showUpdateDialog() {

        updateDialog = new Dialog(this, R.style.FullScreenDialogStyle);
        updateDialog.setContentView(R.layout.dialog_update_profile);
        updateDialog.setCancelable(false);


        Button buttonUpdate;
        ImageView buttonCancel;
        editName = updateDialog.findViewById(R.id.editUserName);
        editPhone = updateDialog.findViewById(R.id.editUserPhone);
        editEmail = updateDialog.findViewById(R.id.editUserEmail);
        buttonCancel = updateDialog.findViewById(R.id.buttonCancel);
        buttonUpdate = updateDialog.findViewById(R.id.buttonSubmit);

        editName.setText(sharedPrefs.userName());
        editEmail.setText(sharedPrefs.userEmail());
        editPhone.setText(sharedPrefs.userPhone());

        updateDialog.show();


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editName.getText().toString().isEmpty()) {
                    toast("Enter user name");
                } else if (editEmail.getText().toString().isEmpty()) {
                    toast("Enter email address");
                } else if (editPhone.getText().toString().isEmpty()) {
                    toast("Enter phone number");
                } else {
                    updateProfile(null, null);
                }
            }
        });


    }


    private void updateProfile(Bitmap bm, String imageUrl) {

        Map<String, Object> childUpdates;

        if (imageUrl != null) {

            childUpdates = new HashMap<>();
            childUpdates.put("imageUrl", imageUrl);
        } else {

            childUpdates = new HashMap<>();
            childUpdates.put("userName", editName.getText().toString());
            childUpdates.put("email", editEmail.getText().toString());
            childUpdates.put("phone", editPhone.getText().toString());

        }


        userProfileReference.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if (imageUrl != null) {
                            sharedPrefs.setUserImage(imageUrl);
                            profileImage.setImageBitmap(bm);
                        } else {

                            sharedPrefs.setUserPhone(editPhone.getText().toString());
                            sharedPrefs.setUserEmail(editEmail.getText().toString());
                            sharedPrefs.setUserName(editName.getText().toString());


                            getUserProfile();
                        }

                        toast("Profile updated successfully");

                        if (updateDialog != null) {
                            updateDialog.dismiss();
                        }

                    }
                });

    }

    private void galleryIntent(int intentType) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), intentType);
    }


    private void onSelectFromGalleryResult(Intent data, int requestCode) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                if (bm != null) {
                    imageSelected = true;
                    filePath = data.getData();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bm != null) {

            if (requestCode == SELECT_FILE) {
                imageViewPostDialogImage.setImageBitmap(bm);
            } else {
                updateProfilePic(bm);
            }


        } else {
            toast("Failed to upload image try again");
        }

    }

    private void toast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }


    private void updateProfilePic(Bitmap bm) {

        toast("Updating profile picture");

        StorageReference bucketReference = storageReference.child("image" + System.currentTimeMillis() + ".png");
        bucketReference.putFile(filePath).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl()
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {

                                        updateProfile(bm, task.getResult().toString());

                                    }
                                });

                    }
                });
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);

    }

    private void showMessage() {
        textViewWarning.setVisibility(View.VISIBLE);
    }

    private void hideMessage() {
        textViewWarning.setVisibility(View.GONE);

    }

}
