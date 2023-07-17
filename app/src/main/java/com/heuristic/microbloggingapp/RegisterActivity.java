package com.heuristic.microbloggingapp;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    private com.heuristic.microbloggingapp.databinding.ActivityRegisterBinding binding;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.heuristic.microbloggingapp.databinding.ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

      binding.SignUpbtn.setOnClickListener(v -> {
        String musername = Objects.requireNonNull(binding.username.getText()).toString().trim();
        String memail = Objects.requireNonNull(binding.email.getText()).toString().trim();
        String mpassword = Objects.requireNonNull(binding.password.getText()).toString().trim();

        if(!musername.equals("") && !memail.equals("") && mpassword.length()>6)
        {

            //Login
            fetchUserFromEmail(musername, memail, mpassword);

        }

        else
        {
            Toast.makeText(RegisterActivity.this,"Please enter proper values", Toast.LENGTH_SHORT).show();
        }
      });


      binding.existingUser.setOnClickListener(v -> {
          Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
          startActivity(intent);
          finish();
      });


    }

    private void fetchUserFromEmail(String username, String email, String password) {
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> users = task.getResult().getSignInMethods();
                        if (users != null)
                        if (!users.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "User Already exist", Toast.LENGTH_SHORT).show();
                        } else {
                            registerNewUser(username, email, password);
                        }
                    }
                }).addOnFailureListener(e -> {

                });
    }


    private void registerNewUser(String username, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        String userId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                        User user = new User(userId, username, email);
                        addUserToDB(user);
                        //Insert to db
                    } else {
                        Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void addUserToDB(User user) {
        databaseReference.child(user.getUserId()).setValue(user)
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Database failed", Toast.LENGTH_SHORT).show());
    }
}