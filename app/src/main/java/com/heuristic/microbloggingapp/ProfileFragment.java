package com.heuristic.microbloggingapp;

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

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileFragment extends Fragment {
    String clickedUserId = "";
    String clickedUserName = "";
    String currentUserId = "";
    private FragmentProfileBinding binding;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private PostAdapter adapter;

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

        adapter = new PostAdapter();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        }

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