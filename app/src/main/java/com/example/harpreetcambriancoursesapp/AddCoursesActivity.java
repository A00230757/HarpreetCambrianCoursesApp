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

public class AddCoursesActivity extends AppCompatActivity {

    ArrayList<course> arraylist_courses = new ArrayList<course>();
    myadapter mycustomadapter_courses;

    ListView listview_courses;
    EditText edittext_course_code,edittext_course_name,edittext_description,edittext_imagepath;

    Spinner spinnerdepartment , spinnerprofessor;
    ArrayList<String> arraydepartments = new ArrayList<>();
    ArrayAdapter<String> adapter_departments ;
    ArrayList<String> arrayprofessors = new ArrayList<>();
    ArrayAdapter<String> adapter_professor;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefcourse;
    DatabaseReference courseref;
    FirebaseStorage firebaseStorage;
    StorageReference mainrefstorage;

    String course_photopath="/storage/emulated/0/Pictures/Title (30).jpg/d1";
    String selected_department="";
    String selected_professor="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_courses);

        listview_courses = (ListView) (findViewById(R.id.listview_courses));
        edittext_course_code = (EditText) (findViewById(R.id.edittext_course_code));
        edittext_course_name = (EditText) (findViewById(R.id.edittext_course_name));
        edittext_description = (EditText) (findViewById(R.id.edittext_description));
        edittext_imagepath = (EditText) (findViewById(R.id.edittext_imagepath));

