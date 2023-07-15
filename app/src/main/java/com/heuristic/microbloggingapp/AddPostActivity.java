package com.heuristic.microbloggingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.heuristic.microbloggingapp.databinding.ActivityAddPostBinding;

import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    private ActivityAddPostBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String username = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        setBackPressListener();

        setPostButtonListener();
    }

    private void setBackPressListener() {
        binding.backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setPostButtonListener() {
        binding.postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postDescription = binding.postDescriptionEt.getText().toString().trim();
                if (!postDescription.equals("")) {
                    // Post
                    String userId = firebaseUser.getUid();
                    Posts posts = new Posts("", userId, "", new HashMap<>(), postDescription, "");
                    fetchUserNameFromUserId(userId, posts);
                }
            }
        });
    }

    private void fetchUserNameFromUserId(String userId, Posts posts) {
        databaseReference.child("Users").child(userId) // https://www.firebasedb.com/Users/
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) { // DataSnapshot={user_id="fdkjfdsjfdifgjfdkfd", username="ansarishahid", email="name@email.com"}
                        User user = snapshot.getValue(User.class); // User(fdkjfdsjfdifgjfdkfd, ansarishahid, name@email.com)
                        if (user != null) {
                            username = user.getUsername();
                            String timestamp = String.valueOf(System.currentTimeMillis());

                            posts.setUser_name(username);
                            posts.setPost_id(timestamp);
                            posts.setTimestamp(timestamp);

                            addPostToFirebaseDB(posts, timestamp);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addPostToFirebaseDB(Posts posts, String timestamp) {
        // Post add code..
        // Posts/128936472/{post}
        databaseReference.child("Posts").child(timestamp).setValue(posts) // https://www.firebasedb.com/Posts/1562153524/{postId="12783647", user_name:"", user_id, likes}
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddPostActivity.this, "Posted...", Toast.LENGTH_SHORT).show();
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
}