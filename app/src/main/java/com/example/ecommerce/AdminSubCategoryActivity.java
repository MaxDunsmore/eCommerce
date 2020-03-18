package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Model.Categories;
import com.example.ecommerce.ViewHolder.CategoriesViewHolder;
import com.example.ecommerce.databinding.ActivityAdminSubCategoryBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminSubCategoryActivity extends AppCompatActivity {

    private DatabaseReference categoriesReference;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ActivityAdminSubCategoryBinding activityAdminSubCategoryBinding;
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminSubCategoryBinding = DataBindingUtil.setContentView(this,R.layout.activity_admin_sub_category);

        categoryName = getIntent().getStringExtra("category");

        categoriesReference = FirebaseDatabase.getInstance().getReference().child("SubCategories").child(categoryName);
        recyclerView = findViewById(R.id.recycler_sub_category_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        activityAdminSubCategoryBinding.fabAddSubCategory.setOnClickListener(view->{
            Intent intent = new Intent(AdminSubCategoryActivity.this, AdminAddSubCategoryActivity.class);
            intent.putExtra("category", categoryName);
            startActivity(intent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Categories> options = new FirebaseRecyclerOptions.Builder<Categories>()
                .setQuery(categoriesReference, Categories.class)
                .build();

        FirebaseRecyclerAdapter<Categories, CategoriesViewHolder> adapter =
                new FirebaseRecyclerAdapter<Categories, CategoriesViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position, @NonNull Categories model) {
                        holder.textViewCategoryName.setText(model.getCname());
                        Picasso.get().load(model.getImage()).into(holder.imageViewCategory);
                        holder.imageViewCategory.setOnClickListener(v -> {
                            Intent intent = new Intent(AdminSubCategoryActivity.this, AdminAddNewProductActivity.class);
                            String categoryName = getIntent().getStringExtra("category") +  model.getCname();
                            intent.putExtra("category", categoryName);
                            startActivity(intent);
                        });
                    }

                    @NonNull
                    @Override
                    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_layout, parent, false);
                        CategoriesViewHolder holder = new CategoriesViewHolder(view);
                        return holder;
                    }
                };        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
