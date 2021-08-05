package com.example.harpreetcambriancoursesapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StudentSelectNewCoursesActivity extends AppCompatActivity {

    ArrayList<course> arraylist_courses = new ArrayList<course>();
    ArrayList<studentselectcourseclass> arraylist_selectedcourses = new ArrayList<studentselectcourseclass>();
    myadapter mycustomadapter_courses;

    ListView listview_courses;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefcourse;
    DatabaseReference selectedmainrefcourse;
    DatabaseReference courseref;
    DatabaseReference selectedcourseref;
    FirebaseStorage firebaseStorage;
    StorageReference mainrefstorage;

    String course_photopath="/storage/emulated/0/Pictures/Title (30).jpg/d1";
    String selected_department="";
    String studentid="a00230757";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_select_new_courses);
        setTitle("Student Select Courses");

        listview_courses = (ListView) (findViewById(R.id.listview_courses));

        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefcourse = firebaseDatabase.getReference();
        courseref =mainrefcourse.child("courses");
        selectedmainrefcourse = firebaseDatabase.getReference();
        selectedcourseref =selectedmainrefcourse.child("selectedcourses");

        firebaseStorage = FirebaseStorage.getInstance();
        mainrefstorage = firebaseStorage.getReference();

        mycustomadapter_courses = new myadapter();
        listview_courses.setAdapter(mycustomadapter_courses);
        fetchCoursesFromFirebase("");
        alreadySelectedOrNot();

        listview_courses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), arraylist_courses.get(position).name+" "+arraylist_courses.get(position).description, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchCoursesFromFirebase(String department_selected){
        arraylist_courses.clear();
        courseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraylist_courses.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    course coursetemp = singlesnapshot.getValue(course.class);
                    try {
                        // if(coursetemp.under_dept.equals(department_selected)){
                        arraylist_courses.add(coursetemp);
                        //}
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                mycustomadapter_courses.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class myadapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return arraylist_courses.size();
        }

        @Override
        public Object getItem(int position) {
            return arraylist_courses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position*10;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null) {
                LayoutInflater l = LayoutInflater.from(getApplicationContext());
                convertView = l.inflate(R.layout.selectcourse_single_layout, parent, false);
            }
            TextView texview_course_name = (TextView) (convertView.findViewById(R.id.texview_course_name));
            TextView texview_course_description = (TextView) (convertView.findViewById(R.id.texview_course_description));
            TextView texview_course_photo = (TextView) (convertView.findViewById(R.id.texview_course_photo));
            CheckBox checkboxselectcourse =(CheckBox) (convertView.findViewById(R.id.checkboxselectcourse));
            ImageView imv1course =(ImageView) (convertView.findViewById(R.id.imv1course));

            course c = arraylist_courses.get(position);
            texview_course_name.setText("Name "+c.name);
            texview_course_description.setText("Description "+c.description);
            texview_course_photo.setText("path "+c.path);
            boolean f =test(c.coursecode);
            Toast.makeText(getApplicationContext(),arraylist_selectedcourses.size()+"",Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("MSSGGSS",f+"hello");
                }
            }).start();
            if(f){
                Toast.makeText(getApplicationContext(),f+"",Toast.LENGTH_SHORT).show();
                checkboxselectcourse.setChecked(true);
            }


            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference course_photo_reference = storageRef.child("courses"+c.path);
            course_photo_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
                    //do something with downloadurl
                    Picasso.with(StudentSelectNewCoursesActivity.this).load(downloadUrl).resize(200,200).into(imv1course);
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    checkboxselectcourse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {// checkbox listener to on / off background music
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked)
                            {
                                studentselectcourseclass obj = new studentselectcourseclass(c.coursecode,studentid);
                                DatabaseReference selected_course_reference = selectedcourseref.child(studentid+""+c.coursecode);
                                Log.d("MYMESSAGE",selected_course_reference.getKey());
                                selected_course_reference.setValue(obj);
                                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
                                alreadySelectedOrNot();

                            }
                            else
                            {
                                studentselectcourseclass obj = new studentselectcourseclass(c.coursecode,studentid);
                                DatabaseReference selected_course_reference = selectedcourseref.child(studentid+""+c.coursecode);
                                selected_course_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                                            singleSnapshot.getRef().removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                                Toast.makeText(getApplicationContext(),"UnSelected",Toast.LENGTH_SHORT).show();
                                alreadySelectedOrNot();
                            }
                        }
                    });
//                    btdelete.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            courseref.child(d.name).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
//                                        singleSnapshot.getRef().removeValue();
//                                       // deletefile(d.path);
//                                       // fetchCoursesFromFirebase(selected_department,selected_professor);
//                                    }
//                                }
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                }
//                            });
//                        }
//                    });

                }
            }).start();
            return convertView;
        }

    }

    public void  alreadySelectedOrNot(){
        arraylist_selectedcourses.clear();
        selectedcourseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    studentselectcourseclass obj = singlesnapshot.getValue(studentselectcourseclass.class);
                    try {
                        Log.d("MSSGGSS","fetchtest"+obj.coursecode+","+obj.studentid);
                        arraylist_selectedcourses.add(obj);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean test(String cc){
        boolean v= false;
        for (int i =0 ; i<arraylist_selectedcourses.size();i++){
            Log.d("MSSGGSS","hitop"+cc+studentid+"--"+arraylist_selectedcourses.get(i).studentid+"++"+arraylist_selectedcourses.get(i).coursecode);
            if (arraylist_selectedcourses.get(i).studentid.equals(studentid) & arraylist_selectedcourses.get(i).coursecode.equals(cc)){
                Log.d("MSSGGSS","hiinside"+cc+studentid+"--"+arraylist_selectedcourses.get(i).studentid+"++"+arraylist_selectedcourses.get(i).coursecode);
                v = true;
                break;
            }
        }
        return v;
    }



}
