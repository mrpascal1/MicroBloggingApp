package com.heuristic.microbloggingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {
 EditText username;
 EditText password;
 TextView nuser;
 MaterialButton logbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

         username = findViewById(R.id.username);
         password = findViewById(R.id.password);
         logbtn =   findViewById(R.id.logbtn);
         nuser = findViewById(R.id.nuser);
        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,pass;
                email = username.getText().toString();
                pass = password.getText().toString();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this,"ENTER EMAIL",Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(pass)){
                    Toast.makeText(LoginActivity.this,"ENTER PASSWORD",Toast.LENGTH_SHORT).show();
                }
            }
        });
        nuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                startActivity(intent);}
        });

}
}