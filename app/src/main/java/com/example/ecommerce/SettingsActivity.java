package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import io.paperdb.Paper;


public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding activitySettingsBinding;
    private ClickHandler clickHandler;
    public Uri imageUri;
    public String myUrl = "";
    private StorageTask uploadTask;
    public StorageReference storageProfilePictureRef;
    public String checker = "";
    private String phoneNumberCheck;
    public String imageUriStore;
    public String dbName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        clickHandler = new ClickHandler(this);
        activitySettingsBinding.setClickHandler(clickHandler);
        userInfoDisplay();
    }
    private void userInfoDisplay() {
        dbName = getIntent().getStringExtra("dbName");
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child(dbName).child(Prevalent.currentUserOnline.getPhoneNumber());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("image").exists()) {
                        if(dbName.equals("Admins")){
                            String image = dataSnapshot.child("image").getValue().toString();
                            imageUriStore = image;
                            if (imageUriStore.length() > 10){
                                Picasso.get().load(image).into(activitySettingsBinding.settingsProfileImage);
                            }
                        }else{
                            String image = dataSnapshot.child("image").getValue().toString();
                            imageUriStore = image;
                            if (imageUriStore.length() > 10){
                                Picasso.get().load(image).into(activitySettingsBinding.settingsProfileImage);
                            }
                        }
                    }
                    if (dataSnapshot.child("address").exists()) {
                        String address = dataSnapshot.child("address").getValue().toString();
                        activitySettingsBinding.settingsAddress.setText(address);
                    }
                    String name = dataSnapshot.child("name").getValue().toString();
                    String password = dataSnapshot.child("password").getValue().toString();
                    String phoneNumber = dataSnapshot.child("phoneNumber").getValue().toString();
                    activitySettingsBinding.settingsPhoneNumber.setText(phoneNumber);
                    phoneNumberCheck = phoneNumber;
                    activitySettingsBinding.settingsFullName.setText(name);
                    activitySettingsBinding.settingsPassword.setText(password);
                }else{
                    Toast.makeText(SettingsActivity.this, "Data Base Error, please restart the app", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public class ClickHandler {
        private Context context;

        public ClickHandler(Context context) {
            this.context = context;
        }

        public void closeButton(View view) {
            finish();
        }

        public void changeProfileButton(View view) {
            checker = "clicked";
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(SettingsActivity.this);
        }

        public void saveButton(View view) {

            if (activitySettingsBinding.settingsPhoneNumber.getText().length() < 10){
                Toast.makeText(context, "Phone number incomplete", Toast.LENGTH_SHORT).show();
            }else if(activitySettingsBinding.settingsPassword.getText().length() < 1) {//change to higher number
                Toast.makeText(context, "Password needs at least 6 digits", Toast.LENGTH_SHORT).show();
            }
            else{
                if (checker.equals("clicked")) {
                    userInfoSaved();
                } else {
                    updateOnlyUserInfo();
                }
            }
        }
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(dbName);
        if (!activitySettingsBinding.settingsPhoneNumber.getText().toString().equals(phoneNumberCheck)) {
            ref.child(Prevalent.currentUserOnline.getPhoneNumber()).removeValue();
            Prevalent.currentUserOnline.setPhoneNumber(phoneNumberCheck);
        }
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", activitySettingsBinding.settingsFullName.getText().toString());
        userMap.put("address", activitySettingsBinding.settingsAddress.getText().toString());
        userMap.put("phoneNumber", activitySettingsBinding.settingsPhoneNumber.getText().toString());
        userMap.put("password", activitySettingsBinding.settingsPassword.getText().toString());
        userMap.put("image", imageUriStore);
        Paper.book().write(Prevalent.UserPhoneKey,activitySettingsBinding.settingsPhoneNumber.getText().toString());
        Paper.book().write(Prevalent.UserPasswordKey,activitySettingsBinding.settingsPassword.getText().toString());
        Prevalent.currentUserOnline.setName(activitySettingsBinding.settingsFullName.getText().toString());
        ref.child(activitySettingsBinding.settingsPhoneNumber.getText().toString()).updateChildren(userMap);
        Prevalent.currentUserOnline.setPhoneNumber(activitySettingsBinding.settingsPhoneNumber.getText().toString());
        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile Information successfully updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            activitySettingsBinding.settingsProfileImage.setImageURI(imageUri);
        } else if (TextUtils.isEmpty(activitySettingsBinding.settingsAddress.getText().toString())) {
            Toast.makeText(this, "Error, Try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(activitySettingsBinding.settingsFullName.getText().toString())) {
            Toast.makeText(this, "Please enter your name.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(activitySettingsBinding.settingsPhoneNumber.getText().toString())) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait, whiles we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri != null) {
            final StorageReference fileRef = storageProfilePictureRef.child(Prevalent.currentUserOnline.getPhoneNumber() + dbName);
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask((Continuation) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            })
                    .addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(dbName);

                            if (!activitySettingsBinding.settingsPhoneNumber.getText().toString().equals(phoneNumberCheck)) {
                                ref.child(Prevalent.currentUserOnline.getPhoneNumber()).removeValue();
                            }

                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("name", activitySettingsBinding.settingsFullName.getText().toString());
                            userMap.put("address", activitySettingsBinding.settingsAddress.getText().toString());
                            userMap.put("phoneNumber", activitySettingsBinding.settingsPhoneNumber.getText().toString());
                            userMap.put("password", activitySettingsBinding.settingsPassword.getText().toString());
                            userMap.put("image", myUrl);
                            ref.child(activitySettingsBinding.settingsPhoneNumber.getText().toString()).updateChildren(userMap);
                            Prevalent.currentUserOnline.setName(activitySettingsBinding.settingsFullName.getText().toString());
                            Prevalent.currentUserOnline.setImage(myUrl);
                            Prevalent.currentUserOnline.setPhoneNumber(activitySettingsBinding.settingsPhoneNumber.getText().toString());
                            progressDialog.dismiss();
                            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                            Toast.makeText(SettingsActivity.this, "Profile Information successfully updated", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }
}
