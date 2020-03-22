package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.databinding.ActivitySetQuestionBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SetQuestionActivity extends AppCompatActivity {
    ActivitySetQuestionBinding activitySetQuestionBinding;
    private String dbName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySetQuestionBinding = DataBindingUtil.setContentView(this, R.layout.activity_set_question);
        dbName = getIntent().getStringExtra("dbName");

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayPreviousAnswers();

        activitySetQuestionBinding.buttonVerifySet.setOnClickListener(view -> {
            setAnswers();

        });
    }

    private void setAnswers() {
        String question1 = activitySetQuestionBinding.editTextQuestion1Set.getText().toString();
        String question2 = activitySetQuestionBinding.editTextQuestion2Set.getText().toString();
        String answer1 = activitySetQuestionBinding.editTextAnswer1Set.getText().toString().toLowerCase();
        String answer2 = activitySetQuestionBinding.editTextAnswer2Set.getText().toString().toLowerCase();
        if (question1.equals("") || question2.equals("") || answer1.equals("") || answer2.equals("")) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(dbName).child(Prevalent.currentUserOnline.getPhoneNumber());
            HashMap<String, Object> userdatamap = new HashMap<>();
            userdatamap.put("question1", question1);
            userdatamap.put("question2", question2);
            userdatamap.put("answer1", answer1);
            userdatamap.put("answer2", answer2);
            ref.child("Security Questions").updateChildren(userdatamap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Your security questions have been set", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SetQuestionActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            });

        }
    }
    private void displayPreviousAnswers(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(dbName).child(Prevalent.currentUserOnline.getPhoneNumber());
        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String answer1 = dataSnapshot.child("answer1").getValue().toString();
                    String answer2 = dataSnapshot.child("answer2").getValue().toString();
                    String question1 = dataSnapshot.child("question1").getValue().toString();
                    String question2 = dataSnapshot.child("question2").getValue().toString();
                    activitySetQuestionBinding.editTextAnswer1Set.setText(answer1);
                    activitySetQuestionBinding.editTextAnswer2Set.setText(answer2);
                    activitySetQuestionBinding.editTextQuestion1Set.setText(question1);
                    activitySetQuestionBinding.editTextQuestion2Set.setText(question2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
