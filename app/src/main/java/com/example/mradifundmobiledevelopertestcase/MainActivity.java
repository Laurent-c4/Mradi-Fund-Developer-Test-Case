package com.example.mradifundmobiledevelopertestcase;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;
    private int REQUEST_CODE_PERMISSIONS = 10;
    private String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};

    private StorageReference mStorageReference;
    private DatabaseReference mUploadsReference;
    private DatabaseReference mPasswordRef;
    private String userId;

    private  NavController navController;

    private ProgressBar progressBar;

    public static Uri mPDFUri=null;

    public static LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<DataPoint>();
    public static Double maxY = 0.0;
    public static String uploadedFilename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("  Home");
        toolbar.setLogo(R.drawable.ic_actionbar_logo);
        setSupportActionBar(toolbar);


        navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration=
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController);

        progressBar = findViewById(R.id.determinateBar);

        mStorageReference = FirebaseStorage.getInstance().getReference("Uploads");
        mUploadsReference = FirebaseDatabase.getInstance().getReference("Uploads");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        mPasswordRef = FirebaseDatabase.getInstance().getReference("Passwords").child(userId);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


        // CHECK IF USER HAS SET ID NUMBER, WHICH IS ESSENTIAL FOR DECRYPTING PDF BY THE BACKEND API
        mPasswordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for(DataSnapshot locationSnapshot:dataSnapshot.getChildren()){
                    count++;
                }

                if(count==0) {
                    FragmentManager fm = getSupportFragmentManager();
                    IDNumberFragment idNumberFragment = new IDNumberFragment();
                    idNumberFragment.show(fm, "IDFragment");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(MainActivity.this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK
               && data != null && data.getData() != null){
            mPDFUri = data.getData();

            if (!getFileName(mPDFUri).contains("MPESA_Statement")){
                Toast.makeText(MainActivity.this,"Invalid. Please Choose an MPESA Statement", Toast.LENGTH_LONG).show();
            } else {

                if(allPermissionsGranted()) {
                    // CHECK IF USER HAS SET ID NUMBER, WHICH IS ESSENTIAL FOR DECRYPTING PDF BY THE BACKEND API
                    mPasswordRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int count = 0;
                            for(DataSnapshot locationSnapshot:dataSnapshot.getChildren()){
                                count++;
                            }

                            if(count==0) {
                                FragmentManager fm = getSupportFragmentManager();
                                IDNumberFragment idNumberFragment = new IDNumberFragment();
                                idNumberFragment.show(fm, "IDFragment");
                            } else {
                                // CHECK IF USER HAS SET ID NUMBER, WHICH IS ESSENTIAL FOR DECRYPTING PDF BY THE BACKEND API
                                mPasswordRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int count = 0;
                                        for(DataSnapshot locationSnapshot:dataSnapshot.getChildren()){
                                            count++;
                                        }

                                        if(count==0) {
                                            FragmentManager fm = getSupportFragmentManager();
                                            IDNumberFragment idNumberFragment = new IDNumberFragment();
                                            idNumberFragment.show(fm, "IDFragment");
                                        } else {
                                            uploadFile();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else {
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()) {
                // CHECK IF USER HAS SET ID NUMBER, WHICH IS ESSENTIAL FOR DECRYPTING PDF BY THE BACKEND API
                mPasswordRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int count = 0;
                        for(DataSnapshot locationSnapshot:dataSnapshot.getChildren()){
                            count++;
                        }

                        if(count==0) {
                            FragmentManager fm = getSupportFragmentManager();
                            IDNumberFragment idNumberFragment = new IDNumberFragment();
                            idNumberFragment.show(fm, "IDFragment");
                        } else {
                            // CHECK IF USER HAS SET ID NUMBER, WHICH IS ESSENTIAL FOR DECRYPTING PDF BY THE BACKEND API
                            mPasswordRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int count = 0;
                                    for(DataSnapshot locationSnapshot:dataSnapshot.getChildren()){
                                        count++;
                                    }

                                    if(count==0) {
                                        FragmentManager fm = getSupportFragmentManager();
                                        IDNumberFragment idNumberFragment = new IDNumberFragment();
                                        idNumberFragment.show(fm, "IDFragment");
                                    } else {
                                        uploadFile();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(this, "Permissions not granted by user ", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    public String getFileName(Uri uri) {
        ContentResolver cR = getContentResolver();
        Cursor returnCursor = cR.query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String fileName = returnCursor.getString(nameIndex);
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
        uploadedFilename = fileName;
        return fileName;
    }

    private void uploadFile(){
        if (mPDFUri != null){

            //UPLOAD PDF FILE TO FIREBASE STORAGE
            StorageReference fileReference = mStorageReference.child(userId+"/"+getFileName(mPDFUri));
            progressBar.setVisibility(View.VISIBLE);
            fileReference.putFile(mPDFUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }, 3000);

                            Toast.makeText(MainActivity.this,"Upload Succesful", Toast.LENGTH_LONG).show();

                            //UPDATE DATABASE WITH NEW PDF DETAILS
                            Upload upload = new Upload(getFileName(mPDFUri),taskSnapshot.getUploadSessionUri().toString());
                            mUploadsReference.child(userId).push().setValue(upload);


                            // POST REQUEST TO BACKEND API CONTAINING NEW PDF WHOSE DATA SHOULD BE EXTRACTED
                            File file = new File(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DOWNLOADS),getFileName(mPDFUri));

                            RequestBody requestBody = RequestBody.create(MediaType.parse("application/pdf"),file);
                            MultipartBody.Part part = MultipartBody.Part.createFormData("files[]",file.getName(), requestBody);

                            RequestBody description = RequestBody.create(MediaType.parse("text/plain"),"New PDF");

                            Retrofit retrofit = NetworkClient.getRetrofit();
                            UploadApi uploadApi = retrofit.create(UploadApi.class);
                            Call call = uploadApi.uploadPDF(description, part, userId);
                            call.enqueue(new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) {
//                                    DatabaseReference mUserExpenditureListReference;
//                                    mUserExpenditureListReference = FirebaseDatabase.getInstance().getReference("Expenditure_List").child(userId).child(getFileName(mPDFUri).replaceAll("[^A-Za-z0-9]",""));
//                                    mUserExpenditureListReference.addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                                            Map<String, List<Expenditure>> cache = new HashMap<>();
////                                            List<Expenditure> expenditureList = new ArrayList<>();
////                                            for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()){
////                                                Expenditure expenditure = locationSnapshot.getValue(Expenditure.class);
////
////
////                                                //Group same-day expenditure in hash map
////                                                List<Expenditure> list = cache.get(expenditure.getDate().substring(8,10));
////                                                if (list == null) {
////                                                    list = new ArrayList<>();
////                                                    cache.put(expenditure.getDate().substring(8,10),list);
////                                                }
////                                                list.add(expenditure);
////
////                                            }
////
////                                            // Function to sort map by Key
////                                            // TreeMap to store values of HashMap
////                                            TreeMap<String, List<Expenditure>> sorted = new TreeMap<>();
////
////                                            // Copy all data from hashMap into TreeMap
////                                            sorted.putAll(cache);
////
////                                            // Display the TreeMap which is naturally sorted
////                                            for (Map.Entry<String, List<Expenditure>> entry : sorted.entrySet()){
////
////                                                Double totalSpent = 0.0;
////
////                                                for (int i = 0; i < entry.getValue().size(); i++) {
////                                                    totalSpent += entry.getValue().get(i).getSpent();
////                                                }
////
////
////
////                                                lineGraphSeries.appendData(new DataPoint(Integer.parseInt(entry.getKey()), totalSpent),true,cache.entrySet().size());
////                                                if (totalSpent > maxY){
////                                                    maxY = totalSpent + 2000;
////                                                }
////                                            }
////
////                                            navController.navigate(R.id.lineGraphFragment);
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });
                                    navController.navigate(R.id.barGraphFragment);
                                }

                                @Override
                                public void onFailure(Call call, Throwable t) {
                                    Log.e("On Failure", t.getMessage());
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int)progress);
                        }
                    });
        } else {
            Toast.makeText(this,"No File Selected", Toast.LENGTH_LONG).show();
        }
    }

}
