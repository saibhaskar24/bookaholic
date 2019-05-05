package com.example.bookaholic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import com.squareup.picasso.Callback;

public class gallery extends AppCompatActivity {

    GridView androidGridView;
    FirebaseFirestore db;
    String currentuser;
    ArrayList<String> images;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        db = FirebaseFirestore.getInstance();
        androidGridView = (GridView) findViewById(R.id.gridview_android_example);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("My Books");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            assert  getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        images =  new ArrayList<>();
        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        db.collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get("uid").toString().equals(currentuser)) {
                                    images.add(document.get("image").toString());
                                }
                                Log.d("Grid : ", images + " ");
                                androidGridView.setAdapter(new ImageAdapterGridView(gallery.this));
                                androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    public void onItemClick(AdapterView<?> parent,
                                                            View v, int position, long id) {
                                        Toast.makeText(getBaseContext(), "Grid Item " + (position + 1) + " Selected", Toast.LENGTH_LONG).show();
                                    }
                                });}
                        } else {
                            Log.w("FError", "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    public class ImageAdapterGridView extends BaseAdapter {
        Context mContext;
        LayoutInflater inflater;

        public ImageAdapterGridView(Context c) {
            mContext = c;
            inflater = LayoutInflater.from(c);
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.item_grid_image, parent, false);
                holder = new ViewHolder();
                assert view != null;

                holder.imageView = (ImageView) view.findViewById(R.id.image);

                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Picasso.get()
                    .load(images.get(position))
                    .placeholder(R.drawable.loading)
                    .fit()
                    .into(holder.imageView, new Callback() {

                        @Override
                        public void onSuccess() {
                            holder.imageView.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.progressBar.setVisibility(View.VISIBLE);
                            holder.imageView.setVisibility(View.INVISIBLE);
                            String er = e.getMessage().toString();
                            Toast.makeText(gallery.this, "Error : "+er,Toast.LENGTH_LONG).show();
                        }
                    });

            return view;
        }
    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }
}
