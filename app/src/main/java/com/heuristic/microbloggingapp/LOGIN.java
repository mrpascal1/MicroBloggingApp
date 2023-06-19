package com.heuristic.microbloggingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class LOGIN extends AppCompatActivity {
 EditText username;
 EditText password;
 TextView nuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         username = findViewById(R.id.username);
         password = findViewById(R.id.password);

        MaterialButton logbtn =  (MaterialButton) findViewById(R.id.logbtn);
        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            };
        });
}}