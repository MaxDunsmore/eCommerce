package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.rey.material.widget.CheckBox;
import android.widget.Toast;

import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.databinding.ActivityLoginBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding activityLoginBinding;
    public User user;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private CheckBox checkBoxRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loadingBar = new ProgressDialog(this);
        user = new User();
        activityLoginBinding.setUser(user);
        ClickHandler clickHandler = new ClickHandler(this);
        activityLoginBinding.setClickHandler(clickHandler);
        activityLoginBinding.forgotPasswordLink.setOnClickListener(view ->{
            Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
            intent.putExtra("dbName",parentDbName);
            startActivity(intent);
        });
        checkBoxRememberMe = findViewById(R.id.remember_me);
        Paper.init(this);


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    public class ClickHandler {
        private Context context;

        public ClickHandler(Context context) {
            this.context = context;
        }

        public void adminLink(View view){
            activityLoginBinding.mainLoginButton.setText("Login Admin");
            activityLoginBinding.adminPanelLink.setVisibility(View.INVISIBLE);
            activityLoginBinding.notAdminPanelLink.setVisibility(View.VISIBLE);
            parentDbName = "Admins";
        }
        public void notAdminLink(View view){
            activityLoginBinding.mainLoginButton.setText("Login");
            activityLoginBinding.adminPanelLink.setVisibility(View.VISIBLE);
            activityLoginBinding.notAdminPanelLink.setVisibility(View.INVISIBLE);
            parentDbName = "Users";
        }

        public void loginButtonClick(View view) {
            loginUser();
        }
    }

    private void loginUser() {
        if (user.getPhoneNumber() == null || activityLoginBinding.getUser().getPhoneNumber().isEmpty()) {
            Toast.makeText(this, "Please enter your phone number ....", Toast.LENGTH_SHORT).show();
        } else if (user.getPassword() == null || activityLoginBinding.getUser().getPassword().isEmpty()) {
            Toast.makeText(this, "Please enter your password ....", Toast.LENGTH_SHORT).show();
        } else if (user.getPhoneNumber().length() < 10) {
            Toast.makeText(this, "Your phone number does not have enough digits", Toast.LENGTH_SHORT).show();
        } else {
            String password = user.getPassword();
            String phoneNumber = user.getPhoneNumber();
            loadingBar.setTitle("Account Login");
            loadingBar.setMessage("Logging in, Please wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessToAccount(phoneNumber,password);
        }
    }
    public void AllowAccessToAccount(String phoneNumber,String password){
        if(checkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phoneNumber);
            Paper.book().write(Prevalent.UserPasswordKey,password);
            Paper.book().write(Prevalent.UserAccountType,parentDbName);
        }
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(phoneNumber).exists()){
                    User userData = dataSnapshot.child(parentDbName).child(phoneNumber).getValue(User.class);
                    if (userData.getPhoneNumber().equals(phoneNumber)){
                        if (userData.getPassword().equals(password)){
                           if(parentDbName.equals("Admins")){
                               Toast.makeText(LoginActivity.this,"Welcome Admin, you are currently being logged in",Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();
                               Intent intent = new Intent(LoginActivity.this, AdminHome.class);
                               Prevalent.currentUserOnline = userData;
                               startActivity(intent);
                           }
                           else if (parentDbName.equals("Users")){
                               Toast.makeText(LoginActivity.this,"Welcome, you are currently being logged in",Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();
                               Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                               Prevalent.currentUserOnline = userData;
                               startActivity(intent);
                           }
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"Incorrect Password, please try again",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                }else {
                    Toast.makeText(LoginActivity.this,"Account with this phone number: " + phoneNumber + " does not exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
