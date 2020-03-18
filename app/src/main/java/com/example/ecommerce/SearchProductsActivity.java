package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerce.Model.Products;
import com.example.ecommerce.ViewHolder.ProductViewHolder;
import com.example.ecommerce.databinding.ActivitySearchProductsBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchProductsActivity extends AppCompatActivity {
    ActivitySearchProductsBinding activitySearchProductsBinding;
    String searchInput;
    String upperString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchProductsBinding = DataBindingUtil.setContentView(this,R.layout.activity_search_products);
        activitySearchProductsBinding.recyclerListProducts.setLayoutManager(new LinearLayoutManager(SearchProductsActivity.this));
        activitySearchProductsBinding.buttonSearchProducts.setOnClickListener(view -> {
            searchInput = activitySearchProductsBinding.editTextSearchProducts.getText().toString();
            upperString = searchInput.substring(0, 1).toUpperCase() + searchInput.substring(1).toLowerCase();
            onStart();

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
        //possibly change pname to description search by description

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("pname").startAt(upperString).endAt(upperString+"\uf8ff"),Products.class)
                .build();



        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter
                = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                String stringPriceHome = "Price: " + model.getPrice();
                holder.txtProductName.setText(model.getPname());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText(stringPriceHome);
                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.imageView.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchProductsActivity.this,ProductDetailsActivity.class );
                    intent.putExtra("pid",model.getPid());
                    startActivity(intent);

                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
                return new ProductViewHolder(view);
            }
        };
        activitySearchProductsBinding.recyclerListProducts.setAdapter(adapter);
        adapter.startListening();
    }
}
