package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerce.Model.Categories;
import com.example.ecommerce.ViewHolder.CategoriesViewHolder;
import com.example.ecommerce.databinding.ActivityDisplaySubCategoriesBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class DisplaySubCategoriesActivity extends AppCompatActivity {
    ActivityDisplaySubCategoriesBinding activityDisplaySubCategoriesBinding;
    private ClickHandler clickHandler;
    RecyclerView.LayoutManager layoutManager;
    public String dbName;
    public String category;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDisplaySubCategoriesBinding = DataBindingUtil.setContentView(this,R.layout.activity_display_sub_categories);
        clickHandler = new ClickHandler(this);
        activityDisplaySubCategoriesBinding.setClickHandler(clickHandler);
        dbName = getIntent().getStringExtra("dbName");
        category = getIntent().getStringExtra("category");

        reference = FirebaseDatabase.getInstance().getReference().child("SubCategories").child(category);
        activityDisplaySubCategoriesBinding.recyclerCategorySubCategoriesDisplay.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        activityDisplaySubCategoriesBinding.recyclerCategorySubCategoriesDisplay.setLayoutManager(new GridLayoutManager(this,2));

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
        FirebaseRecyclerOptions<Categories> options = new FirebaseRecyclerOptions.Builder<Categories>()
                .setQuery(reference,Categories.class)
                .build();

        FirebaseRecyclerAdapter<Categories, CategoriesViewHolder> adapter =
                new FirebaseRecyclerAdapter<Categories, CategoriesViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position, @NonNull Categories model) {
                        holder.textViewCategoryName.setText(model.getCname());
                        Picasso.get().load(model.getImage()).into(holder.imageViewCategory);
                        holder.imageViewCategory.setOnClickListener(v -> {
                            Intent intent = new Intent(DisplaySubCategoriesActivity.this, DisplaySubCategoriesProductsActivity.class);
                            intent.putExtra("category", category);
                            intent.putExtra("subCategory", model.getCname());
                            intent.putExtra("dbName",getIntent().getStringExtra("dbName"));
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
                };
        activityDisplaySubCategoriesBinding.recyclerCategorySubCategoriesDisplay.setAdapter(adapter);
        adapter.startListening();
    }

}
