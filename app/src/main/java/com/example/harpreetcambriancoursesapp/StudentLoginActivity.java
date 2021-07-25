package com.example.harpreetcambriancoursesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class StudentLoginActivity extends AppCompatActivity {

    EditText edittextstudentid, edittextstudentpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        setTitle("STUDENT LOGIN");
        edittextstudentid = (EditText) (findViewById(R.id.edittextstudentid));
        edittextstudentpassword= (EditText) (findViewById(R.id.edittextstudentpassword));
    }
    public void login(View v ) {
        Intent in =new Intent(this,StudentHomeActivity.class);
        startActivity(in);
    }
}


