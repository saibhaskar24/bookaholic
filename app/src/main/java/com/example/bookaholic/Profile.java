package com.example.bookaholic;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    CircleImageView image;
    FirebaseUser currentUser;
    StorageReference storageReference;
    FirebaseFirestore db ;
    Uri resultUri;
    boolean im = false;
    TextView name;
    String url,n;


    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Users").child(currentUser.getUid());
        name = (TextView) findViewById(R.id.name);
        image = (CircleImageView) findViewById(R.id.profile_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("Profile");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            assert  getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().exists()) {
                        n = task.getResult().getString("name");
                        url = task.getResult().getString("image");
                        name.setText(n);
                        Picasso.get().load(url).placeholder(R.drawable.profile).into(image);

                    }
                }
            }
        });
    }

    public void add_book(View view) {
        Intent intent = new Intent(this, addbook.class);
        startActivity(intent);
    }

    public void remove_book(View view) {
        Intent intent = new Intent(this, gallery.class);
        startActivity(intent);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        change();
    }
    private void change() {
        Intent intent = new Intent(this,login.class);
        startActivity(intent);
        finish();
    }
}
