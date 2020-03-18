package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ecommerce.Model.AdminOrders;
import com.example.ecommerce.databinding.ActivityAdminAddNewProductBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity {
    ActivityAdminAddNewProductBinding activityAdminAddNewProductBinding;
    private RecyclerView orderList;
    private DatabaseReference ordersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminAddNewProductBinding = DataBindingUtil.setContentView(this,R.layout.activity_admin_new_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        orderList = findViewById(R.id.recycler_cart_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef, AdminOrders.class)
                .build();
        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, int position, @NonNull AdminOrders model) {
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
                            Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                            intent.putExtra("uid",uID);
                            startActivity(intent);
                        });
                        holder.itemView.setOnClickListener(v -> {
                            CharSequence options1[] = new CharSequence[]
                                    {
                                            "Confirm Order",
                                            "Order Shipped",
                                            "Cancel"

                                    };

                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                            builder.setTitle("Have you shipped this product");
                            builder.setItems(options1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String uID = getRef(position).getKey();
                                    if(which == 0){
                                        ordersRef.child(uID).child("state").setValue("confirmed");
                                    }else if(which == 1){
                                        RemoveOrder(uID);
                                    }
                                    else{
                                        finish();
                                    }
                                }
                            });
                            builder.show();
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };

        orderList.setAdapter(adapter);
        adapter.startListening();
    }

    private void RemoveOrder(String uID) {
        ordersRef.child(uID).removeValue();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress;
        Button showOrderButton;
        AdminOrdersViewHolder(@NonNull View itemView) {
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
