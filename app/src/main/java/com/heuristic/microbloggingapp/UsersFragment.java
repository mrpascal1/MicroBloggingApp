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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.heuristic.microbloggingapp.databinding.FragmentUsersBinding;
import com.heuristic.microbloggingapp.databinding.UserCardItemBinding;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private FragmentUsersBinding binding;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private UserCardAdapter adapter;
    private List<User> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        userList = new ArrayList<>();

        adapter = new UserCardAdapter();
        adapter.setUserCardClickListener(new UserCardClickListener() {
            @Override
            public void onClick(String userId, String username) {
                Intent intent = new Intent(requireContext(), UserDetailActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        binding.recyclerContact.setAdapter(adapter);
        binding.recyclerContact.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.recyclerContact.setHasFixedSize(true);

        getUsers();
    }


    private void getUsers() {
        databaseReference.child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) { // https://.../Posts/List
                        userList.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren()) {
                            User post = snapshot1.getValue(User.class);
                            if (post != null) {
                                userList.add(post);
                            }
                        }
                        adapter.setData(userList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("TAG", "onCancelled: " + error.getMessage());
                    }
                });
    }


}