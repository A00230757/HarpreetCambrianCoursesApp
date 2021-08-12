package com.example.harpreetcambriancoursesapp;



import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class StudentEditProfileActivity extends AppCompatActivity {

    String studentid="a00230757";
    String path="";
    String department ="";

    TextView textviewstudentid,textviewunderdepartment;
    EditText edittextemail,edittextmobile,edittextname;
    ImageView imageviewstudentimage;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefstudent;
    DatabaseReference studentref;
    FirebaseStorage firebaseStorage;
    StorageReference mainrefstorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit_profile);
        setTitle("STUDENT EDIT PROFILE");
        textviewstudentid = (TextView)(findViewById(R.id.textviewstudentid));
        textviewunderdepartment = (TextView)(findViewById(R.id.textviewunderdepartment));
        edittextemail = (EditText) (findViewById(R.id.edittextemail));
        edittextmobile = (EditText) (findViewById(R.id.edittextmobile));
        edittextname = (EditText) (findViewById(R.id.edittextname));
        imageviewstudentimage =(ImageView)(findViewById(R.id.imageviewstudentimage));


        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefstudent = firebaseDatabase.getReference();
        studentref =mainrefstudent.child("students");

        firebaseStorage = FirebaseStorage.getInstance();
        mainrefstorage = firebaseStorage.getReference();
        fetchStudentData();
    }

    public void fetchStudentData()
    {
        try{
            DatabaseReference st1 = studentref.child(studentid);
            st1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Extract value from dataSnapShot and Convert it to Java Object
                    student stTemp = dataSnapshot.getValue(student.class);

                    textviewstudentid.setText("ID : "+stTemp.studentid);
                    textviewunderdepartment.setText("Dept : "+ stTemp.under_dept);
                    edittextemail.setText(stTemp.email);
                    edittextmobile.setText(stTemp.mobile);
                    edittextname.setText(stTemp.name);
                    path=stTemp.path;
                    department=stTemp.under_dept;
                    Log.d("MSSGGPHOTO",stTemp.path+"");

                    StorageReference student_photo_reference = mainrefstorage.child("students"+stTemp.path);
                    student_photo_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri downloadUrl)
                        {
                            //do something with downloadurl
                            Picasso.with(StudentEditProfileActivity.this).load(downloadUrl).resize(200,200).into(imageviewstudentimage);
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void updateStudentInfo(View v)
    {
        try{
            String student_name=edittextname.getText().toString();
            String student_mobile = edittextmobile.getText().toString();
            String student_email = edittextemail.getText().toString();
            if(student_email.isEmpty()){
                Toast.makeText(this, "Enter Student Email", Toast.LENGTH_SHORT).show();
            }
            else if(student_mobile.isEmpty()){
                Toast.makeText(this, "Enter Student Mobile", Toast.LENGTH_SHORT).show();
            }
            else if(student_name.isEmpty()){
                Toast.makeText(this, "Enter Student Name", Toast.LENGTH_SHORT).show();
            }
            else{
                student stnew = new student(studentid,student_name,student_email,path,student_mobile,department);
                DatabaseReference st4 = studentref.child(studentid);
                st4.setValue(stnew);
                Toast.makeText(this, "Student Records Updated", Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}