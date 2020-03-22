package com.example.ecommerce;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.databinding.ActivityResetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResetPasswordActivity extends AppCompatActivity {
    ActivityResetPasswordBinding activityResetPasswordBinding;
    private String dbName = "";
    String answer1DB;
    String answer2DB;
    String userPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityResetPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password);
        dbName = getIntent().getStringExtra("dbName");
        activityResetPasswordBinding.editTextPhoneReset.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) {
                    userPhoneNumber = activityResetPasswordBinding.editTextPhoneReset.getText().toString();
                    displayPreviousAnswers();
                    closeKeyboard();
                }
            }
        });

        activityResetPasswordBinding.buttonVerifyReset.setOnClickListener(view -> {
            verifyUser();
        });
        activityResetPasswordBinding.editTextPhoneReset.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_GO) {
                closeKeyboard();
                displayPreviousAnswers();

                handled = true;

            }
            return handled;
        });
    }


    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void displayPreviousAnswers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(dbName).child(activityResetPasswordBinding.editTextPhoneReset.getText().toString());
        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    activityResetPasswordBinding.editTextQuestion2Reset.setVisibility(View.VISIBLE);
                    activityResetPasswordBinding.editTextQuestion1Reset.setVisibility(View.VISIBLE);
                    activityResetPasswordBinding.buttonVerifyReset.setVisibility(View.VISIBLE);
                    activityResetPasswordBinding.textTitleReset.setVisibility(View.VISIBLE);
                    activityResetPasswordBinding.textQuestionOneReset.setVisibility(View.VISIBLE);
                    activityResetPasswordBinding.textQuestion2TwoReset.setVisibility(View.VISIBLE);
                    activityResetPasswordBinding.editTextAnswer1Reset.setVisibility(View.VISIBLE);
                    activityResetPasswordBinding.editTextAnswer2Reset.setVisibility(View.VISIBLE);

                    String question1 = dataSnapshot.child("question1").getValue().toString();
                    String question2 = dataSnapshot.child("question2").getValue().toString();
                    answer1DB = dataSnapshot.child("answer1").getValue().toString();
                    answer2DB = dataSnapshot.child("answer2").getValue().toString();
                    activityResetPasswordBinding.editTextQuestion1Reset.setText(question1);
                    activityResetPasswordBinding.editTextQuestion2Reset.setText(question2);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Security questions not set for this number", Toast.LENGTH_SHORT).show();
                    activityResetPasswordBinding.editTextQuestion2Reset.setVisibility(View.INVISIBLE);
                    activityResetPasswordBinding.editTextQuestion1Reset.setVisibility(View.INVISIBLE);
                    activityResetPasswordBinding.buttonVerifyReset.setVisibility(View.INVISIBLE);
                    activityResetPasswordBinding.textTitleReset.setVisibility(View.INVISIBLE);
                    activityResetPasswordBinding.textQuestionOneReset.setVisibility(View.INVISIBLE);
                    activityResetPasswordBinding.textQuestion2TwoReset.setVisibility(View.INVISIBLE);
                    activityResetPasswordBinding.editTextAnswer1Reset.setVisibility(View.INVISIBLE);
                    activityResetPasswordBinding.editTextAnswer2Reset.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void verifyUser() {
        //String phone = activityResetPasswordBinding.editTextPhoneReset.getText().toString();
        String answer1 = activityResetPasswordBinding.editTextAnswer1Reset.getText().toString();
        String answer2 = activityResetPasswordBinding.editTextAnswer2Reset.getText().toString();
        if (answer1.equals("") || answer2.equals("")) {
            Toast.makeText(this, "Please answer the security questions", Toast.LENGTH_SHORT).show();
        } else {
            if (answer1.equals(answer1DB) && answer2.equals(answer2DB)) {
                resetPasswordAlert();

            } else {
                Toast.makeText(this, "Incorrect Answer/s", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void resetPasswordAlert() {
        Toast.makeText(this, "Account Verified", Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
        builder.setTitle("New Password");

        LinearLayout layout = new LinearLayout(ResetPasswordActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText passwordNew = new EditText(ResetPasswordActivity.this);
        passwordNew.setHint("Enter your new Password");
        layout.addView(passwordNew);

        final EditText passwordConfirm = new EditText(ResetPasswordActivity.this);
        passwordConfirm.setHint("Please Confirm your password");
        layout.addView(passwordConfirm);

        builder.setView(layout);

        builder.setPositiveButton("Change Password", (dialog, which) -> {
            if(!passwordNew.getText().toString().equals("")){
                if(passwordNew.getText().toString().equals(passwordConfirm.getText().toString())){
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(dbName)
                            .child(userPhoneNumber).child("password");
                    ref.setValue(passwordNew.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    Toast.makeText(ResetPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            });

                }else{
                    Toast.makeText(ResetPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(ResetPasswordActivity.this, "New password can not be blank", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel()).show();
    }
}

