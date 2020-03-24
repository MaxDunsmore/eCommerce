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
import com.example.ecommerce.databinding.ActivityAdminCategoryBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class AdminHome extends AppCompatActivity {
    ActivityAdminCategoryBinding activityAdminCategoryBinding;
    private ClickHandler clickHandler;
    private DatabaseReference categoriesReference;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminCategoryBinding = DataBindingUtil.setContentView(this,R.layout.activity_admin_category);
        Paper.init(this);

        categoriesReference = FirebaseDatabase.getInstance().getReference().child("Categories");
        recyclerView = findViewById(R.id.recycler_category_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        activityAdminCategoryBinding.buttonMaintainAdmin.setOnClickListener(view ->{
            Intent intent = new Intent(AdminHome.this,HomeActivity.class);
            intent.putExtra("Admin","Admin");
            intent.putExtra("dbName","Admin");
            startActivity(intent);
        });

        activityAdminCategoryBinding.buttonLogoutAdmin.setOnClickListener(v -> {
            Paper.book().destroy();
            Intent intent = new Intent(AdminHome.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        activityAdminCategoryBinding.buttonCheckOrdersAdmin.setOnClickListener(view ->{
            Intent intent = new Intent(AdminHome.this,AdminNewOrdersActivity.class);
            intent.putExtra("dbName","Admins");
            startActivity(intent);
        });
        activityAdminCategoryBinding.fabAddCategory.setOnClickListener(view -> {
            Intent intent = new Intent(AdminHome.this,AdminAddNewCategoryActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Categories> options = new FirebaseRecyclerOptions.Builder<Categories>()
                .setQuery(categoriesReference,Categories.class)
                .build();

        FirebaseRecyclerAdapter<Categories, CategoriesViewHolder> adapter =
                new FirebaseRecyclerAdapter<Categories, CategoriesViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position, @NonNull Categories model) {
                        holder.textViewCategoryName.setText(model.getCname());
                        Picasso.get().load(model.getImage()).into(holder.imageViewCategory);
                        holder.imageViewCategory.setOnClickListener(v -> {
                            Intent intent = new Intent(AdminHome.this, AdminSubCategoryActivity.class);
                            intent.putExtra("category", model.getCname());
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
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public class ClickHandler {
        private Context context;

        public ClickHandler(Context context) {
            this.context = context;
        }


    }

    private void getCategory(String categoryName) {
        Intent intent = new Intent(AdminHome.this, AdminAddNewProductActivity.class);
        intent.putExtra("category", categoryName);
        startActivity(intent);
    }
}
