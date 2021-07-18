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

public class AddStudentActivity extends AppCompatActivity {

    ArrayList<student> arraylist_student = new ArrayList<student>();
    myadapter mycustomadapter_student;

    ListView listview_student;
    EditText edittext_student_id, edittext_student_name,edittext_email,edittext_imagepath,edittext_mobile;

    Spinner spinnerdepartment;
    ArrayList<String> arraydepartments = new ArrayList<>();
    ArrayAdapter<String> adapter_departments ;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefstudent;
    DatabaseReference studentref;
    FirebaseStorage firebaseStorage;
    StorageReference mainrefstorage;

    String student_photopath="";
    String selected_department="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        listview_student = (ListView) (findViewById(R.id.listview_student));
        edittext_student_id = (EditText) (findViewById(R.id.edittext_student_id));
        edittext_student_name = (EditText) (findViewById(R.id.edittext_student_name));
        edittext_email = (EditText) (findViewById(R.id.edittext_email));
        edittext_mobile = (EditText) (findViewById(R.id.edittext_mobile));
        edittext_imagepath = (EditText) (findViewById(R.id.edittext_imagepath));

        spinnerdepartment = (Spinner) (findViewById(R.id.spinnerdepartment));
        adapter_departments = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arraydepartments);
        spinnerdepartment.setAdapter(adapter_departments);



        firebaseDatabase = FirebaseDatabase.getInstance(new firebase_cloud().getLink());
        mainrefstudent = firebaseDatabase.getReference();
        studentref =mainrefstudent.child("students");

        firebaseStorage = FirebaseStorage.getInstance();
        mainrefstorage = firebaseStorage.getReference();

        mycustomadapter_student= new myadapter();
        listview_student.setAdapter(mycustomadapter_student);
        fetchDepartmentsFromFirebase();

        spinnerdepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_department = arraydepartments.get(position);
                fetchstudentsFromFirebase(selected_department);
                Toast.makeText(getApplicationContext(),selected_department,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listview_student.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), arraylist_student.get(position).name+" "+arraylist_student.get(position).email, Toast.LENGTH_SHORT).show();
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

    public void fetchstudentsFromFirebase(String department_selected){
        arraylist_student.clear();
        studentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraylist_student.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    student studenttemp = singlesnapshot.getValue(student.class);
                    try {
                        if(studenttemp.under_dept.equals(department_selected)){
                            arraylist_student.add(studenttemp);
                        }
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
                Toast.makeText(getApplicationContext(),arraylist_student.size()+"",Toast.LENGTH_SHORT).show();
                mycustomadapter_student.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean checkDuplicateEntry (String studentid){
        boolean flag = true;
        for(int i=0; i<arraylist_student.size(); i++) {
            String single_student_id = arraylist_student.get(i).studentid;
            if (single_student_id.equals(studentid)){
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
            student_photopath =selectedImagePath;
            edittext_imagepath.setText(student_photopath);
            edittext_imagepath.setEnabled(false);
            Log.d("MYMESSAGE",selectedImagePath);
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
        String studentid =edittext_student_id.getText().toString();
        String name_student = edittext_student_name.getText().toString();
        String email_student = edittext_email.getText().toString();
        String mobile_student =edittext_mobile.getText().toString();
        if (studentid.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter student id",Toast.LENGTH_SHORT).show();
        }
        else  if (name_student.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter student name",Toast.LENGTH_SHORT).show();
        }
        else if(email_student.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter student email",Toast.LENGTH_SHORT).show();
        }
        else if (mobile_student.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter student mobile number",Toast.LENGTH_SHORT).show();
        }
        else if (student_photopath.isEmpty()){
            Toast.makeText(getApplicationContext(),"Choose student image",Toast.LENGTH_SHORT).show();
        }
        else{
            student student_object = new student(studentid,name_student,email_student,student_photopath+"/"+studentid,mobile_student,selected_department);
            DatabaseReference student_reference = studentref.child(studentid);
            Log.d("MYMESSAGE",student_reference.getKey());
            if(checkDuplicateEntry(studentid)) {
                student_reference.setValue(student_object);
                uploadlogic(student_photopath , studentid);
            }
            else{
                Toast.makeText(getApplicationContext(),"student with same ID in this department already exists",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadlogic(String path , String studentid)
    {
        File localfile=new File(path);
        final long uploadfilesize = localfile.length();
        StorageReference filerefoncloud = mainrefstorage.child("/students/"+student_photopath+"/"+studentid);
        UploadTask myuploadtask = filerefoncloud.putFile(Uri.fromFile(localfile));
        myuploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddStudentActivity.this, "New student Added ,Upload DONE !!!!", Toast.LENGTH_SHORT).show();
                //tv3.setText(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()+"");
                edittext_student_id.setText("");
                edittext_student_name.setText("");
                edittext_email.setText("");
                edittext_mobile.setText("");
                edittext_imagepath.setText("");
                student_photopath="";
                //fetchProfessorsFromFirebase(selected_department);
            }
        });
        myuploadtask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddStudentActivity.this, "New professor Upload Failed !!!", Toast.LENGTH_SHORT).show();
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
        StorageReference file11 = mainrefstorage.child("studentss/"+path);
        file11.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddStudentActivity.this, "Student Deleted ,File Deleted from Storage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class myadapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return arraylist_student.size();
        }

        @Override
        public Object getItem(int position) {
            return arraylist_student.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position*10;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null) {
                LayoutInflater l = LayoutInflater.from(getApplicationContext());
                convertView = l.inflate(R.layout.single_row_addstudent, parent, false);
            }
            TextView texview_student_name = (TextView) (convertView.findViewById(R.id.texview_student_name));
            TextView texview_student_email = (TextView) (convertView.findViewById(R.id.texview_student_email));
            TextView texview_student_mobile = (TextView) (convertView.findViewById(R.id.texview_student_mobile));
            Button btdelete =(Button)(convertView.findViewById(R.id.btdeletestudent));
            ImageView imv1student=(ImageView) (convertView.findViewById(R.id.imv1student));

            student p = arraylist_student.get(position);
            Log.d("TTHHGG",p.name+","+p.email+","+p.mobile);
            texview_student_name.setText("Name "+p.name);
            texview_student_email.setText("Email "+p.email);
            texview_student_mobile.setText("Mobile "+p.mobile);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference student_photo_reference = storageRef.child("students"+p.path);
            student_photo_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
                    //do something with downloadurl
                    Picasso.with(AddStudentActivity.this).load(downloadUrl).resize(200,200).into(imv1student);
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    btdelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            studentref.child(p.mobile).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                                        singleSnapshot.getRef().removeValue();
                                        deletefile(p.path);
                                        fetchstudentsFromFirebase(selected_department);
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
