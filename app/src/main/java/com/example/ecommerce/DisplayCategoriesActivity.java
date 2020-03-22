package com.example.ecommerce;

import android.content.Context;
import android.content.Intent;
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

import com.example.ecommerce.Model.Products;
import com.example.ecommerce.ViewHolder.ProductViewHolder;
import com.example.ecommerce.databinding.ActivityDisplayCategoriesBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class DisplayCategoriesActivity extends AppCompatActivity {
    //public String category;
    private ClickHandler clickHandler;
    ActivityDisplayCategoriesBinding activityDisplayCategoriesBinding;
    RecyclerView.LayoutManager layoutManager;
    public String dbName;
    public String category;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDisplayCategoriesBinding = DataBindingUtil.setContentView(this, R.layout.activity_display_categories);

        clickHandler = new ClickHandler(this);
        activityDisplayCategoriesBinding.setClickHandler(clickHandler);
        dbName = getIntent().getStringExtra("dbName");

        reference = FirebaseDatabase.getInstance().getReference().child("Products");
        activityDisplayCategoriesBinding.recyclerCategoryCategoriesDisplay.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        activityDisplayCategoriesBinding.recyclerCategoryCategoriesDisplay.setLayoutManager(layoutManager);
        category = getIntent().getStringExtra("category");
        Log.d("!@!@",category);
    }

    public class ClickHandler {
        private Context context;
        public String category = getIntent().getStringExtra("category");

        public ClickHandler(Context context) {
            this.context = context;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbName = getIntent().getStringExtra("dbName");
        Log.d("!@!~",dbName);

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(reference.orderByChild("category").equalTo(category), Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                        String stringPriceHome = "Price: " + model.getPrice();
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText(stringPriceHome);
                        Picasso.get().load(model.getImage()).into(holder.imageView);
                        holder.imageView.setOnClickListener(v -> {
                            if (dbName.equals("Admins")) {
                                Intent intent = new Intent(DisplayCategoriesActivity.this, AdminMaintainProductsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(DisplayCategoriesActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
                        return new ProductViewHolder(view);
                    }
                };
        activityDisplayCategoriesBinding.recyclerCategoryCategoriesDisplay.setAdapter(adapter);
        adapter.startListening();

    }
}