        spinnerdepartment = (Spinner) (findViewById(R.id.spinnerdepartment));
        adapter_departments = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arraydepartments);
        spinnerdepartment.setAdapter(adapter_departments);
        spinnerprofessor = (Spinner) (findViewById(R.id.spinnerprofessor));
        adapter_professor = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayprofessors);
        spinnerprofessor.setAdapter(adapter_professor);



        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefcourse = firebaseDatabase.getReference();
        courseref =mainrefcourse.child("courses");

        firebaseStorage = FirebaseStorage.getInstance();
        mainrefstorage = firebaseStorage.getReference();

        mycustomadapter_courses = new myadapter();
        listview_courses.setAdapter(mycustomadapter_courses);
        fetchDepartmentsFromFirebase();

        spinnerdepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_department = arraydepartments.get(position);
                if(!selected_department.equals("")){
                    arraylist_courses.clear();
                    mycustomadapter_courses.notifyDataSetChanged();
                    fetchProfessorsFromFirebase();
                }
                //fetchProfessorsFromFirebase();
                Toast.makeText(getApplicationContext(),selected_department,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerprofessor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_professor = arrayprofessors.get(position);
                fetchCoursesFromFirebase(selected_department,selected_professor);
                Toast.makeText(getApplicationContext(),selected_professor,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        listview_courses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), arraylist_courses.get(position).name+" "+arraylist_courses.get(position).description, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchDepartmentsFromFirebase(){
        arraydepartments.clear();
        DatabaseReference mainrefdepartment;
        DatabaseReference departmentref;
        mainrefdepartment = firebaseDatabase.getReference();
        departmentref =mainrefdepartment.child("departments");
        departmentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraydepartments.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    department depttemp = singlesnapshot.getValue(department.class);
                    try {
                        Log.d("MYESSAGE",singlesnapshot.getValue(department.class).name);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    arraydepartments.add(depttemp.name);
                }
                adapter_departments.notifyDataSetChanged();
                //fetchProfessorsFromFirebase();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fetchProfessorsFromFirebase(){
        arrayprofessors.clear();
        DatabaseReference mainrefprofessor;
        DatabaseReference professorref;
        mainrefprofessor = firebaseDatabase.getReference();
        professorref =mainrefprofessor.child("professors");
        professorref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayprofessors.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    professor proftemp = singlesnapshot.getValue(professor.class);
                    try {
                        Log.d("MYESSAGE",singlesnapshot.getValue(professor.class).name);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    if (proftemp.under_dept.equals(selected_department)){
                        arrayprofessors.add(proftemp.name);
                    }
                }
                adapter_professor.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void fetchCoursesFromFirebase(String department_selected ,String professor_selected){
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
                        if(coursetemp.under_dept.equals(department_selected)&& coursetemp.professor.equals(selected_professor)){
                            arraylist_courses.add(coursetemp);
                        }
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

    public boolean checkDuplicateEntry (String coursecode){
        boolean flag = true;
        for(int i=0; i<arraylist_courses.size(); i++) {
            String single_course_code = arraylist_courses.get(i).coursecode;
            if (single_course_code.equals(coursecode)){
                flag = false;
                break;
            }
        }
        return flag;
    }


    public void camera(View view)
    {
        Intent in  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(in,90);
    }

    public void gallery(View view)
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,91);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==90 && resultCode==RESULT_OK)
        {
            Bitmap bmp  = (Bitmap) data.getExtras().get("data");
        }
        else if(requestCode==91 && resultCode==RESULT_OK)
        {
            Uri uri = data.getData();
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(getApplicationContext(),selectedImageUri);
            System.out.println("Image Path : " + selectedImagePath);
            course_photopath =selectedImagePath;
            edittext_imagepath.setText(course_photopath);
            edittext_imagepath.setEnabled(false);
            Log.d("MYMESSAGE",course_photopath);
        }
    }

    public static String getPath( Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    public void add(View view)
    {
        String name_course = edittext_course_name.getText().toString();
        String code_course = edittext_course_name.getText().toString();
        String description_course = edittext_description.getText().toString();
        if (code_course.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter course code",Toast.LENGTH_SHORT).show();
        }
        else if (name_course.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter course name",Toast.LENGTH_SHORT).show();
        }
        else if(description_course.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter course description",Toast.LENGTH_SHORT).show();
        }
        else if (course_photopath.isEmpty()){
            Toast.makeText(getApplicationContext(),"Choose course image",Toast.LENGTH_SHORT).show();
        }
        else{
            course course_object = new course(code_course,name_course,description_course,course_photopath+"/"+name_course,selected_department, selected_professor);
            DatabaseReference course_reference = courseref.child(name_course);
            Log.d("MYMESSAGE",course_reference.getKey());
            if(checkDuplicateEntry(code_course)) {
                course_reference.setValue(course_object);
                uploadlogic(course_photopath , code_course);
            }
            else{
                Toast.makeText(getApplicationContext(),"course with same code in this department already exists",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadlogic(String path , String coursecode)
    {
        File localfile=new File(path);
        final long uploadfilesize = localfile.length();
        StorageReference filerefoncloud = mainrefstorage.child("/courses/"+course_photopath+"/"+coursecode);
        UploadTask myuploadtask = filerefoncloud.putFile(Uri.fromFile(localfile));
        myuploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddCoursesActivity.this, "New course Added ,Upload DONE !!!!", Toast.LENGTH_SHORT).show();
                //tv3.setText(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()+"");
                edittext_course_code.setText("");
                edittext_course_name.setText("");
                edittext_description.setText("");
                edittext_imagepath.setText("");
                course_photopath="";

                //fetchCoursesFromFirebase(selected_department);
            }
        });
        myuploadtask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddCoursesActivity.this, "New Course Upload Failed !!!", Toast.LENGTH_SHORT).show();
            }
        });
        myuploadtask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // int per = (int)((taskSnapshot.getBytesTransferred()*100)/uploadfilesize);
                //pbar2.setProgress(per);
            }
        });
    }

    public void deletefile(String path)
    {
        StorageReference file11 = mainrefstorage.child("courses/"+path);
        file11.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddCoursesActivity.this, "Course Deleted ,File Deleted from Storage", Toast.LENGTH_SHORT).show();
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
                convertView = l.inflate(R.layout.single_row_addcourse, parent, false);
            }
            TextView texview_course_name = (TextView) (convertView.findViewById(R.id.texview_course_name));
            TextView texview_course_description = (TextView) (convertView.findViewById(R.id.texview_course_description));
            TextView texview_course_photo = (TextView) (convertView.findViewById(R.id.texview_course_photo));
            Button btdelete =(Button)(convertView.findViewById(R.id.btdeletecourse));
            ImageView imv1course =(ImageView) (convertView.findViewById(R.id.imv1course));

            course d = arraylist_courses.get(position);
            texview_course_name.setText("Name "+d.name);
            texview_course_description.setText("Description "+d.description);
            texview_course_photo.setText("path "+d.path);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference course_photo_reference = storageRef.child("courses"+d.path);
            course_photo_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
                    //do something with downloadurl
                    Picasso.with(AddCoursesActivity.this).load(downloadUrl).resize(200,200).into(imv1course);
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    btdelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            courseref.child(d.name).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                                        singleSnapshot.getRef().removeValue();
                                        deletefile(d.path);
                                        fetchCoursesFromFirebase(selected_department,selected_professor);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    });
                }
            }).start();
            return convertView;
        }

    }

}
