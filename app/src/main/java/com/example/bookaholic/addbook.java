package com.example.bookaholic;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class addbook extends AppCompatActivity {

    Toolbar toolbar;
    ImageView imageView;
    Button add;
    TextInputEditText name,aname;
    FirebaseUser currentUser;
    StorageReference mStorageRef;
    FirebaseFirestore db;
    boolean im = false;
    Uri resultUri;
    String uid,ran;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbook);
        imageView = (ImageView) findViewById(R.id.image);
        add = (Button) findViewById(R.id.badd);
        name = (TextInputEditText) findViewById(R.id.name);
        aname = (TextInputEditText) findViewById(R.id.aname);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ran = random();
        uid = currentUser.getUid().toString();
        mStorageRef = FirebaseStorage.getInstance().getReference("Books").child(uid).child(uid + ran);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick();
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("CONTRIBUTE");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private void pick() {
        Intent gintent = new Intent();
        gintent.setType("image/*");
        gintent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(gintent.createChooser(gintent, "Select image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                imageView.setImageURI(resultUri);
                im = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error + " ", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public void p(View view) {

        final String Name,AName;
        Name = name.getText().toString();
        AName = aname.getText().toString();
        Toast.makeText(addbook.this, " Adding"  , Toast.LENGTH_SHORT).show();
        if (Name.isEmpty() || AName.isEmpty() || !im) {
            Toast.makeText(this, "Fill all the details ", Toast.LENGTH_SHORT).show();
        } else {
            mStorageRef.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(addbook.this, " Finishing" , Toast.LENGTH_SHORT).show();
                            final String url = uri.toString();
                            final Map<String, Object> post = new HashMap<>();
                            post.put("image", url);
                            post.put("bname", Name);
                            post.put("aname", AName);
                            post.put("uid", uid);
                            db.collection("Books").document(uid + ran).set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()) {
                                             Toast.makeText(addbook.this, "Book added", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(addbook.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                         }
                                       else {
                                            String error = task.getException().getMessage().toString();
                                            Toast.makeText(addbook.this, " database : " + error, Toast.LENGTH_SHORT).show();
                                        }
                                        }});
                        }
                    });
                }
                    });
                }
        }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(12);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
