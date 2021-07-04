package com.example.harpreetcambriancoursesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainInterface extends AppCompatActivity {

    ImageView imv1, imv2, imv3;

    int a[] = {R.drawable.admin, R.drawable.professor, R.drawable.student};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_interface);
        setTitle("MAIN INTERFACE");
        imv1 = (ImageView) (findViewById(R.id.imv1));
        imv2 = (ImageView) (findViewById(R.id.imv2));
        imv3 = (ImageView) (findViewById(R.id.imv3));
        imv1.setImageResource(a[0]);
        imv2.setImageResource(a[1]);
        imv3.setImageResource(a[2]);
    }
    public void login(View v ) {
        if( v.getId() == R.id.imv1){
            Intent in =new Intent(this,AddDepartmentActivity.class);
            startActivity(in);
        }
        else if( v.getId() == R.id.imv2){
            Intent in =new Intent(this,AddCoursesActivity.class);
            startActivity(in);
        }
        else if( v.getId() == R.id.imv3){
            Toast.makeText(getApplicationContext(),"Student Login",Toast.LENGTH_SHORT).show();
        }
        else{}
    }
}




