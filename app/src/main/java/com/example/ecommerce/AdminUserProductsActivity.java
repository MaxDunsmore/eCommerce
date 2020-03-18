package com.example.ecommerce;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.ViewHolder.CartViewHolder;
import com.example.ecommerce.databinding.ActivityAdminUserProductsBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminUserProductsActivity extends AppCompatActivity {
    ActivityAdminUserProductsBinding activityAdminUserProductsBinding;

    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListRef;
    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminUserProductsBinding = DataBindingUtil.setContentView(this, R.layout.activity_admin_user_products);

        userID = getIntent().getStringExtra("uid");
        activityAdminUserProductsBinding.recyclerProductsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        activityAdminUserProductsBinding.recyclerProductsList.setLayoutManager(layoutManager);

        cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List").child("Admin View").child(userID).child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef, Cart.class)
                        .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                String stringQuantity = "Quantity: " + model.getQuantity();
                String stringPrice = "Price: " + model.getPrice();
                String stringName = model.getPname();
                holder.textProductQuantity.setText(stringQuantity);
                holder.textProductPrice.setText(stringPrice);
                holder.textProductName.setText(stringName);
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                return new CartViewHolder(view);
            }

        };
        activityAdminUserProductsBinding.recyclerProductsList.setAdapter(adapter);
        adapter.startListening();
    }
}
