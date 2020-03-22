package com.example.ecommerce;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ecommerce.databinding.ActivityResetPasswordBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResetPasswordActivity extends AppCompatActivity {
    ActivityResetPasswordBinding activityResetPasswordBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityResetPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password);
        activityResetPasswordBinding.editTextPhoneReset.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10){
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(activityResetPasswordBinding.editTextPhoneReset.getText().toString());
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


    @Override
    protected void onStart() {
        super.onStart();

    }

    private void verifyUser() {
        String phone = activityResetPasswordBinding.editTextPhoneReset.getText().toString();
    }
}

