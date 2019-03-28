package com.example.loginfirebase;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class NewPost extends AppCompatActivity {

    ImageView image;
    TextView title;
    Button selectImage,post;
    public static final int READ_EXTERNAL_STORAGE = 0;
    private  static final int GALLERY_INTENT = 2;
    private Firebase mRootRef;
    private Uri mImageUri = null;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mdatabaseRef;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        Firebase.setAndroidContext(this);

        image = (ImageView) findViewById(R.id.imageView);
        title = (TextView) findViewById(R.id.Title);
        selectImage = (Button) findViewById(R.id.selectButton);
        post = (Button) findViewById(R.id.Post);
        mProgressDialog = new ProgressDialog(NewPost.this);

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), "Call for permission", Toast.LENGTH_SHORT).show();
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                    }
                }
                else {
                    callGalley();
                }
            }
        });
        mdatabaseRef = FirebaseDatabase.getInstance().getReference();
        mRootRef = new Firebase("https://loginfirebase-ee7bd.firebaseio.com/").child("User_Details").push();
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://loginfirebase-ee7bd.appspot.com");
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mName = title.getText().toString().trim();
                if(mName.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter title", Toast.LENGTH_SHORT).show();
                    return;
                }
                Firebase childRef_name = mRootRef.child("Image_Title");
                childRef_name.setValue(mName);
                Toast.makeText(getApplicationContext(), "Update Info",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(NewPost.this, SignIn.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callGalley();
                break;
        }
        Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();
    }

    private void callGalley() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            image.setImageURI(mImageUri);
            StorageReference filePath = mStorage.child("User_Images").child(mImageUri.getLastPathSegment());

            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();

                    downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            mRootRef.child("Image_URL").setValue(downloadUri.toString());
                            Glide.with(getApplicationContext())
                                    .load(downloadUri)
                                    .crossFade()
                                    .placeholder(R.drawable.loading)
                                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                    .into(image);
                            Toast.makeText(getApplicationContext(), "Updated...", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    });


                }
            });
        }
    }

}
