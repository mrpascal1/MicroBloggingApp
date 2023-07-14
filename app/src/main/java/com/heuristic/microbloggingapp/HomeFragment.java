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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.heuristic.microbloggingapp.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private PostAdapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

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
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        postsList = new ArrayList<>();
        adapter = new PostAdapter();
        binding.postRecyclerview.setAdapter(adapter);
        binding.postRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.postRecyclerview.setHasFixedSize(true);
        getPosts();
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