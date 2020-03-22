package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ecommerce.databinding.ActivityAdminAddNewProductBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {
    ActivityAdminAddNewProductBinding activityAdminAddNewProductBinding;
    String categoryName = "";
    String subCategoryName = "";
    Intent intent;
    private ProgressDialog loadingBar;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private String description, price, productName, saveCurrentDate, descriptionLong;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productImagesRef;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAdminAddNewProductBinding = DataBindingUtil.setContentView(this, R.layout.activity_admin_add_new_product);
        ClickHandler clickHandler = new ClickHandler(this);
        activityAdminAddNewProductBinding.setClickHandler(clickHandler);
        intent = getIntent();
        categoryName = getIntent().getExtras().get("category").toString();
        subCategoryName = getIntent().getExtras().get("subCategory").toString();

        Toast.makeText(this, "Category: " + categoryName, Toast.LENGTH_LONG).show();
        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        loadingBar = new ProgressDialog(this);

    }

    public class ClickHandler {
        private Context context;

        public ClickHandler(Context context) {
            this.context = context;
        }

        public void inputProductImage(View view) {
            openGallery();
        }

        public void addProductButtonClick(View view) {
            ValidateProductData();
        }
    }

    private void ValidateProductData() {
        description = activityAdminAddNewProductBinding.editTextDescriptionNew.getText().toString();
        price = activityAdminAddNewProductBinding.editTextNameNewProduct.getText().toString();
        productName = activityAdminAddNewProductBinding.editTextNameNewProduct.getText().toString();
        descriptionLong = activityAdminAddNewProductBinding.editTextDescriptionLongNew.getText().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Please upload a product image...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please write a product description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Please write a product price", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(productName)) {
            Toast.makeText(this, "Please write a product name", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {
        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Please wait whiles we add your new product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        saveCurrentDate = calendar.getTime().toString();
        productRandomKey = saveCurrentDate;
        final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(e -> {
            String message = e.toString();
            Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(AdminAddNewProductActivity.this, "Product Image uploaded Successfully", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AdminAddNewProductActivity.this, "Product image Url successfully retrieved", Toast.LENGTH_SHORT).show();
                    SaveProductInfoToDatabase();
                }
            });
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
            activityAdminAddNewProductBinding.imageSelectProductNew.setImageURI(imageUri);
        }
    }

    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("description", description);
        productMap.put("descriptionLong", descriptionLong);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price", price);
        productMap.put("pname", productName);
        productMap.put("subCategory",subCategoryName);
        productsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminAddNewProductActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        finish();
                    } else {
                        loadingBar.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
