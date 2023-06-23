package com.heuristic.microbloggingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText username,email,password;

    private View SignUpbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        email = findViewById(R.id.username);
        password = findViewById(R.id.username);

      SignUpbtn = findViewById(R.id.SignUpbtn);

      SignUpbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            String musername = username.getText().toString().trim();
            String memail = email.getText().toString().trim();
            String mpassword = password.getText().toString().trim();

            if(!musername.equals("") && !memail.equals("") && mpassword.length()>6)
            {
                //Login
            }

            else
            {
                Toast.makeText(RegisterActivity.this,"Please enter proper values", Toast.LENGTH_SHORT).show();
            }
          }
      });



    }
}