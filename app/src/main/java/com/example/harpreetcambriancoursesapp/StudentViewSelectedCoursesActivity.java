package com.example.harpreetcambriancoursesapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class StudentViewSelectedCoursesActivity extends AppCompatActivity {


    String studentid = "a00230757";
    ArrayList<selectedcourse> arraylist_courses = new ArrayList<selectedcourse>();
    myadapter mycustomadapter_courses;

    ListView listview_courses;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefcourse;
    DatabaseReference courseref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view_selected_courses);

        listview_courses = (ListView) (findViewById(R.id.listview_selectedcourses));

        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefcourse = firebaseDatabase.getReference();
        courseref = mainrefcourse.child("selectedcourses");
        mycustomadapter_courses = new myadapter();
        listview_courses.setAdapter(mycustomadapter_courses);
        fetchCoursesFromFirebase();

        listview_courses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), arraylist_courses.get(position).coursecode + " " + arraylist_courses.get(position).studentid, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchCoursesFromFirebase() {
        arraylist_courses.clear();
        courseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraylist_courses.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for (DataSnapshot singlesnapshot : dataSnapshot.getChildren()) {
                    selectedcourse coursetemp = singlesnapshot.getValue(selectedcourse.class);
                    try {
                        if (coursetemp.studentid.equals(studentid)) {
                            arraylist_courses.add(coursetemp);
                        }
                    } catch (Exception ex) {
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


    class myadapter extends BaseAdapter {
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
            return position * 10;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater l = LayoutInflater.from(getApplicationContext());
                convertView = l.inflate(R.layout.selected_courses_single_layout, parent, false);
            }
            TextView texview_course_name = (TextView) (convertView.findViewById(R.id.texview_course_name));
            Button btdelete = (Button) (convertView.findViewById(R.id.btdeletecourse));

            selectedcourse d = arraylist_courses.get(position);
            texview_course_name.setText("Name " + d.coursecode);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    btdelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent myIntent = new Intent(StudentViewSelectedCoursesActivity.this, SelectedCourseDetailActivity.class);
                            myIntent.putExtra("coursecode", d.coursecode); //Optional parameters
                            myIntent.putExtra("studentid", d.studentid); //Optional parameters
                            startActivity(myIntent);
                        }
                    });
                }
            }).start();
            return convertView;
        }

    }



}
