package com.example.bookaholic;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    int windowwidth;
    int screenCenter;
    Toolbar toolbar;
    int x_cord, y_cord, x, y;
    int Likes = 0;
    public RelativeLayout parentView;
    float alphaValue = 0;
    private Context context;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    ArrayList<UserDataModel> userDataModelArrayList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        db = FirebaseFirestore.getInstance();
        userDataModelArrayList = new ArrayList<>();
        db.collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserDataModel model = new UserDataModel();
                                model.setAname(document.get("aname").toString());
                                model.setBname(document.get("bname").toString());
                                model.setImage(document.get("image").toString());
                                model.setUid(document.get("uid").toString());
                                userDataModelArrayList.add(model);
                                Log.d("FData", document.getId() + " => " + userDataModelArrayList);
                            }
                            Collections.reverse(userDataModelArrayList);
                            Log.d("FData" ,userDataModelArrayList.size()+" =>" + userDataModelArrayList);



                            context = MainActivity.this;

                            parentView = (RelativeLayout) findViewById(R.id.main_layoutview);

                            windowwidth = getWindowManager().getDefaultDisplay().getWidth();

                            screenCenter = windowwidth / 6;
                            Log.d("FData" ,userDataModelArrayList.size()+" =>" + userDataModelArrayList);

                            for (int i = 0; i < userDataModelArrayList.size(); i++) {

                                LayoutInflater inflate =
                                        (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                final View containerView = inflate.inflate(R.layout.custom_layout, null);

                                ImageView userIMG = (ImageView) containerView.findViewById(R.id.userIMG);
                                RelativeLayout relativeLayoutContainer = (RelativeLayout) containerView.findViewById(R.id.relative_container);


                                LayoutParams layoutParams = new LayoutParams(
                                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

                                containerView.setLayoutParams(layoutParams);

                                containerView.setTag(i);
                                Picasso.get().load(userDataModelArrayList.get(i).getImage()).into(userIMG);
                                //userIMG.setBackgroundResource(userDataModelArrayList.get(i).getImage());


                                containerView.setPadding(0, i, 0, 0);

                                LayoutParams layoutTvParams = new LayoutParams(
                                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);


                                // ADD dynamically like TextView on image.
                                final TextView tvLike = new TextView(context);
                                tvLike.setLayoutParams(layoutTvParams);
                                tvLike.setPadding(10, 10, 10, 10);
                                tvLike.setBackgroundDrawable(getResources().getDrawable(R.drawable.btnlikeback));
                                tvLike.setText("LIKE");
                                tvLike.setGravity(Gravity.CENTER);
                                tvLike.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                tvLike.setTextSize(50);
                                tvLike.setTextColor(ContextCompat.getColor(context, R.color.colorProgressBar));
                                tvLike.setX(20);
                                tvLike.setY(100);
                                tvLike.setRotation(-50);
                                tvLike.setAlpha(alphaValue);
                                relativeLayoutContainer.addView(tvLike);


//            ADD dynamically dislike TextView on image.
                                final TextView tvUnLike = new TextView(context);
                                tvUnLike.setLayoutParams(layoutTvParams);
                                tvUnLike.setPadding(10, 10, 10, 10);
                                tvUnLike.setBackgroundDrawable(getResources().getDrawable(R.drawable.btnunlikeback));
                                tvUnLike.setText("UNLIKE");
                                tvUnLike.setGravity(Gravity.CENTER);
                                tvUnLike.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                tvUnLike.setTextSize(50);
                                tvUnLike.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                                tvUnLike.setX(550);
                                tvUnLike.setY(150);
                                tvUnLike.setRotation(50);
                                tvUnLike.setAlpha(alphaValue);
                                relativeLayoutContainer.addView(tvUnLike);


                                TextView tvName = (TextView) containerView.findViewById(R.id.tvName);
                                TextView tvTotLikes = (TextView) containerView.findViewById(R.id.tvTotalLikes);


                                tvName.setText(userDataModelArrayList.get(i).getAname());
                                tvTotLikes.setText(userDataModelArrayList.get(i).getBname());

                                // Touch listener on the image layout to swipe image right or left.
                                final int finalI1 = i;
                                relativeLayoutContainer.setOnTouchListener(new OnTouchListener() {

                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {

                                        x_cord = (int) event.getRawX();
                                        y_cord = (int) event.getRawY();

                                        containerView.setX(0);
                                        containerView.setY(0);

                                        switch (event.getAction()) {
                                            case MotionEvent.ACTION_DOWN:

                                                x = (int) event.getX();
                                                y = (int) event.getY();


                                                Log.v("On touch", x + " " + y);
                                                break;
                                            case MotionEvent.ACTION_MOVE:

                                                x_cord = (int) event.getRawX();
                                                // smoother animation.
                                                y_cord = (int) event.getRawY();

                                                containerView.setX(x_cord - x);
                                                containerView.setY(y_cord - y);


                                                if (x_cord >= screenCenter) {
                                                    containerView.setRotation((float) ((x_cord - screenCenter) * (Math.PI / 32)));
                                                    if (x_cord > (screenCenter + (screenCenter / 2))) {
                                                        tvLike.setAlpha(1);
                                                        if (x_cord > (windowwidth - (screenCenter / 4))) {
                                                            Likes = 2;
                                                        } else {
                                                            Likes = 0;
                                                        }
                                                    } else {
                                                        Likes = 0;
                                                        tvLike.setAlpha(0);
                                                    }
                                                    tvUnLike.setAlpha(0);
                                                } else {
                                                    // rotate image while moving
                                                    containerView.setRotation((float) ((x_cord - screenCenter) * (Math.PI / 32)));
                                                    if (x_cord < (screenCenter / 2)) {
                                                        tvUnLike.setAlpha(1);
                                                        if (x_cord < screenCenter / 4) {
                                                            Likes = 1;
                                                        } else {
                                                            Likes = 0;
                                                        }
                                                    } else {
                                                        Likes = 0;
                                                        tvUnLike.setAlpha(0);
                                                    }
                                                    tvLike.setAlpha(0);
                                                }

                                                break;
                                            case MotionEvent.ACTION_UP:

                                                x_cord = (int) event.getRawX();
                                                y_cord = (int) event.getRawY();

                                                Log.e("X Point", "" + x_cord + " , Y " + y_cord);
                                                tvUnLike.setAlpha(0);
                                                tvLike.setAlpha(0);

                                                if (Likes == 0) {
                                                    Toast.makeText(context, "NOTHING"+userDataModelArrayList.get(finalI1).getUid(), Toast.LENGTH_SHORT).show();
                                                    Log.e("Event_Status :-> ", "Nothing"+ userDataModelArrayList.get(finalI1).getUid());
                                                    containerView.setX(0);
                                                    containerView.setY(0);
                                                    containerView.setRotation(0);
                                                } else if (Likes == 1) {
                                                    Toast.makeText(context, "UNLIKE"+userDataModelArrayList.get(finalI1).getUid(), Toast.LENGTH_SHORT).show();
                                                    Log.e("Event_Status :-> ", "UNLIKE");
                                                    parentView.removeView(containerView);
                                                } else if (Likes == 2) {
                                                    Toast.makeText(context, "LIKED"+userDataModelArrayList.get(finalI1).getUid(), Toast.LENGTH_SHORT).show();
                                                    Log.e("Event_Status :-> ", "Liked");
                                                    parentView.removeView(containerView);
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                        return true;
                                    }
                                });


                                parentView.addView(containerView);

                            }

                        } else {
                            Log.w("FError", "Error getting documents.", task.getException());
                        }
                    }
                });
        //Log.d("FData" ,userDataModelArrayList.size()+" =>" + userDataModelArrayList);
        //Collections.reverse(userDataModelArrayList);

        setTitle("Bookaholic");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.chat) {

        }
        else if(item.getItemId() == R.id.profile) {
            Intent intent = new Intent(this,Profile.class);
            startActivity(intent);
        }
        return true;    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            Toast.makeText(MainActivity.this,"Login or Register",Toast.LENGTH_SHORT).show();
            change();
        }
    }
    private void change() {
        Intent intent = new Intent(this,login.class);
        startActivity(intent);
        finish();
    }
}
