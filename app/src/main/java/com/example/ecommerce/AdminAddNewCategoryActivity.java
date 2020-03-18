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

import com.example.ecommerce.databinding.ActivityAdminAddNewCategoryBinding;
import com.example.ecommerce.databinding.ActivityAdminAddNewProductBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewCategoryActivity extends AppCompatActivity {

    ActivityAdminAddNewCategoryBinding activityAdminAddNewCategoryBinding;
    String categoryName = "";
    Intent intent;
    private ProgressDialog loadingBar;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private String description, price, categoryNameString, saveCurrentDate, descriptionLong;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productImagesRef;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminAddNewCategoryBinding = DataBindingUtil.setContentView(this,R.layout.activity_admin_add_new_category);
        intent = getIntent();

        productImagesRef = FirebaseStorage.getInstance().getReference().child("Category Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Categories");
        loadingBar = new ProgressDialog(this);

        activityAdminAddNewCategoryBinding.imageSelectCategory.setOnClickListener(view -> {
            openGallery();
        });

        activityAdminAddNewCategoryBinding.buttonAddCategory.setOnClickListener(view -> {
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
            activityAdminAddNewCategoryBinding.imageSelectCategory.setImageURI(imageUri);
        }
    }

    private void ValidateProductData() {
        categoryNameString = activityAdminAddNewCategoryBinding.editTextNameCategory.getText().toString();
        if (imageUri == null) {
            Toast.makeText(this, "Please upload a category image...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(categoryNameString)) {
            Toast.makeText(this, "Please write a category name", Toast.LENGTH_SHORT).show();
        }  else {
            StoreProductInformation();
        }
    }
    private void StoreProductInformation() {
        loadingBar.setTitle("Add New Category");
        loadingBar.setMessage("Please wait whiles we add your new category");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        saveCurrentDate = calendar.getTime().toString();
        productRandomKey = saveCurrentDate;
        final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(e -> {
            String message = e.toString();
            Toast.makeText(AdminAddNewCategoryActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(AdminAddNewCategoryActivity.this, "Category uploaded Successfully", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AdminAddNewCategoryActivity.this, "Product image Url successfully retrieved", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AdminAddNewCategoryActivity.this, "Category Added Successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        finish();
                    } else {
                        loadingBar.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(AdminAddNewCategoryActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
