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

public class AdminCategoryActivity extends AppCompatActivity {
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
            Intent intent = new Intent(AdminCategoryActivity.this,HomeActivity.class);
            intent.putExtra("Admin","Admin");
            intent.putExtra("dbName","Admin");
            startActivity(intent);
        });

        activityAdminCategoryBinding.buttonLogoutAdmin.setOnClickListener(v -> {
            Paper.book().destroy();
            Intent intent = new Intent(AdminCategoryActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        activityAdminCategoryBinding.buttonCheckOrdersAdmin.setOnClickListener(view ->{
            Intent intent = new Intent(AdminCategoryActivity.this,AdminNewOrdersActivity.class);
            startActivity(intent);
        });
        activityAdminCategoryBinding.fabAddCategory.setOnClickListener(view -> {
            Intent intent = new Intent(AdminCategoryActivity.this,AdminAddNewCategoryActivity.class);
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
                            Intent intent = new Intent(AdminCategoryActivity.this, AdminSubCategoryActivity.class);
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
        public void dressF(View view){
            getCategory("dressFashionF");
        }
        public void jacketF(View view){
            getCategory("jacketsFashionF");
        }
        public void pantsF(View view){
            getCategory("pantsFashionF");
        }
        public void shirtF(View view){
            getCategory("shirtsFashionF");
        }
        public void shoesF(View view){
            getCategory("shoesFashionF");
        }
        public void shortsF(View view){
            getCategory("shortsFashionF");
        }
        public void socksF(View view){
            getCategory("socksFashionF");
        }

        public void jacketM(View view){
            getCategory("jacketsFashionM");
        }
        public void shirtM(View view){
            getCategory("shirtsFashionM");
        }
        public void shortsM(View view){
            getCategory("shortsFashionM");
        }
        public void pantsM(View view){
            getCategory("pantsFashionM");
        }
        public void shoesM(View view){
            getCategory("shoesFashionM");
        }
        public void socksM(View view){
            getCategory("socksFashionM");
        }
        public void beltsM(View view){
            getCategory("beltsFashionM");
        }

        public void computer(View view){
            getCategory("computerElectronics");
        }
        public void gaming(View view){
            getCategory("GamingElectronics");
        }
        public void carAccessories(View view){
            getCategory("carAccessoriesElectronics");
        }
        public void audio(View view){
            getCategory("audioElectronics");
        }
        public void phone(View view){
            getCategory("phoneElectronics");
        }
        public void camera(View view){
            getCategory("cameraElectronics");
        }
        public void smartHome(View view){
            getCategory("smartHomeElectronics");
        }
        public void tv(View view){
            getCategory("tvElectronics");
        }

        public void furniture(View view){
            getCategory("furnitureHomeAndGarden");
        }
        public void tool(View view){
            getCategory("toolsHomeAndGarden");
        }
        public void garden(View view){
            getCategory("gardenHomeAndGarden");
        }
        public void smallAppliances(View view){
            getCategory("smallAppliancesHomeAndGarden");
        }
        public void largeAppliances(View view){
            getCategory("largeAppliancesHomeAndGarden");
        }
        public void outsideTools(View view){
            getCategory("outsideToolsHomeAndGarden");
        }
        public void vacuum(View view){
            getCategory("vacuumHomeAndGarden");
        }

        public void outdoorSports(View view){
            getCategory("outdoorSportsSportingGoods");
        }
        public void teamSports(View view){
            getCategory("teamSportsSportingGoods");
        }
        public void cycling(View view){
            getCategory("cyclingSportingGoods");
        }
        public void inDoorSports(View view){
            getCategory("billiardsSportingGoods");
        }
        public void fishing(View view){
            getCategory("fishingSportingGoods");
        }
        public void golf(View view){
            getCategory("golfSportingGoods");
        }
        public void hunting(View view){
            getCategory("huntingSportingGoods");
        }
        public void waterSports(View view){
            getCategory("waterSportsSportingGoods");
        }
        public void fitnessYoga(View view){
            getCategory("fitnessYogaSportingGoods");
        }
    }

    private void getCategory(String categoryName) {
        Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
        intent.putExtra("category", categoryName);
        startActivity(intent);
    }
}
