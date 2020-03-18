package com.example.ecommerce;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.ecommerce.databinding.ActivityAdminAddSubCategoryBinding;
import com.example.ecommerce.databinding.ActivityAdminSubCategoryBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;

public class AdminAddSubCategoryActivity extends AppCompatActivity {

    ActivityAdminAddSubCategoryBinding activityAdminAddSubCategoryBinding;
    Intent intent;
    private ProgressDialog loadingBar;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private String categoryNameString, saveCurrentDate;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productImagesRef;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminAddSubCategoryBinding = DataBindingUtil.setContentView(this,R.layout.activity_admin_add_sub_category);

        intent = getIntent();
        String categoryString = getIntent().getStringExtra("category");

        productImagesRef = FirebaseStorage.getInstance().getReference().child("SubCategory Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("SubCategories").child(categoryString);
        loadingBar = new ProgressDialog(this);

        activityAdminAddSubCategoryBinding.imageSelectSubCategory.setOnClickListener(view -> {
            openGallery();
        });

        activityAdminAddSubCategoryBinding.buttonAddSubCategory.setOnClickListener(view -> {
            ValidateProductData();
        });
    }
    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            activityAdminAddSubCategoryBinding.imageSelectSubCategory.setImageURI(imageUri);
        }
    }

    private void ValidateProductData() {
        categoryNameString = activityAdminAddSubCategoryBinding.editTextNameCategory.getText().toString();
        if (imageUri == null) {
            Toast.makeText(this, "Please upload a sub-category image...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(categoryNameString)) {
            Toast.makeText(this, "Please write a sub-category name", Toast.LENGTH_SHORT).show();
        }  else {
            StoreProductInformation();
        }
    }
    private void StoreProductInformation() {
        loadingBar.setTitle("Add New sub-category");
        loadingBar.setMessage("Please wait whiles we add your new sub-category");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        saveCurrentDate = calendar.getTime().toString();
        productRandomKey = saveCurrentDate;
        final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(e -> {
            String message = e.toString();
            Toast.makeText(AdminAddSubCategoryActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(AdminAddSubCategoryActivity.this, "Sub-category uploaded Successfully", Toast.LENGTH_SHORT).show();
            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    loadingBar.dismiss();
                    throw task.getException();
                }
                downloadImageUrl = filePath.getDownloadUrl().toString();
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    downloadImageUrl = task.getResult().toString();
                    Toast.makeText(AdminAddSubCategoryActivity.this, "Product image Url successfully retrieved", Toast.LENGTH_SHORT).show();
                    SaveProductInfoToDatabase();
                }
            });
        });
    }

    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("image", downloadImageUrl);
        productMap.put("cname", categoryNameString);
        productsRef.child(categoryNameString).updateChildren(productMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminAddSubCategoryActivity.this, "Sub-category Added Successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        finish();
                    } else {
                        loadingBar.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(AdminAddSubCategoryActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
