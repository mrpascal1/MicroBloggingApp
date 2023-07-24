package com.heuristic.microbloggingapp;

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
import com.heuristic.microbloggingapp.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private PostAdapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseUser firebaseUser;

    private List<Posts> postsList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        postsList = new ArrayList<>();
        adapter = new PostAdapter();

        adapter.setOnUtilityButtonClickListener(new UtilityButtonClickListener() {
            @Override
            public void onLikeClicked(String postId) {
                setLikeInDatabase(postId);
            }
        }, firebaseUser.getUid());

        binding.postRecyclerview.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        binding.postRecyclerview.setLayoutManager(linearLayoutManager);
        binding.postRecyclerview.setHasFixedSize(true);
        getPosts();
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

    private void getPosts() {
        databaseReference.child("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) { // https://.../Posts/List
                        postsList.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren()) {
                            Posts post = snapshot1.getValue(Posts.class);
                            if (post != null) {
                                postsList.add(post);
                            }
                        }
                        adapter.setData(postsList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("TAG", "onCancelled: " + error.getMessage());
                    }
                });
    }
}