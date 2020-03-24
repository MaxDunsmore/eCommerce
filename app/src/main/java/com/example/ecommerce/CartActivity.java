package com.example.ecommerce;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.ViewHolder.CartViewHolder;
import com.example.ecommerce.databinding.ActivityCartBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding activityCartBinding;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private double orderTotalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCartBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart);


        ClickHandler clickHandler = new ClickHandler(this);
        activityCartBinding.setClickHandler(clickHandler);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setLayoutManager(layoutManager);

        activityCartBinding.cartList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        activityCartBinding.cartList.setLayoutManager(layoutManager);

        activityCartBinding.nextProcessBtn.setOnClickListener(view -> {
            Toast.makeText(this, activityCartBinding.totalPrice.getText(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
            intent.putExtra("Total Price", String.valueOf(orderTotalPrice));
            startActivity(intent);
            finish();
        });


    }

    public class ClickHandler {
        private Context context;

        public ClickHandler(Context context) {
            this.context = context;
        }

        public void nextButtonCart(View view) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        CheckOrderState();
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(Prevalent.currentUserOnline.getPhoneNumber()).child("Products"), Cart.class)
                        .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                String stringQuantity = "Quantity: " + model.getQuantity();
                String stringPrice = "Price: " + model.getPrice();
                String stringName = model.getPname();
                holder.textProductQuantity.setText(stringQuantity);
                holder.textProductPrice.setText(stringPrice);
                holder.textProductName.setText(stringName);
                Picasso.get().load(model.getImage()).into(holder.imageView);
                setTotalPrice(model);


                holder.itemView.setOnClickListener(view -> {
                    CharSequence[] options = new CharSequence[]{
                            "Edit",
                            "Remove"
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setTitle("Cart Options:");
                    builder.setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }
                        if (which == 1) {
                            cartListRef.child("User View")
                                    .child(Prevalent.currentUserOnline.getPhoneNumber())
                                    .child("Products")
                                    .child(model.getPid())
                                    .removeValue();
                            //possibly remove code
                            cartListRef.child("Admin View")
                                    .child(Prevalent.currentUserOnline.getPhoneNumber())
                                    .child("Products")
                                    .child(model.getPid())
                                    .removeValue()
                                    .addOnCompleteListener(task -> {
                                        Toast.makeText(CartActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();

                                        String price = model.getPrice();
                                        double priceInt = Double.parseDouble(price.replaceAll("[^\\d.-]", ""));
                                        double quantityInt = Double.parseDouble(model.getQuantity());
                                        double productTotalPrice = priceInt * quantityInt;
                                        orderTotalPrice = orderTotalPrice - productTotalPrice;
                                        String orderTotalPriceString = "Total Price: $" + orderTotalPrice;
                                        activityCartBinding.totalPrice.setText(orderTotalPriceString);
                                    });
                        }
                    });
                    builder.show();

                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                return new CartViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckOrderState(){
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentUserOnline.getPhoneNumber());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("state").getValue().toString();

                    if(shippingState.equals("shipped")){
                        String shippedMessage = "Dear " + userName + "\n order is shipped successfully";
                        activityCartBinding.totalPrice.setText(shippedMessage);
                        recyclerView.setVisibility(View.GONE);

                        activityCartBinding.textMsg1Cart.setVisibility(View.VISIBLE);
                        activityCartBinding.textMsg1Cart.setText("Congratulations your order has been Shipped successfully. Your order will be at your door step son");
                        activityCartBinding.nextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "You can purchase more products once your received your first order", Toast.LENGTH_SHORT).show();
                    }else if(shippingState.equals("not shipped")){
                        activityCartBinding.totalPrice.setText("Order Status: Not shipped");
                        recyclerView.setVisibility(View.GONE);

                        activityCartBinding.textMsg1Cart.setVisibility(View.VISIBLE);
                        activityCartBinding.nextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "You can purchase more products once your received your first order", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setTotalPrice(@NonNull Cart model) {
        String price = "";
        price = model.getPrice();
        double priceInt = 0;
        double quantityInt = 0;
        if (!price.isEmpty()) {
            priceInt = Double.parseDouble(price.replaceAll("[^\\d.-]", ""));
        }
        if (!model.getQuantity().isEmpty()) {
            quantityInt = Double.parseDouble(model.getQuantity());
        }


        double productTotalPrice = priceInt * quantityInt;
        orderTotalPrice = orderTotalPrice + productTotalPrice;
        final String totalPriceString = "Total Price: $" + orderTotalPrice;
        activityCartBinding.totalPrice.setText(totalPriceString);
    }
}
