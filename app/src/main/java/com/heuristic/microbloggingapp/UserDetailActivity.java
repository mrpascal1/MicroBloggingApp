package com.heuristic.microbloggingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.heuristic.microbloggingapp.databinding.ActivityUserDetailBinding;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class UserDetailActivity extends AppCompatActivity {

    private ActivityUserDetailBinding binding;

    String clickedUserId = "";
    String clickedUserName = "";
    String currentUserId = "";

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new PostAdapter();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        }

        Intent intent = getIntent();
        clickedUserId = intent.getStringExtra("userId");
        clickedUserName = intent.getStringExtra("username");

        setUserDetails();

        binding.postRecyclerview.setAdapter(adapter);
        binding.postRecyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.postRecyclerview.setHasFixedSize(true);


        adapter.setOnUtilityButtonClickListener(new UtilityButtonClickListener() {
            @Override
            public void onLikeClicked(String postId) {
                setLikeInDatabase(postId);
            }
        }, currentUserId);

        fetchClickedUserPosts();

        binding.backIv.setOnClickListener(v -> finish());

        binding.logoutIv.setOnClickListener(v -> new MaterialAlertDialogBuilder(v.getContext())
                .setTitle("Micro Blogging App")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    firebaseAuth.signOut();
                    openLoginActivity();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show());
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setUserDetails() {
        binding.usernameTv.setText(StringUtils.capitalize(clickedUserName));
        binding.shortUsernameTv.setText(clickedUserName.substring(0, 2).toUpperCase());
    }

    private void setLikeInDatabase(String postId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(firebaseUser.getUid(), "liked");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put(postId, "liked");

        DatabaseReference likesRef = databaseReference.child("Posts").child(postId).child("likes");
        likesRef.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            databaseReference.child("Users").child(firebaseUser.getUid()).child("liked").child(postId).removeValue();
                            likesRef.child(firebaseUser.getUid()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TAG", "onComplete: Removed child");
                                            } else  {
                                                Log.d("TAG", "onComplete: Error");
                                            }
                                        }
                                    });
                        } else {
                            likesRef.updateChildren(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                databaseReference.child("Users").child(firebaseUser.getUid())
                                                        .child("liked").child(postId).updateChildren(userMap);
                                                Log.d("TAG", "onComplete: Child child");

                                            } else {
                                                Log.d("TAG", "onComplete: Error");
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void fetchClickedUserPosts() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Posts> postsArrayList = new ArrayList<>();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Posts posts = snapshot1.getValue(Posts.class);
                    if (posts != null && posts.getUser_id().equals(clickedUserId)) {
                        postsArrayList.add(posts);
                    }
                }
                adapter.setData(postsArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}