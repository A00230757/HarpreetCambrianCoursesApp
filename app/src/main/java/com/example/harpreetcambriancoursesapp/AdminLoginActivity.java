package com.example.harpreetcambriancoursesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AdminLoginActivity extends AppCompatActivity {

    EditText edittextadminid, edittextadminpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        setTitle("ADMIN LOGIN");
        edittextadminid = (EditText) (findViewById(R.id.edittextadminid));
        edittextadminpassword= (EditText) (findViewById(R.id.edittextadminpassword));
    }
    public void login(View v ) {
        Intent in =new Intent(this,AdminHomeActivity.class);
        startActivity(in);
    }
}




