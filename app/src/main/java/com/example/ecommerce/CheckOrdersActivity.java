package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.AdminOrders;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.databinding.ActivityCheckOrdersBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckOrdersActivity extends AppCompatActivity {
    ActivityCheckOrdersBinding activityCheckOrdersBinding;
    private RecyclerView orderList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCheckOrdersBinding = DataBindingUtil.setContentView(this,R.layout.activity_check_orders);

        //needs to only reference orders with users phone  number
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        orderList = findViewById(R.id.recycler_cart_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef.orderByChild("phone").equalTo(Prevalent.currentUserOnline.getPhoneNumber()), AdminOrders.class)
                        .build();
        FirebaseRecyclerAdapter<AdminOrders, OrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, OrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrdersViewHolder holder, int position, @NonNull AdminOrders model) {
                            String stringName, stringPhoneNumber, stringTotalPrice, stringDateTime, stringShippingAddress;
                            stringName = "Name: " + model.getName();
                            stringPhoneNumber ="Phone: " + model.getPhone();
                            stringTotalPrice = "Total Amount: $" + model.getTotalAmount();
                            stringDateTime = "Order at: " + model.getDate() + " " + model.getTime();
                            stringShippingAddress = "Shipping Address: " + model.getCity() + ", " + model.getAddress();
                            holder.userName.setText(stringName);
                            holder.userPhoneNumber.setText(stringPhoneNumber);
                            holder.userTotalPrice.setText(stringTotalPrice);
                            holder.userDateTime.setText(stringDateTime);
                            holder.userShippingAddress.setText(stringShippingAddress);
                            holder.showOrderButton.setOnClickListener(v -> {
                                String uID = getRef(position).getKey();
                                Intent intent = new Intent(CheckOrdersActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid",uID);
                                startActivity(intent);
                            });
                            holder.itemView.setOnClickListener(v -> {
                                CharSequence options1[] = new CharSequence[]
                                        {
                                                "Contact Seller",
                                                "Cancel"

                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(CheckOrdersActivity.this);
                                builder.setTitle("Contact Seller");
                                builder.setItems(options1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String uID = getRef(position).getKey();
                                        if(which == 0){
                                            Toast.makeText(CheckOrdersActivity.this, "This feature is not available", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                builder.show();
                            });
                    }

                    @NonNull
                    @Override
                    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new OrdersViewHolder(view);
                    }
                };

        orderList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class OrdersViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress;
        Button showOrderButton;
        OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.orders_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            showOrderButton = itemView.findViewById(R.id.button_products_order);

        }
    }
}
