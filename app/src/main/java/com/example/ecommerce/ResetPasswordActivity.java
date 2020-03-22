package com.example.ecommerce;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ecommerce.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {
    private String check = "";
    private String phoneNumber = "";
    ActivityResetPasswordBinding activityResetPasswordBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityResetPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password);
        activityResetPasswordBinding.buttonVerifyReset.setOnClickListener(view -> {

        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}

