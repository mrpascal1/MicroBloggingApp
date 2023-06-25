package com.heuristic.microbloggingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    private com.heuristic.microbloggingapp.databinding.ActivityRegisterBinding binding;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.heuristic.microbloggingapp.databinding.ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

      binding.SignUpbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            String musername = binding.username.getText().toString().trim();
            String memail = binding.email.getText().toString().trim();
            String mpassword = binding.password.getText().toString().trim();

            if(!musername.equals("") && !memail.equals("") && mpassword.length()>6)
            {

                //Login
                fetchUserFromEmail(musername, memail, mpassword);

            }

            else
            {
                Toast.makeText(RegisterActivity.this,"Please enter proper values", Toast.LENGTH_SHORT).show();
            }
          }
      });


      binding.existingUser.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
              startActivity(intent);
              finish();
          }
      });


    }

    private void fetchUserFromEmail(String username, String email, String password) {
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            List<String> users = task.getResult().getSignInMethods();
                            if (users != null)
                            if (!users.isEmpty()) {
                                Toast.makeText(RegisterActivity.this, "User Already exist", Toast.LENGTH_SHORT).show();
                            } else {
                                registerNewUser(username, email, password);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void registerNewUser(String username, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String userId = task.getResult().getUser().getUid();
                            User user = new User(userId, username, email);
                            addUserToDB(user);
                            //Insert to db
                        } else {
                            Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void addUserToDB(User user) {
        databaseReference.child(user.getUserId()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Database failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}