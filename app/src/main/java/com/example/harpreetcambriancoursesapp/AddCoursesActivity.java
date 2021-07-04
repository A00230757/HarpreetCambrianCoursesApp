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
    EditText edittext_course_name,edittext_description,edittext_imagepath;

    Spinner spinnerdepartment;
    ArrayList<String> arraydepartments = new ArrayList<>();
    ArrayAdapter<String> adapter_departments ;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefcourse;
    DatabaseReference courseref;
    FirebaseStorage firebaseStorage;
    StorageReference mainrefstorage;

    String course_photopath="/storage/emulated/0/Pictures/Title (30).jpg/d1";
    String selected_department="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_courses);

        listview_courses = (ListView) (findViewById(R.id.listview_courses));
        edittext_course_name = (EditText) (findViewById(R.id.edittext_course_name));
        edittext_description = (EditText) (findViewById(R.id.edittext_description));
        edittext_imagepath = (EditText) (findViewById(R.id.edittext_imagepath));

        spinnerdepartment = (Spinner) (findViewById(R.id.spinnerdepartment));
        adapter_departments = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arraydepartments);
        spinnerdepartment.setAdapter(adapter_departments);



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
                fetchCoursesFromFirebase(selected_department);
                Toast.makeText(getApplicationContext(),selected_department,Toast.LENGTH_SHORT).show();

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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                        if(coursetemp.under_dept.equals(department_selected)){
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

    public boolean checkDuplicateEntry (String coursename){
        boolean flag = true;
        for(int i=0; i<arraylist_courses.size(); i++) {
            String single_course_name = arraylist_courses.get(i).name;
            if (single_course_name.equals(coursename)){
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
        Intent in = new Intent(Intent.ACTION_GET_CONTENT);
        in.setType("image/*");
        startActivityForResult(in,91);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==90 && resultCode==RESULT_OK)
        {
            Bitmap bmp  = (Bitmap) data.getExtras().get("data");
//Uri tempUri = getImageUri(getApplicationContext(), bmp);
//            File finalFile = new File(getRealPathFromURI(tempUri));
//            department_photopath =finalFile.getAbsolutePath().toString();
//            edittext_imagepath.setText(department_photopath);
//            edittext_imagepath.setEnabled(false);
//            Log.d("MYMESSAGE",finalFile.getAbsolutePath().toString());
            //imv1.setImageBitmap(bmp);
        }
        else if(requestCode==91 && resultCode==RESULT_OK)
        {
            Uri uri = data.getData();
            try {
                Bitmap bmp=MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), bmp);
                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));
                course_photopath =finalFile.getAbsolutePath().toString();
                edittext_imagepath.setText(course_photopath);
                edittext_imagepath.setEnabled(false);
                Log.d("MYMESSAGE",finalFile.getAbsolutePath().toString());



            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void add(View view)
    {
        String name_course = edittext_course_name.getText().toString();
        String description_course = edittext_description.getText().toString();
        if (name_course.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter course name",Toast.LENGTH_SHORT).show();
        }
        else if(description_course.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter course description",Toast.LENGTH_SHORT).show();
        }
        else if (course_photopath.isEmpty()){
            Toast.makeText(getApplicationContext(),"Choose course image",Toast.LENGTH_SHORT).show();
        }
        else{
            course course_object = new course(name_course,description_course,course_photopath+"/"+name_course,selected_department);
            DatabaseReference course_reference = courseref.child(name_course);
            Log.d("MYMESSAGE",course_reference.getKey());
            if(checkDuplicateEntry(name_course)) {
                course_reference.setValue(course_object);
                uploadlogic(course_photopath , name_course);
            }
            else{
                Toast.makeText(getApplicationContext(),"course with same name in this department already exists",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadlogic(String path , String coursename)
    {
        File localfile=new File(path);
        final long uploadfilesize = localfile.length();
        StorageReference filerefoncloud = mainrefstorage.child("/courses/"+course_photopath+"/"+coursename);
        UploadTask myuploadtask = filerefoncloud.putFile(Uri.fromFile(localfile));
        myuploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddCoursesActivity.this, "New course Added ,Upload DONE !!!!", Toast.LENGTH_SHORT).show();
                //tv3.setText(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()+"");
                edittext_course_name.setText("");
                edittext_description.setText("");
                edittext_imagepath.setText("");
                course_photopath="";
                fetchCoursesFromFirebase(selected_department);
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
                                        fetchCoursesFromFirebase(selected_department);
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
