package com.example.mradifundmobiledevelopertestcase;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private String userId;

    private ProgressBar progressBar;

    private Uri mPDFUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.determinateBar);

        mStorageReference = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
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
                uploadFile();
            }
        }
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    private  String getFileName(Uri uri) {
        ContentResolver cR = getContentResolver();
        Cursor returnCursor = cR.query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String fileName = returnCursor.getString(nameIndex);
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return fileName;
    }

    private void uploadFile(){
        if (mPDFUri != null){
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
                            Upload upload = new Upload(getFileName(mPDFUri),taskSnapshot.getUploadSessionUri().toString());
                            mDatabaseReference.child(userId).push().setValue(upload);

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
