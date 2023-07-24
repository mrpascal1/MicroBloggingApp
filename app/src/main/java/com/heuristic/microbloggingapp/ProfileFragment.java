package com.heuristic.microbloggingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.heuristic.microbloggingapp.databinding.FragmentProfileBinding;
import com.heuristic.microbloggingapp.databinding.FragmentUsersBinding;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileFragment extends Fragment {
    String currentUserId = "";
    private FragmentProfileBinding binding;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private PostAdapter adapter;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        adapter = new PostAdapter();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        }
        getCurrentUserDetails();

        adapter.setOnUtilityButtonClickListener(new UtilityButtonClickListener() {
            @Override
            public void onLikeClicked(String postId) {
                setLikeInDatabase(postId);
            }
        }, firebaseUser.getUid());
        binding.profileRecyclerview.setAdapter(adapter);
        binding.profileRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.profileRecyclerview.setHasFixedSize(true);

        fetchClickedUserPosts();

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
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
    private void fetchClickedUserPosts() {

        databaseReference.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Posts> postsArrayList = new ArrayList<>();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Posts posts = snapshot1.getValue(Posts.class);
                    if (posts != null && posts.getUser_id().equals(currentUserId)) {
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

    private void getCurrentUserDetails() {
        databaseReference.child("Users").child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            setUserDetails(user.getUsername());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void setUserDetails(String username) {
        binding.usernameTv.setText(StringUtils.capitalize(username));
        binding.shortUsernameTv.setText(username.substring(0, 2).toUpperCase());
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

}