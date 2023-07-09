package com.heuristic.microbloggingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class UserCardMainActivity extends AppCompatActivity {
    ArrayList<UserCard> array=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_card_main);
        RecyclerView recyclerView=findViewById(R.id.recyclerContact);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        UserCard cm=new UserCard("Uzma Shaikh","123567890");


        array.add(new UserCard("Zoya M","2345678901"));
        array.add(new UserCard("Hafsa Welder","3456789012"));
        array.add(new UserCard("Sadaf Shaikh","4567890123"));
        array.add(new UserCard("Rachna Gallepeli","5678901234"));
        array.add(new UserCard("Geetanjali Khamkhar","6789012345"));
        array.add(new UserCard("Shrutia Sabale","7890123456"));
        array.add(new UserCard("Riya Deshmukh","8901234567"));
        array.add(new UserCard("Mantasha Mohd","9012345678"));
        array.add(new UserCard("Umme Haani","0123456789"));
        array.add(new UserCard("Mehwish","9876543210"));
        array.add(new UserCard("Ansari Midhat","8765432109"));
        array.add(new UserCard("Shaikh Afsha","7654321098"));
        array.add(new UserCard("Yusra Baig","6543210987"));
        array.add(new UserCard("Arshiya Shaikh","5432109876"));
        array.add(new UserCard("Aqsa Qureshi","4321098765"));
        array.add(new UserCard("Alvia Khan","3210987654"));
        array.add(new UserCard("Tuba Khan","2109876543"));

        UserCardAdapter adapter=new UserCardAdapter(this,array);
        adapter.setData(array);
        recyclerView.setAdapter(adapter);


    }

}
