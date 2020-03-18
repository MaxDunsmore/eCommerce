package com.example.ecommerce;

import android.app.AlertDialog;
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

import com.example.ecommerce.Model.Products;
import com.example.ecommerce.databinding.ActivityAdminMaintainProductsBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {
    ActivityAdminMaintainProductsBinding activityAdminMaintainProductsBinding;
    String productID = "", state = "Normal";
    private DatabaseReference productRef;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private String description;
    private String price;
    private String productName;
    private String descriptionLong;
    private ProgressDialog loadingBar;
    private String productRandomKey;
    private String downloadImageUrl = "";
    Intent intent;
    private StorageReference productImagesRef;
    private DatabaseReference productsRef;
    private String tracker = "";
    public String saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminMaintainProductsBinding = DataBindingUtil.setContentView(this, R.layout.activity_admin_maintain_products);

        productID = getIntent().getStringExtra("pid");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        intent = getIntent();
        loadingBar = new ProgressDialog(this);

        displaySpecificProductInfo();
        getProductDetails();

        activityAdminMaintainProductsBinding.productImageMaintain.setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GalleryPick);
            tracker = "clicked";
        });
        activityAdminMaintainProductsBinding.buttonApplyMaintain.setOnClickListener(v -> ValidateProductData());
        activityAdminMaintainProductsBinding.buttonDeleteMaintain.setOnClickListener(view -> {

            CharSequence[] options = new CharSequence[]{
                    "Yes - Delete Product",
                    "No"
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(AdminMaintainProductsActivity.this);
            builder.setTitle("Would you like to delete this product?");
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    builder.show();
                    productRef.removeValue().addOnCompleteListener(task -> {
                        Toast.makeText(AdminMaintainProductsActivity.this, "Product Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
                if (which == 1) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });

        ClickHandler clickHandler = new ClickHandler(this);
        activityAdminMaintainProductsBinding.setClickHandler(clickHandler);
    }

    public class ClickHandler {
        private Context context;

        public ClickHandler(Context context) {
            this.context = context;
        }
    }

    private void ValidateProductData() {
        description = activityAdminMaintainProductsBinding.editTextDescriptionMaintain.getText().toString();
        price = activityAdminMaintainProductsBinding.editTextPriceMaintain.getText().toString();
        productName = activityAdminMaintainProductsBinding.editTextNameNewMaintain.getText().toString();
        descriptionLong = activityAdminMaintainProductsBinding.editTextLongDescriptionMaintain.getText().toString();

        if (TextUtils.isEmpty(description)) {
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
        productRandomKey = productID;
        if (!tracker.equals("")) {
            final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
            final UploadTask uploadTask = filePath.putFile(imageUri);
            uploadTask.addOnFailureListener(e -> {
                String message = e.toString();
                Toast.makeText(AdminMaintainProductsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(AdminMaintainProductsActivity.this, "Product Image uploaded Successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            loadingBar.dismiss();
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminMaintainProductsActivity.this, "Product image Url successfully retrieved", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            });
        } else {
            SaveProductInfoToDatabase();
        }

    }

    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("description", description);
        //add long description code
        productMap.put("descriptionLong", descriptionLong);
        if (!tracker.equals("")) {
            productMap.put("image", downloadImageUrl);
        }

        productMap.put("price", price);
        productMap.put("pname", productName);

        productsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminMaintainProductsActivity.this, "Changes Applied Successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        finish();
                    } else {
                        loadingBar.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(AdminMaintainProductsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void getProductDetails() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Products products = dataSnapshot.getValue(Products.class);
                    activityAdminMaintainProductsBinding.editTextNameNewMaintain.setText(products.getPname());
                    activityAdminMaintainProductsBinding.editTextPriceMaintain.setText(products.getPrice());
                    activityAdminMaintainProductsBinding.editTextDescriptionMaintain.setText(products.getDescription());
                    activityAdminMaintainProductsBinding.editTextLongDescriptionMaintain.setText(products.getDescriptionLong());
                    Picasso.get().load(products.getImage()).into(activityAdminMaintainProductsBinding.productImageMaintain);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            activityAdminMaintainProductsBinding.productImageMaintain.setImageURI(imageUri);
        }
    }


    private void displaySpecificProductInfo() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String pName = dataSnapshot.child("pname").getValue().toString();
                    String pPrice = dataSnapshot.child("image").getValue().toString();
                    String pDescription = dataSnapshot.child("description").getValue().toString();
                    String pLongDescription = dataSnapshot.child("descriptionLong").getValue().toString();
                    String pImage = dataSnapshot.child("price").getValue().toString();
                    activityAdminMaintainProductsBinding.editTextNameNewMaintain.setText(pName);
                    activityAdminMaintainProductsBinding.editTextPriceMaintain.setText(pPrice);
                    activityAdminMaintainProductsBinding.editTextDescriptionMaintain.setText(pDescription);
                    activityAdminMaintainProductsBinding.editTextLongDescriptionMaintain.setText(pLongDescription);
                    Picasso.get().load(pImage).into(activityAdminMaintainProductsBinding.productImageMaintain);
/*
                    activityAdminMaintainProductsBinding.buttonApplyChangesMaintain2.setOnClickListener(view -> {
                        applyChanges();
                    });*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void applyChanges() {
        String pName = activityAdminMaintainProductsBinding.editTextNameNewMaintain.getText().toString();
        String pPrice = activityAdminMaintainProductsBinding.editTextPriceMaintain.getText().toString();
        String pDescription = activityAdminMaintainProductsBinding.editTextDescriptionMaintain.getText().toString();
        String pLongDescription = activityAdminMaintainProductsBinding.editTextLongDescriptionMaintain.getText().toString();
        if (pName.equals("")) {
            Toast.makeText(this, "Write down Product Name.", Toast.LENGTH_SHORT).show();
        } else if (pPrice.equals("")) {
            Toast.makeText(this, "Write down Product Price.", Toast.LENGTH_SHORT).show();
        } else if (pDescription.equals("")) {
            Toast.makeText(this, "Write down Product Description.", Toast.LENGTH_SHORT).show();
        } else if (pLongDescription.equals("")) {
            Toast.makeText(this, "Write down Product Full description.", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("description", pDescription);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);
            productMap.put("descriptionLong", pLongDescription);
            if (downloadImageUrl.length() > 3) {
                productMap.put("image", downloadImageUrl);
            }

        }
    }

}
