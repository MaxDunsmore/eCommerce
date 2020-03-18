package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.databinding.ActivityConfirmFinalOrderBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    ActivityConfirmFinalOrderBinding activityConfirmFinalOrderBinding;
    private ClickHandler clickHandler;
    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityConfirmFinalOrderBinding = DataBindingUtil.setContentView(this,R.layout.activity_confirm_final_order);
        totalAmount = getIntent().getStringExtra("Total Price");
        totalAmount.replaceAll("[^\\d.-]", "");

        clickHandler = new ClickHandler(this);
        activityConfirmFinalOrderBinding.setClickHandler(clickHandler);
        activityConfirmFinalOrderBinding.buttonConfirmFinalOrder.setOnClickListener(view ->{
            Check();
        });
    }

    private void Check() {
        if(TextUtils.isEmpty(activityConfirmFinalOrderBinding.editTextNameFinalOrder.getText().toString())){
            Toast.makeText(this, "Please Provide your full name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(activityConfirmFinalOrderBinding.editTextPhoneNumberFinalOrder.getText().toString()) || Integer.parseInt(activityConfirmFinalOrderBinding.editTextPhoneNumberFinalOrder.getText().toString()) < 10){
            Toast.makeText(this, "Please Provide your phone number.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(activityConfirmFinalOrderBinding.editTextAddressFinalOrder.getText().toString())){
            Toast.makeText(this, "Please Provide your address.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(activityConfirmFinalOrderBinding.editTextCityFinalOrder.getText().toString())){
            Toast.makeText(this, "Please Provide your city name", Toast.LENGTH_SHORT).show();
        }else{
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
        final String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("h:mm a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentUserOnline.getPhoneNumber());

        final HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", activityConfirmFinalOrderBinding.editTextNameFinalOrder.getText().toString());
        ordersMap.put("phone", activityConfirmFinalOrderBinding.editTextPhoneNumberFinalOrder.getText().toString());
        ordersMap.put("address", activityConfirmFinalOrderBinding.editTextAddressFinalOrder.getText().toString());
        ordersMap.put("city", activityConfirmFinalOrderBinding.editTextCityFinalOrder.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "not shipped");
        ordersRef.updateChildren(ordersMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseDatabase.getInstance().getReference()
                        .child("Cart List")
                        .child("User View")
                        .child(Prevalent.currentUserOnline.getPhoneNumber())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ConfirmFinalOrderActivity.this, "Your order has been placed", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);// stop user going back
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });
    }

    public class ClickHandler {
        private Context context;

        public ClickHandler(Context context) {
            this.context = context;
        }
    }
}
