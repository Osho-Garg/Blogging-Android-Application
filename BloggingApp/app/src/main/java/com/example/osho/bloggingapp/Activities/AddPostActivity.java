package com.example.osho.bloggingapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;



import com.example.osho.bloggingapp.R;


import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class AddPostActivity extends AppCompatActivity {


    private ImageButton mPostImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitButton;


    private StorageReference mStorage;
    private DatabaseReference mPostDatabase;


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgress;
    private Uri mImageUri;
    private static final int GALLERY_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);


        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();


        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("MBlog");

        mPostImage = (ImageButton) findViewById(R.id.imageID);
        mPostTitle = (EditText) findViewById(R.id.titleID);
        mPostDesc = (EditText) findViewById(R.id.descriptionID);
        mSubmitButton = (Button) findViewById(R.id.submitPostID);


        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });


        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Posting to our database
                startPosting();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            mPostImage.setImageURI(mImageUri);


        }
    }


    private void startPosting() {


        mProgress.setMessage("Posting to blog...");
        mProgress.show();

        final String titleVal = mPostTitle.getText().toString().trim();
        final String descVal = mPostDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) &&
        mImageUri != null){
           //String thumb_download_url;
            //start the uploading...
            // images will be store in "Storage" in firebase
            // we can reference of that images in "Database" in Firebase

            //mImageUri.getLastPathSegment() == /image/myphoto.jpeg"

            final StorageReference filepath = mStorage.child("MBlog_images").
                    child(mImageUri.getLastPathSegment());


            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                    //Uri downloadUrl = taskSnapshot.getDownloadUrl(); //ERROR STATEMENT






                    DatabaseReference newPost = mPostDatabase.push();


                    Map<String, String> dataToSave = new HashMap<>();
                    dataToSave.put("title", titleVal);
                    dataToSave.put("desc", descVal);
                    //dataToSave.put("image", downloadUrl.toString()); // ERROR STATEMENT due to which i am not ab
                    dataToSave.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    dataToSave.put("userid", mUser.getUid());
                    dataToSave.put("username", mUser.getEmail());

                    newPost.setValue(dataToSave);


                    //Old way
//                    newPost.child("title").setValue(titleVal);
//                    newPost.child("desc").setValue(descrVal);
//                    newPost.child("image").setValue(downloadUrl.toString());
//                    newPost.child("timestamp").setValue(java.lang.System.currentTimeMillis());

                    mProgress.dismiss();




                }

            });

        }
    }
}


