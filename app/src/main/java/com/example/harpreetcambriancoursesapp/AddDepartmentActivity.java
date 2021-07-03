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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

public class AddDepartmentActivity extends AppCompatActivity {

    ArrayList<department> arraylist_departments = new ArrayList<department>();
    myadapter mycustomadapter_departments;

    ListView listview_departments;
    EditText edittext_department_name,edittext_description,edittext_imagepath;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mainrefdepartment;
    DatabaseReference departmentref;
    FirebaseStorage firebaseStorage;
    StorageReference mainrefstorage;

    String department_photopath="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_department);

        listview_departments = (ListView) (findViewById(R.id.listview_departments));
        edittext_department_name = (EditText) (findViewById(R.id.edittext_department_name));
        edittext_description = (EditText) (findViewById(R.id.edittext_description));
        edittext_imagepath = (EditText) (findViewById(R.id.edittext_imagepath));

        firebaseDatabase = FirebaseDatabase.getInstance("https://harpreetcambriancoursesapp-default-rtdb.firebaseio.com/");
        mainrefdepartment = firebaseDatabase.getReference();
        departmentref =mainrefdepartment.child("departments");

        firebaseStorage = FirebaseStorage.getInstance();
        mainrefstorage = firebaseStorage.getReference();

        mycustomadapter_departments = new myadapter();
        listview_departments.setAdapter(mycustomadapter_departments);
        fetchDepartmentsFromFirebase();

        listview_departments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), arraylist_departments.get(position).name+" "+arraylist_departments.get(position).description, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchDepartmentsFromFirebase(){
        arraylist_departments.clear();
        departmentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraylist_departments.clear();
                //Log.d("MYESSAGE",dataSnapshot.toString());
                for(DataSnapshot  singlesnapshot : dataSnapshot.getChildren())
                {
                    department depttemp = singlesnapshot.getValue(department.class);
                    try {
                        //Log.d("MYESSAGE",singlesnapshot.getValue(department.class));
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    arraylist_departments.add(depttemp);
                }
                mycustomadapter_departments.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean checkDuplicateEntry (String deptname){
        boolean flag = true;
        for(int i=0; i<arraylist_departments.size(); i++) {
            String single_department_name = arraylist_departments.get(i).name;
            if (single_department_name.equals(deptname)){
                flag = false;
                break;
            }
        }
        return flag;
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
                department_photopath =finalFile.getAbsolutePath().toString();
                edittext_imagepath.setText(department_photopath);
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
        String name_department = edittext_department_name.getText().toString();
        String description_department = edittext_description.getText().toString();
        if (name_department.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter department name",Toast.LENGTH_SHORT).show();
        }
        else if(description_department.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter department description",Toast.LENGTH_SHORT).show();
        }
        else if (department_photopath.isEmpty()){
            Toast.makeText(getApplicationContext(),"Choose department image",Toast.LENGTH_SHORT).show();
        }
        else{
            department department_object = new department(name_department,description_department,department_photopath+"/"+name_department);
            DatabaseReference deparmtment_reference = departmentref.child(name_department);
            Log.d("MYMESSAGE",deparmtment_reference.getKey());
            if(checkDuplicateEntry(name_department)) {
                deparmtment_reference.setValue(department_object);
                uploadlogic(department_photopath , name_department);
            }
            else{
                Toast.makeText(getApplicationContext(),"Department with same name already exists",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadlogic(String path , String deptname)
    {
        File localfile=new File(path);
        final long uploadfilesize = localfile.length();
        StorageReference filerefoncloud = mainrefstorage.child("/departments/"+department_photopath+"/"+deptname);
        UploadTask myuploadtask = filerefoncloud.putFile(Uri.fromFile(localfile));
        myuploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddDepartmentActivity.this, "New department Added ,Upload DONE !!!!", Toast.LENGTH_SHORT).show();
                //tv3.setText(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()+"");
                edittext_department_name.setText("");
                edittext_description.setText("");
                edittext_imagepath.setText("");
                department_photopath="";
                fetchDepartmentsFromFirebase();
            }
        });
        myuploadtask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddDepartmentActivity.this, "New Department Upload Failed !!!", Toast.LENGTH_SHORT).show();
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
        StorageReference file11 = mainrefstorage.child("departments/"+path);
        file11.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddDepartmentActivity.this, "Department Deleted ,File Deleted from Storage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class myadapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return arraylist_departments.size();
        }

        @Override
        public Object getItem(int position) {
            return arraylist_departments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position*10;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null) {
                LayoutInflater l = LayoutInflater.from(getApplicationContext());
                convertView = l.inflate(R.layout.single_row_adddepartment, parent, false);
            }
            TextView texview_department_name = (TextView) (convertView.findViewById(R.id.texview_department_name));
            TextView texview_department_description = (TextView) (convertView.findViewById(R.id.texview_department_description));
            TextView texview_department_photo = (TextView) (convertView.findViewById(R.id.texview_department_photo));
            Button btdelete =(Button)(convertView.findViewById(R.id.btdeletedept));
            ImageView imv1dept =(ImageView) (convertView.findViewById(R.id.imv1dept));

            department d = arraylist_departments.get(position);
            texview_department_name.setText("Name "+d.name);
            texview_department_description.setText("Description "+d.description);
            texview_department_photo.setText("path "+d.path);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference department_photo_reference = storageRef.child("departments"+d.path);
            department_photo_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri downloadUrl)
                {
                    //do something with downloadurl
                    Picasso.with(AddDepartmentActivity.this).load(downloadUrl).resize(200,200).into(imv1dept);
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    btdelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            departmentref.child(d.name).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                                        singleSnapshot.getRef().removeValue();
                                        deletefile(d.path);
                                        fetchDepartmentsFromFirebase();
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
