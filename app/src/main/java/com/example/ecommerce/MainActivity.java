package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;
    private ClickHandler clickHandler;
    String parentDbName = "Users";
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        clickHandler = new ClickHandler(this);
        mainBinding.setClickHandler(clickHandler);
        loadingBar = new ProgressDialog(this);
        Paper.init(this);
    }
    public class ClickHandler{
        private Context context;
        public ClickHandler(Context context){
            this.context = context;
        }
        public void loginButtonClick(View view){
            checkRememberBox();
        }
        public void joinNowButtonClick(View view){
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
        public void checkRememberBox(){
            String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
            String UserPasswordKey =  Paper.book().read(Prevalent.UserPasswordKey);
            parentDbName = Paper.book().read(Prevalent.UserAccountType);
            if(UserPhoneKey != "" && UserPasswordKey != ""){
                if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){
                    loadingBar.setTitle("Already Logged in");
                    loadingBar.setMessage("Please Wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    AllowAccess(UserPhoneKey,UserPasswordKey);
                }else{
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    private void AllowAccess(final String phoneNumber, final String password) {
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(phoneNumber).exists()){
                    User userData = dataSnapshot.child(parentDbName).child(phoneNumber).getValue(User.class);
                    if (userData.getPhoneNumber().equals(phoneNumber)){
                        if (userData.getPassword().equals(password)){
                            loadingBar.dismiss();
                            if(parentDbName.equals("Users")){
                                Toast.makeText(MainActivity.this,"Welcome, Logged in Successfully",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                                Prevalent.currentUserOnline = userData;
                                startActivity(intent);
                            }else if(parentDbName.equals("Admins")){
                                Toast.makeText(MainActivity.this,"Welcome Admin, Logged in Successfully",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this,AdminCategoryActivity.class);
                                Prevalent.currentUserOnline = userData;
                                startActivity(intent);
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this,"Wrong Password",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                }else {
                    Toast.makeText(MainActivity.this,"Account with phone number:  " + phoneNumber + " does not exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
